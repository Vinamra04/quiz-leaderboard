
# Quiz Leaderboard System

This project was built as part of the **Bajaj Finserv Health Java Qualifier Assignment** conducted at SRM Institute of Science and Technology on April 24, 2026.

## Problem Statement

The assignment simulates a real-world backend integration scenario. A quiz validator API exposes participant scores across multiple rounds. The API must be polled exactly 10 times (poll index 0-9), with a mandatory 5-second delay between each request. The catch - the same event data can appear across multiple polls (duplicates), and if not handled correctly, the final scores will be wrong.

The objective was to:
- Poll the API 10 times
- Deduplicate events using `roundId + participant` as a unique identifier
- Aggregate scores correctly per participant
- Generate a leaderboard sorted by total score
- Submit the leaderboard exactly once

## My Approach

The core challenge here isn't just making API calls - it's idempotent event processing, which is a common pattern in distributed systems. The same message arriving twice should not be counted twice.

I used a `HashSet` to track seen events. Before adding a score, I check if the combination of `roundId + participant` has already been processed. If it has, I skip it. This ensures each round's score for each participant is counted exactly once regardless of how many times the API returns it.

For the aggregation, I used a `HashMap` with `Map.merge()` which cleanly handles both inserting a new participant and adding to an existing score in one line.

## Tech Stack

- Java 21
- Spring Boot 4.0.6
- Maven
- Spring Web (RestTemplate for HTTP calls)

## Project Structure

```text
quiz-leaderboard/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/srmquiz/quiz_leaderboard/
│       │       ├── model/
│       │       │   ├── QuizEvent.java
│       │       │   ├── PollResponse.java
│       │       │   └── LeaderboardEntry.java
│       │       ├── service/
│       │       │   └── QuizService.java
│       │       ├── runner/
│       │       │   └── QuizRunner.java
│       │       └── QuizLeaderboardApplication.java
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```

## How it Works

1. `QuizRunner` implements `CommandLineRunner` and kicks off the process when the app starts
2. `QuizService.buildLeaderboard()` loops from poll 0 to 9, calling the GET endpoint each time with a 5 second sleep between requests
3. Every event is checked against a `HashSet` - duplicates are silently ignored
4. Scores are accumulated in a `HashMap` and the final leaderboard is sorted in descending order
5. `QuizService.submitLeaderboard()` POSTs the result once to the submit endpoint

## How to Run

Make sure you have Java 21+ and Maven installed.

```bash
mvn spring-boot:run
```

The app runs, completes all 10 polls (~50 seconds), prints the leaderboard, submits it, and exits.

## Output

```
Bob -> 295
Alice -> 280
Charlie -> 260
Result: {submittedTotal=835, ...}
Submitted.
```