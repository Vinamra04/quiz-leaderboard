package com.srmquiz.quiz_leaderboard.service;

import com.srmquiz.quiz_leaderboard.model.LeaderboardEntry;
import com.srmquiz.quiz_leaderboard.model.PollResponse;
import com.srmquiz.quiz_leaderboard.model.QuizEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final String REG_NO = "2024CS101";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<LeaderboardEntry> buildLeaderboard() throws InterruptedException {
        Set<String> seen = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + i;
            PollResponse response = restTemplate.getForObject(url, PollResponse.class);

            if (response != null && response.events != null) {
                for (QuizEvent e : response.events) {
                    String key = e.roundId + "_" + e.participant;
                    if (seen.add(key))
                        scores.merge(e.participant, e.score, Integer::sum);
                }
            }

            Thread.sleep(5000);
        }

        return scores.entrySet().stream()
                .map(e -> new LeaderboardEntry(e.getKey(), e.getValue()))
                .sorted((a, b) -> b.totalScore - a.totalScore)
                .collect(Collectors.toList());
    }

    public void submitLeaderboard(List<LeaderboardEntry> leaderboard) {
        String url = BASE_URL + "/quiz/submit";
        Map<String, Object> body = new HashMap<>();
        body.put("regNo", REG_NO);
        body.put("leaderboard", leaderboard);
        Map<String, Object> result = restTemplate.postForObject(url, body, Map.class);
        System.out.println("Result: " + result);
    }
}