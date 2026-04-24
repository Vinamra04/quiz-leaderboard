package com.srmquiz.quiz_leaderboard.runner;

import com.srmquiz.quiz_leaderboard.model.LeaderboardEntry;
import com.srmquiz.quiz_leaderboard.service.QuizService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizRunner implements CommandLineRunner {

    private final QuizService quizService;

    public QuizRunner(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<LeaderboardEntry> leaderboard = quizService.buildLeaderboard();
        leaderboard.forEach(e -> System.out.println(e.participant + " -> " + e.totalScore));
        quizService.submitLeaderboard(leaderboard);
        System.out.println("Submitted.");
    }
}