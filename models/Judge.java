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

    /**
     * Memungkinkan seorang Judge untuk memberikan suara pada sebuah Ide.
     * Vote akan ditambahkan ke daftar vote Judge dan juga ke DummyDatabase.
     *
     * @param idea Objek Ide yang akan divote.
     * @param score Skor yang diberikan (misalnya 1-5).
     * @param comment Komentar atau feedback dari Judge.
     */
    public void vote(Idea idea, int score, String comment) {
        String voteId = UUID.randomUUID().toString();

        Vote newVote = new Vote(voteId, idea, this, score, comment);

        this.votes.add(newVote);

        com.innovote.utils.DummyDatabase.addVote(newVote);

        System.out.println("Judge " + this.getUsername() + " voted for idea: " + idea.getTitle() + " with score: " + score);
    }

    /**
     * Mengembalikan riwayat vote yang telah diberikan oleh Judge ini.
     * @return Daftar objek Vote.
     */
    public List<Vote> getVotingHistory() {
        return new ArrayList<>(votes); 
    }
}
