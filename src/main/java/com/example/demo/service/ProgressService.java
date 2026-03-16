package com.example.demo.service;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.dto.ToggleSolvedRequest;
import com.example.demo.entity.UserProgress;
import com.example.demo.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final UserProgressRepository progressRepository;

    public ProgressService(UserProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Transactional
    public boolean toggleSolved(Long userId, ToggleSolvedRequest req) {
        if (progressRepository.existsByUserIdAndProblemId(userId, req.getProblemId())) {
            progressRepository.deleteByUserIdAndProblemId(userId, req.getProblemId());
            return false;
        } else {
            UserProgress progress = new UserProgress(
                userId,
                req.getProblemId(),
                req.getProblemTitle(),
                req.getDifficulty()
            );
            progressRepository.save(progress);
            return true;
        }
    }

    public DashboardStatsDto getStats(Long userId) {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalSolved(progressRepository.countByUserId(userId));
        stats.setEasySolved(progressRepository.countByUserIdAndDifficulty(userId, "easy"));
        stats.setMediumSolved(progressRepository.countByUserIdAndDifficulty(userId, "medium"));
        stats.setHardSolved(progressRepository.countByUserIdAndDifficulty(userId, "hard"));

        List<UserProgress> all = progressRepository.findByUserIdOrderBySolvedAtDesc(userId);

        // ✅ Fixed — collect problem IDs as List<Integer>
        List<Integer> solvedIds = all.stream()
            .map(UserProgress::getProblemId)
            .collect(Collectors.toList());
        stats.setSolvedProblemIds(solvedIds);

        // Recent 5
        List<DashboardStatsDto.RecentProblem> recent = all.stream()
            .limit(5)
            .map(p -> new DashboardStatsDto.RecentProblem(
                p.getProblemId(),
                p.getProblemTitle(),
                p.getDifficulty(),
                p.getSolvedAt() != null
                    ? p.getSolvedAt().toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd MMM"))
                    : "Today"
            ))
            .collect(Collectors.toList());
        stats.setRecentProblems(recent);

        stats.setStreak(calculateStreak(all));
        stats.setMockInterviews(0);
        stats.setResumeScore(null);

        return stats;
    }

    private int calculateStreak(List<UserProgress> all) {
        if (all.isEmpty()) return 0;

        List<LocalDate> days = all.stream()
            .filter(p -> p.getSolvedAt() != null)
            .map(p -> p.getSolvedAt().toLocalDate())
            .distinct()
            .sorted((a, b) -> b.compareTo(a))
            .collect(Collectors.toList());

        if (days.isEmpty()) return 0;

        int streak = 0;
        LocalDate expected = LocalDate.now();

        for (LocalDate day : days) {
            if (day.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}