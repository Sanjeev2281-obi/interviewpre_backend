package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class MockService {

    private final MockSessionRepository mockSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final QuestionSubmissionRepository questionSubmissionRepository;
    private final SessionEvaluationRepository sessionEvaluationRepository;
    private final UserUsageTrackingRepository userUsageTrackingRepository;
    private final WeakTopicAnalysisRepository weakTopicAnalysisRepository;
    private final ClaudeService claudeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MockService(
            MockSessionRepository mockSessionRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            QuestionSubmissionRepository questionSubmissionRepository,
            SessionEvaluationRepository sessionEvaluationRepository,
            UserUsageTrackingRepository userUsageTrackingRepository,
            WeakTopicAnalysisRepository weakTopicAnalysisRepository,
            ClaudeService claudeService) {
        this.mockSessionRepository = mockSessionRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.questionSubmissionRepository = questionSubmissionRepository;
        this.sessionEvaluationRepository = sessionEvaluationRepository;
        this.userUsageTrackingRepository = userUsageTrackingRepository;
        this.weakTopicAnalysisRepository = weakTopicAnalysisRepository;
        this.claudeService = claudeService;
    }

    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public UsageStatsDto getUsage(User user) {
        LocalDate startOfWeek = getStartOfWeek(LocalDate.now());
        UserUsageTracking tracking = userUsageTrackingRepository
                .findByUserIdAndWeekStart(user.getId(), startOfWeek)
                .orElseGet(() -> new UserUsageTracking(user, startOfWeek, 0));

        int limit = user.getRole().equals("PRO") ? Integer.MAX_VALUE : 1;
        int used = tracking.getWeeklyCount();
        int remaining = Math.max(0, limit - used);
        String resetsAt = startOfWeek.plusWeeks(1).atStartOfDay().toString() + ":00Z";

        return new UsageStatsDto(used, limit == Integer.MAX_VALUE ? 9999 : limit, remaining, resetsAt);
    }

    @Transactional
    public Map<String, Object> generateSession(User user, GenerateMockRequest request) {
        // 1. Enforce limits on FREE users
        boolean isPro = user.getRole().equals("PRO");
        LocalDate startOfWeek = getStartOfWeek(LocalDate.now());
        UserUsageTracking tracking = userUsageTrackingRepository
                .findByUserIdAndWeekStart(user.getId(), startOfWeek)
                .orElseGet(() -> {
                    UserUsageTracking t = new UserUsageTracking(user, startOfWeek, 0);
                    return userUsageTrackingRepository.save(t);
                });

        if (!isPro) {
            if (tracking.getWeeklyCount() >= 1) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Weekly mock interview limit reached. Upgrade to PRO for unlimited access!");
            }
            // FREE user locks
            request.setCompany("Any");
        }

        // 2. Build system and user prompts for Claude
        String difficultyMix = isPro ? "1 easy, 1 medium, 1 hard" : "1 easy, 2 medium";
        String topics = String.join(", ", request.getTopics());

        String systemPrompt = "You are an expert technical interviewer at a top tech company.\n" +
                "Generate a mock interview question set in ONLY valid JSON. No markdown. No backticks. No explanation.\n" +
                "Return exactly this shape:\n" +
                "{\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"type\": \"dsa\",\n" +
                "      \"title\": \"Problem title\",\n" +
                "      \"difficulty\": \"easy|medium|hard\",\n" +
                "      \"topic\": \"Arrays|Strings|Trees|Graphs|DP|Stack|Queue|LinkedList|BinarySearch|Greedy|Hashing|Recursion\",\n" +
                "      \"tag\": \"Two Pointers\",\n" +
                "      \"company\": \"" + request.getCompany() + "\",\n" +
                "      \"desc\": \"Clear problem description using real-world context (e.g. delivery routes, social feeds)\",\n" +
                "      \"example\": \"Input: ...\\nOutput: ...\\nExplanation: ...\",\n" +
                "      \"constraints\": [\"1 <= nums.length <= 10^5\", \"Values are distinct\"],\n" +
                "      \"hints\": [\"hint 1\", \"hint 2\", \"hint 3\"],\n" +
                "      \"testCases\": [\n" +
                "        { \"id\": 1, \"input\": \"...\", \"expected\": \"...\", \"visible\": true },\n" +
                "        { \"id\": 2, \"input\": \"...\", \"expected\": \"...\", \"visible\": true },\n" +
                "        { \"id\": 3, \"input\": \"...\", \"expected\": \"...\", \"visible\": true },\n" +
                "        { \"id\": 4, \"input\": \"...\", \"expected\": \"...\", \"hidden\": true },\n" +
                "        { \"id\": 5, \"input\": \"...\", \"expected\": \"...\", \"hidden\": true }\n" +
                "      ],\n" +
                "      \"starterCode\": {\n" +
                "        \"javascript\": \"...\",\n" +
                "        \"python\": \"...\",\n" +
                "        \"java\": \"...\",\n" +
                "        \"cpp\": \"...\"\n" +
                "      },\n" +
                "      \"complexity\": { \"time\": \"O(n)\", \"space\": \"O(1)\" },\n" +
                "      \"timeLimit\": 20\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "For behavioral questions, omit starterCode, complexity, testCases, constraints, and example, and instead return: desc, timeLimit, starMethod[] (array of strings guide), rubric[] (array of grading criteria), hints[] (helpful hints).";

        if (request.getType().equals("DSA Only")) {
            systemPrompt += "\nGenerate exactly 5 DSA questions matching this difficulty distribution: " + difficultyMix + " (e.g., 2 easy, 2 medium, 1 hard/medium-hard).";
        } else if (request.getType().equals("Behavioral")) {
            systemPrompt += "\nGenerate exactly 5 behavioral questions.";
        } else {
            systemPrompt += "\nGenerate exactly 5 questions: 3 DSA questions (difficulty: " + difficultyMix + ") and 2 behavioral questions.";
        }

        String userPrompt = "Generate 5 mock interview questions for a " + request.getLevel() + " developer targeting " + request.getCompany() + " interviews, focusing on " + topics + ".\n" +
                "Make questions original and different from standard LeetCode problems.";

        // 3. Call Claude API & Parse
        String responseText = "";
        JsonNode rootNode = null;
        int parseAttempts = 0;
        while (parseAttempts < 2) {
            try {
                parseAttempts++;
                responseText = claudeService.generateQuestions(systemPrompt, userPrompt);
                rootNode = objectMapper.readTree(responseText);
                if (rootNode.has("questions") && rootNode.path("questions").isArray() && rootNode.path("questions").size() > 0) {
                    break;
                }
            } catch (Exception e) {
                if (parseAttempts >= 2) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse questions generated from Claude API. Please retry.", e);
                }
            }
        }

        // 4. Save Session
        MockSession session = new MockSession(user, request.getCompany(), request.getType(), request.getLevel(), "RUNNING");
        MockSession savedSession = mockSessionRepository.save(session);

        // 5. Save Questions
        List<Map<String, Object>> responseQuestions = new ArrayList<>();
        JsonNode questionsNode = rootNode.path("questions");
        for (JsonNode qNode : questionsNode) {
            InterviewQuestion q = new InterviewQuestion();
            q.setSession(savedSession);
            q.setQuestionType(qNode.path("type").asText("dsa"));
            q.setDifficulty(qNode.path("difficulty").asText("medium"));
            q.setTopic(qNode.path("topic").asText("General"));
            q.setTitle(qNode.path("title").asText("Mock Question"));
            q.setDescription(qNode.path("desc").asText(""));
            q.setCompany(qNode.path("company").asText(request.getCompany()));

            if (q.getQuestionType().equals("dsa")) {
                q.setExpectedComplexity(qNode.path("complexity").toString());
                q.setExample(qNode.path("example").asText(""));
                q.setConstraintsJson(qNode.path("constraints").toString());
                q.setHintsJson(qNode.path("hints").toString());
                q.setTestCasesJson(qNode.path("testCases").toString());
                q.setStarterCodeJson(qNode.path("starterCode").toString());
            } else {
                q.setHintsJson(qNode.path("hints").toString());
                q.setStarMethodJson(qNode.path("starMethod").toString());
                q.setRubricJson(qNode.path("rubric").toString());
            }

            InterviewQuestion savedQ = interviewQuestionRepository.save(q);

            // Create client response map
            Map<String, Object> qMap = new HashMap<>();
            qMap.put("id", savedQ.getId().toString());
            qMap.put("type", savedQ.getQuestionType());
            qMap.put("difficulty", savedQ.getDifficulty());
            qMap.put("topic", savedQ.getTopic());
            qMap.put("title", savedQ.getTitle());
            qMap.put("desc", savedQ.getDescription());
            qMap.put("timeLimit", qNode.path("timeLimit").asInt(20));
            if (savedQ.getQuestionType().equals("dsa")) {
                qMap.put("example", savedQ.getExample());
                try {
                    qMap.put("constraints", objectMapper.readValue(savedQ.getConstraintsJson(), List.class));
                    qMap.put("hints", objectMapper.readValue(savedQ.getHintsJson(), List.class));
                    qMap.put("testCases", objectMapper.readValue(savedQ.getTestCasesJson(), List.class));
                    qMap.put("starterCode", objectMapper.readValue(savedQ.getStarterCodeJson(), Map.class));
                    qMap.put("complexity", objectMapper.readValue(savedQ.getExpectedComplexity(), Map.class));
                } catch (Exception ignored) {}
            } else {
                try {
                    qMap.put("hints", objectMapper.readValue(savedQ.getHintsJson(), List.class));
                    qMap.put("starMethod", objectMapper.readValue(savedQ.getStarMethodJson(), List.class));
                    qMap.put("rubric", objectMapper.readValue(savedQ.getRubricJson(), List.class));
                } catch (Exception ignored) {}
            }
            responseQuestions.add(qMap);
        }

        // 6. Increment Usage
        tracking.setWeeklyCount(tracking.getWeeklyCount() + 1);
        userUsageTrackingRepository.save(tracking);

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", savedSession.getId().toString());
        result.put("questions", responseQuestions);
        return result;
    }

    @Transactional
    public Map<String, Object> evaluateSession(User user, EvaluateMockRequest request) {
        MockSession session = mockSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mock Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to mock session");
        }

        if (!session.getStatus().equals("RUNNING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mock Session is already evaluated");
        }

        boolean isPro = user.getRole().equals("PRO");

        // 1. Gather all questions and submissions to construct evaluation prompt
        List<InterviewQuestion> questions = interviewQuestionRepository.findBySessionId(session.getId());
        Map<UUID, InterviewQuestion> qMap = new HashMap<>();
        for (InterviewQuestion q : questions) {
            qMap.put(q.getId(), q);
        }

        StringBuilder submissionSummary = new StringBuilder();
        for (SubmissionDto sub : request.getSubmissions()) {
            InterviewQuestion q = qMap.get(sub.getQuestionId());
            if (q == null) continue;

            submissionSummary.append("Question ID: ").append(q.getId()).append("\n");
            submissionSummary.append("Title: ").append(q.getTitle()).append(" (Type: ").append(q.getQuestionType()).append(")\n");

            if (q.getQuestionType().equals("dsa")) {
                submissionSummary.append("Language: ").append(sub.getLanguage()).append("\n");
                submissionSummary.append("Candidate Code:\n").append(sub.getCodeAnswer()).append("\n");
                submissionSummary.append("Hints Used: ").append(sub.getUsedHints()).append("\n");
            } else {
                submissionSummary.append("Candidate Answer:\n").append(sub.getBehavioralAnswer()).append("\n");
            }
            submissionSummary.append("\n---\n\n");
        }

        // 2. Build system and user prompts
        String systemPrompt = "You are a senior engineering interviewer giving detailed, constructive mock interview feedback.\n" +
                "Evaluate the candidate's submissions and return ONLY valid JSON (no markdown, no backticks):\n" +
                "{\n" +
                "  \"score\": 78,\n" +
                "  \"grade\": \"B+\",\n" +
                "  \"summary\": \"2-3 sentence overall assessment.\",\n" +
                "  \"strengths\": [\"strength 1\", \"strength 2\"],\n" +
                "  \"improvements\": [\"improvement 1\", \"improvement 2\"],\n" +
                "  \"breakdown\": {\n" +
                "    \"QUESTION_UUID_STRING_1\": {\n" +
                "      \"ok\": true,\n" +
                "      \"score\": 85,\n" +
                "      \"feedback\": \"" + (isPro ? "2-3 sentences detailed feedback." : "1 sentence basic feedback.") + "\",\n" +
                "      \"complexity\": \"O(n)\",\n" +
                "      \"complexityOk\": true,\n" +
                "      \"codeQuality\": \"good|fair|poor\",\n" +
                "      \"suggestions\": [\"suggestion 1\"]\n" +
                "    }\n" +
                "  },\n" +
                "  \"hiringDecision\": \"Strong Hire|Hire|Borderline|No Hire\",\n" +
                "  \"nextSteps\": [\"Study DP\", \"Practice system design\"]\n" +
                "}\n" +
                "Note: The keys in \"breakdown\" must match the corresponding QUESTION_UUID_STRING exactly.";

        String userPrompt = "Candidate submissions for evaluation:\n\n" + submissionSummary.toString();

        // 3. Call Claude & Parse
        String responseText = "";
        JsonNode rootNode = null;
        int parseAttempts = 0;
        while (parseAttempts < 2) {
            try {
                parseAttempts++;
                responseText = claudeService.evaluateSession(systemPrompt, userPrompt, isPro ? 2000 : 800);
                rootNode = objectMapper.readTree(responseText);
                if (rootNode.has("score")) {
                    break;
                }
            } catch (Exception e) {
                if (parseAttempts >= 2) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse AI evaluation from Claude API. Please retry.", e);
                }
            }
        }

        // 4. Record submissions in the DB
        JsonNode breakdownNode = rootNode.path("breakdown");
        int totalPoints = 0;
        int totalQuestionsValued = 0;

        for (SubmissionDto sub : request.getSubmissions()) {
            InterviewQuestion q = qMap.get(sub.getQuestionId());
            if (q == null) continue;

            JsonNode evalItem = breakdownNode.path(q.getId().toString());
            int itemScore = evalItem.path("score").asInt(50);

            // Deduct points for hints if user is FREE
            if (!isPro && q.getQuestionType().equals("dsa") && sub.getUsedHints() > 0) {
                itemScore = Math.max(0, itemScore - (sub.getUsedHints() * 5));
            }

            totalPoints += itemScore;
            totalQuestionsValued++;

            QuestionSubmission submission = new QuestionSubmission();
            submission.setQuestion(q);
            submission.setUser(user);
            submission.setLanguage(sub.getLanguage());
            submission.setCodeAnswer(sub.getCodeAnswer());
            submission.setBehavioralAnswer(sub.getBehavioralAnswer());
            submission.setScore(itemScore);
            submission.setFeedback(evalItem.path("feedback").asText("No detailed feedback generated."));
            questionSubmissionRepository.save(submission);

            // Update Weak Topic Analysis for DSA failures
            if (q.getQuestionType().equals("dsa")) {
                boolean isFail = !evalItem.path("ok").asBoolean(true) || itemScore < 60;
                updateWeakTopics(user, q.getTopic(), isFail);
            }
        }

        // 5. Update session metadata
        int finalScore = rootNode.path("score").asInt(totalQuestionsValued > 0 ? (totalPoints / totalQuestionsValued) : 0);
        String finalGrade = rootNode.path("grade").asText("C");
        String hiringDecision = rootNode.path("hiringDecision").asText("Borderline");

        session.setScore(finalScore);
        session.setGrade(finalGrade);
        session.setHiringDecision(hiringDecision);
        session.setStatus("COMPLETED");
        session.setCompletedAt(LocalDateTime.now());
        session.setTimeTaken(java.time.Duration.between(session.getStartedAt(), session.getCompletedAt()).toSeconds());
        mockSessionRepository.save(session);

        // 6. Save overall evaluation
        SessionEvaluation evaluation = new SessionEvaluation();
        evaluation.setSession(session);
        evaluation.setOverallScore(finalScore);
        evaluation.setGrade(finalGrade);
        evaluation.setSummary(rootNode.path("summary").asText("Mock interview completed successfully."));
        evaluation.setStrengthsJson(rootNode.path("strengths").toString());
        evaluation.setImprovementsJson(rootNode.path("improvements").toString());
        evaluation.setHiringDecision(hiringDecision);
        evaluation.setNextStepsJson(rootNode.path("nextSteps").toString());
        sessionEvaluationRepository.save(evaluation);

        // Prepare client result response
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("grade", finalGrade);
        result.put("summary", evaluation.getSummary());
        result.put("hiringDecision", hiringDecision);

        try {
            result.put("strengths", objectMapper.readValue(evaluation.getStrengthsJson(), List.class));
            result.put("improvements", objectMapper.readValue(evaluation.getImprovementsJson(), List.class));
            result.put("nextSteps", objectMapper.readValue(evaluation.getNextStepsJson(), List.class));
        } catch (Exception ignored) {}

        Map<String, Object> breakdownClient = new HashMap<>();
        for (InterviewQuestion q : questions) {
            JsonNode itemNode = breakdownNode.path(q.getId().toString());
            Map<String, Object> detail = new HashMap<>();
            detail.put("ok", itemNode.path("ok").asBoolean(true));
            detail.put("score", itemNode.path("score").asInt(50));
            detail.put("feedback", itemNode.path("feedback").asText(""));
            if (q.getQuestionType().equals("dsa")) {
                detail.put("complexity", itemNode.path("complexity").asText("O(n)"));
                detail.put("complexityOk", itemNode.path("complexityOk").asBoolean(true));
                detail.put("codeQuality", itemNode.path("codeQuality").asText("fair"));
                detail.put("suggestions", itemNode.path("suggestions").toString());
            }
            breakdownClient.put(q.getId().toString(), detail);
        }
        result.put("breakdown", breakdownClient);

        return result;
    }

    private void updateWeakTopics(User user, String topic, boolean isFail) {
        if (topic == null || topic.isBlank()) return;
        WeakTopicAnalysis analysis = weakTopicAnalysisRepository
                .findByUserIdAndTopic(user.getId(), topic)
                .orElseGet(() -> new WeakTopicAnalysis(user, topic, 0, 0, 100.0));

        analysis.setAttempts(analysis.getAttempts() + 1);
        if (isFail) {
            analysis.setFailures(analysis.getFailures() + 1);
        }
        double score = ((analysis.getAttempts() - analysis.getFailures()) * 100.0) / analysis.getAttempts();
        analysis.setStrengthScore(score);
        analysis.setUpdatedAt(LocalDateTime.now());
        weakTopicAnalysisRepository.save(analysis);
    }

    @Cacheable(value = "weakTopics", key = "#user.id")
    public List<WeakTopicDto> getWeakTopics(User user) {
        List<WeakTopicAnalysis> analyses = weakTopicAnalysisRepository.findByUserId(user.getId());
        List<WeakTopicDto> dtoList = new ArrayList<>();
        for (WeakTopicAnalysis a : analyses) {
            dtoList.add(new WeakTopicDto(a.getTopic(), a.getAttempts(), a.getFailures(), a.getStrengthScore()));
        }
        return dtoList;
    }

    public List<Map<String, Object>> getSessions(User user, int page, int size) {
        Page<MockSession> sessions = mockSessionRepository.findByUserIdOrderByStartedAtDesc(user.getId(), PageRequest.of(page, size));
        List<Map<String, Object>> list = new ArrayList<>();
        for (MockSession s : sessions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId().toString());
            map.put("company", s.getCompany());
            map.put("interviewType", s.getInterviewType());
            map.put("level", s.getLevel());
            map.put("status", s.getStatus());
            map.put("score", s.getScore());
            map.put("grade", s.getGrade());
            map.put("hiringDecision", s.getHiringDecision());
            map.put("startedAt", s.getStartedAt().toString());
            map.put("timeTaken", s.getTimeTaken());
            list.add(map);
        }
        return list;
    }

    public Map<String, Object> getSessionDetails(User user, UUID sessionId) {
        MockSession session = mockSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mock Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to mock session");
        }

        List<InterviewQuestion> questions = interviewQuestionRepository.findBySessionId(sessionId);
        List<QuestionSubmission> submissions = questionSubmissionRepository.findByQuestionSessionId(sessionId);
        Optional<SessionEvaluation> evaluation = sessionEvaluationRepository.findBySessionId(sessionId);

        Map<UUID, QuestionSubmission> subMap = new HashMap<>();
        for (QuestionSubmission sub : submissions) {
            subMap.put(sub.getQuestion().getId(), sub);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getId().toString());
        result.put("company", session.getCompany());
        result.put("interviewType", session.getInterviewType());
        result.put("level", session.getLevel());
        result.put("status", session.getStatus());
        result.put("score", session.getScore());
        result.put("grade", session.getGrade());
        result.put("hiringDecision", session.getHiringDecision());
        result.put("startedAt", session.getStartedAt().toString());
        result.put("timeTaken", session.getTimeTaken());

        List<Map<String, Object>> qList = new ArrayList<>();
        for (InterviewQuestion q : questions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId().toString());
            map.put("type", q.getQuestionType());
            map.put("difficulty", q.getDifficulty());
            map.put("topic", q.getTopic());
            map.put("title", q.getTitle());
            map.put("desc", q.getDescription());

            QuestionSubmission sub = subMap.get(q.getId());
            if (sub != null) {
                map.put("language", sub.getLanguage());
                map.put("codeAnswer", sub.getCodeAnswer());
                map.put("behavioralAnswer", sub.getBehavioralAnswer());
                map.put("submissionScore", sub.getScore());
                map.put("submissionFeedback", sub.getFeedback());
            }
            qList.add(map);
        }
        result.put("questions", qList);

        if (evaluation.isPresent()) {
            SessionEvaluation eval = evaluation.get();
            Map<String, Object> evalMap = new HashMap<>();
            evalMap.put("overallScore", eval.getOverallScore());
            evalMap.put("grade", eval.getGrade());
            evalMap.put("summary", eval.getSummary());
            evalMap.put("hiringDecision", eval.getHiringDecision());
            try {
                evalMap.put("strengths", objectMapper.readValue(eval.getStrengthsJson(), List.class));
                evalMap.put("improvements", objectMapper.readValue(eval.getImprovementsJson(), List.class));
                evalMap.put("nextSteps", objectMapper.readValue(eval.getNextStepsJson(), List.class));
            } catch (Exception ignored) {}
            result.put("evaluation", evalMap);
        }

        return result;
    }

    @Transactional
    public Map<String, Object> retrySession(User user, UUID sessionId) {
        MockSession oldSession = mockSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Original Mock Session not found"));

        if (!oldSession.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to mock session");
        }

        GenerateMockRequest request = new GenerateMockRequest();
        request.setCompany(oldSession.getCompany());
        request.setLevel(oldSession.getLevel());
        request.setType(oldSession.getInterviewType());

        List<InterviewQuestion> oldQuestions = interviewQuestionRepository.findBySessionId(sessionId);
        List<String> topics = new ArrayList<>();
        for (InterviewQuestion q : oldQuestions) {
            topics.add(q.getTopic());
        }
        request.setTopics(topics);

        return generateSession(user, request);
    }
}
