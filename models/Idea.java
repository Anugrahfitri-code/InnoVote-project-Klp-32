package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Idea {
    private StringProperty id; 
    private StringProperty title;
    private StringProperty description;
    private StringProperty category; 
    private Participant participant; 
    private LocalDateTime submissionDate;
    private DoubleProperty averageScore;
    private List<Vote> votes; 

    // Constructor
    public Idea(String id, String title, String description, String category, Participant participant) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.participant = participant;
        this.submissionDate = LocalDateTime.now();
        this.averageScore = new SimpleDoubleProperty(0.0); 
        this.votes = new ArrayList<>();
    }

    // ID
    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }
    public void setId(String id) { this.id.set(id); }

    // Title
    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }
    public void setTitle(String title) { this.title.set(title); }

    // Description
    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String description) { this.description.set(description); }

    // Category
    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }
    public void setCategory(String category) { this.category.set(category); }

    // Participant (Pengganti Author)
    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }

    // Submission Date
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    // Average Score
    public double getAverageScore() { return averageScore.get(); }
    public DoubleProperty averageScoreProperty() { return averageScore; }

    // Votes List
    public List<Vote> getVotes() { return votes; }

    public void addVote(Vote vote) {
        this.votes.add(vote);
        updateAverageScore();
    }
    
    public void updateAverageScore() {
        if (votes.isEmpty()) {
            averageScore.set(0.0);
        } else {
            double totalScore = 0;
            for (Vote vote : votes) {
                totalScore += vote.getScore();
            }
            averageScore.set(totalScore / votes.size());
        }
    }
}