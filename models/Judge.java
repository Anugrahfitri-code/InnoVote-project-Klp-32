package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;  

public class Judge extends User {
    private List<Vote> votes;

    public Judge(String id, String username, String password) {
        super(id, username, password, "Judge");
        this.votes = new ArrayList<>();
    }

    public void vote(Idea idea, int score, String comment) {
        String voteId = UUID.randomUUID().toString();
        Vote newVote = new Vote(voteId, idea, this, score, comment);
        this.votes.add(newVote);
        utils.DummyDatabase.addVote(newVote);
        System.out.println("Judge " + this.getUsername() + " voted for idea: " + idea.getTitle() + " with score: " + score);
    }

    public List<Vote> getVotingHistory() {
        return new ArrayList<>(votes); 
    }
}
