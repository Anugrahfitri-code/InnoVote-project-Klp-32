package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;  

public class Participant extends User {
    private List<Idea> submittedIdeas;

    public Participant(String id, String username, String password) {
        super(id, username, password, "Participant");
        this.submittedIdeas = new ArrayList<>();
    }

    public void submitIdea(Idea idea) {
        this.submittedIdeas.add(idea);
    }

    public List<Idea> getSubmittedIdeas() {
        return new ArrayList<>(submittedIdeas);
    }
}
