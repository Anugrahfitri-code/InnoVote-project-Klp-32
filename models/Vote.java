package com.innovote.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Vote {
    private final String id;
    private final Idea idea;
    private final Judge voter; 
    private final IntegerProperty score;
    private final StringProperty comment;

    public Vote(String id, Idea idea, Judge voter, int score, String comment) {
        this.id = id;
        this.idea = idea;
        this.voter = voter;
        this.score = new SimpleIntegerProperty(score);
        this.comment = new SimpleStringProperty(comment);
    }

    public String getId() {
        return id;
    }

    public Idea getIdea() {
        return idea;
    }

    public Judge getVoter() {
        return voter;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public int getScore() {
        return score.get();
    }

    public String getComment() {
        return comment.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return id.equals(vote.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
