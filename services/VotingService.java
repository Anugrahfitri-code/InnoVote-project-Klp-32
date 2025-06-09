package com.innovote.services;

import com.innovote.models.*;
import com.innovote.utils.DummyDatabase;
import com.innovote.exceptions.VotingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

public class VotingService {
    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 10;

    public static Vote submitVote(Judge judge, Idea idea, int score, String comment) throws VotingException {
        validateVote(judge, idea, score);

        Optional<Vote> existingVote = DummyDatabase.getVotes().stream()
                .filter(v -> v.getVoter().equals(judge) && v.getIdea().equals(idea))
                .findFirst();

        if (existingVote.isPresent()) {
            throw new VotingException("You have already voted for this idea.");
        }

        String voteId = UUID.randomUUID().toString();

        Vote newVote = new Vote(voteId, idea, judge, score, comment);
        
        DummyDatabase.addVote(newVote); 

        return newVote;
    }

    private static void validateVote(Judge judge, Idea idea, int score) throws VotingException {
        if (judge == null || idea == null) {
            throw new VotingException("Invalid judge or idea.");
        }

        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new VotingException("Score must be between " + MIN_SCORE + " and " + MAX_SCORE + ".");
        }
    }

    public static long getVoteCount(Idea idea) {

        return DummyDatabase.getVotes().stream()
                .filter(v -> v.getIdea().equals(idea))
                .count();
    }
}
