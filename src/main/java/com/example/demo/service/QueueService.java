package com.example.demo.service;

import com.example.demo.dto.GenerateMockRequest;
import com.example.demo.entity.GenerationQueueEntity;
import com.example.demo.entity.User;
import com.example.demo.repository.GenerationQueueRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class QueueService {

    private final GenerationQueueRepository generationQueueRepository;
    private final UserRepository userRepository;
    private final MockService mockService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public QueueService(
            GenerationQueueRepository generationQueueRepository,
            UserRepository userRepository,
            MockService mockService) {
        this.generationQueueRepository = generationQueueRepository;
        this.userRepository = userRepository;
        this.mockService = mockService;
    }

    public UUID addToQueue(Long userId, GenerateMockRequest request) {
        // Enforce limit check at enqueue time
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Limit check
        mockService.getUsage(user); // If user usage count >= limit, it throws 403 Forbidden ResponseStatusException

        // Check if there is an active job for this user
        long activeJobsCount = generationQueueRepository.countActiveJobsForUser(userId);
        if (activeJobsCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already have an active generation in the queue.");
        }

        try {
            String configJson = objectMapper.writeValueAsString(request);
            GenerationQueueEntity job = new GenerationQueueEntity(userId, configJson);
            GenerationQueueEntity saved = generationQueueRepository.save(job);
            return saved.getId();
        } catch (Exception e) {
            if (e instanceof ResponseStatusException) {
                throw (ResponseStatusException) e;
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to enqueue generation job", e);
        }
    }

    public Map<String, Object> getJobStatus(UUID jobId) {
        GenerationQueueEntity job = generationQueueRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        int position = 0;
        if (job.getStatus().equals("waiting")) {
            position = generationQueueRepository.countWaitingJobsBefore(job.getCreatedAt()) + 1;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", job.getStatus());
        response.put("position", job.getStatus().equals("waiting") ? position : null);

        if (job.getResult() != null) {
            try {
                response.put("result", objectMapper.readValue(job.getResult(), Map.class));
            } catch (Exception e) {
                response.put("result", null);
            }
        } else {
            response.put("result", null);
        }

        return response;
    }

    @Scheduled(fixedDelay = 3000)
    public void processQueue() {
        int currentlyProcessing = generationQueueRepository.countCurrentlyProcessing();
        int slotsAvailable = 3 - currentlyProcessing;
        if (slotsAvailable <= 0) {
            return;
        }

        // Fetch up to slotsAvailable waiting jobs
        List<GenerationQueueEntity> waitingJobs = generationQueueRepository.findByStatusOrderByCreatedAtAsc(
                "waiting", PageRequest.of(0, slotsAvailable));

        for (GenerationQueueEntity job : waitingJobs) {
            markAsProcessing(job);
            executor.submit(() -> {
                try {
                    processJob(job);
                } catch (Exception e) {
                    markAsFailed(job);
                }
            });
        }
    }

    @Transactional
    public void markAsProcessing(GenerationQueueEntity job) {
        job.setStatus("processing");
        job.setUpdatedAt(LocalDateTime.now());
        generationQueueRepository.save(job);
    }

    @Transactional
    public void markAsFailed(GenerationQueueEntity job) {
        job.setStatus("failed");
        job.setUpdatedAt(LocalDateTime.now());
        generationQueueRepository.save(job);
    }

    private void processJob(GenerationQueueEntity job) throws Exception {
        User user = userRepository.findById(job.getUserId()).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        GenerateMockRequest request = objectMapper.readValue(job.getConfig(), GenerateMockRequest.class);
        
        // This executes the actual AI generation, saves mockSession and questions, and increments usage tracking
        Map<String, Object> result = mockService.generateSession(user, request);

        saveJobResult(job, result);
    }

    @Transactional
    public void saveJobResult(GenerationQueueEntity job, Map<String, Object> result) throws Exception {
        job.setResult(objectMapper.writeValueAsString(result));
        job.setStatus("done");
        job.setUpdatedAt(LocalDateTime.now());
        generationQueueRepository.save(job);
    }
}
