package com.srmquiz.quiz_leaderboard.model;

public class LeaderboardEntry {
    public String participant;
    public int totalScore;

    public LeaderboardEntry(String participant, int totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }
}
