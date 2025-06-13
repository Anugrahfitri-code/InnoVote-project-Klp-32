package services;

import models.Idea;
import models.Participant;
import utils.DummyDatabase;
import exceptions.IdeaException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IdeaService {
    public static Idea submitIdea(Participant participant, String title, String description, String category) throws IdeaException {
        validateIdea(title, description, category); // Validasi input

        boolean isDuplicate = DummyDatabase.getAllIdeas().stream()
                .filter(idea -> idea.getParticipant().equals(participant))
                .anyMatch(idea -> idea.getTitle().equalsIgnoreCase(title));

        if (isDuplicate) {
            throw new IdeaException("You have already submitted an idea with this title.");
        }

        String id = UUID.randomUUID().toString(); 
        Idea newIdea = new Idea(id, title, description, category, participant);
        DummyDatabase.addIdea(newIdea);
        return newIdea;
    }

    // Metode untuk mengambil semua ide
    public static List<Idea> getAllIdeas() throws IdeaException {
        try {
            List<Idea> ideas = DummyDatabase.getAllIdeas();
            return ideas;
        } catch (Exception e) {
            throw new IdeaException("Error retrieving all ideas: " + e.getMessage());
        }
    }

    // Metode untuk mengambil ide berdasarkan partisipan
    public static List<Idea> getIdeasByParticipant(Participant participant) throws IdeaException {
        if (participant == null) {
            throw new IdeaException("Participant cannot be null.");
        }
        try {
            return DummyDatabase.getAllIdeas().stream()
                    .filter(idea -> idea.getParticipant().equals(participant))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IdeaException("Error retrieving ideas for participant " + participant.getUsername() + ": " + e.getMessage());
        }
    }

    // Metode untuk mengedit ide yang sudah ada
    public static Idea editIdea(String ideaId, String newTitle, String newDescription, String newCategory) throws IdeaException {
        if (ideaId == null || ideaId.isBlank()) {
            throw new IdeaException("Idea ID cannot be empty for editing.");
        }
        validateIdea(newTitle, newDescription, newCategory);

        Idea ideaToEdit = DummyDatabase.getAllIdeas().stream()
                                .filter(idea -> idea.getId().equals(ideaId))
                                .findFirst()
                                .orElseThrow(() -> new IdeaException("Idea with ID " + ideaId + " not found."));

        // Cek duplikasi judul baru untuk partisipan yang sama, kecuali ide itu sendiri
        boolean isDuplicateTitleForOtherIdea = DummyDatabase.getAllIdeas().stream()
                .filter(idea -> idea.getParticipant().equals(ideaToEdit.getParticipant()))
                .filter(idea -> !idea.getId().equals(ideaId)) 
                .anyMatch(idea -> idea.getTitle().equalsIgnoreCase(newTitle));

        if (isDuplicateTitleForOtherIdea) {
            throw new IdeaException("You already have another idea with the title '" + newTitle + "'.");
        }

        ideaToEdit.setTitle(newTitle);
        ideaToEdit.setDescription(newDescription);
        ideaToEdit.setCategory(newCategory);

        return ideaToEdit;
    }

    public static void deleteIdea(String ideaId) throws IdeaException {
        if (ideaId == null || ideaId.isBlank()) {
            throw new IdeaException("Idea ID cannot be empty for deletion.");
        }

        boolean removed = DummyDatabase.removeIdea(ideaId);

        if (!removed) {
            throw new IdeaException("Idea with ID " + ideaId + " not found or could not be deleted.");
        }
        DummyDatabase.removeVotesForIdea(ideaId);
    }
    
    private static void validateIdea(String title, String description, String category) throws IdeaException {
        if (title == null || title.isBlank()) {
            throw new IdeaException("Idea title cannot be empty.");
        }
        if (description == null || description.isBlank()) {
            throw new IdeaException("Idea description cannot be empty.");
        }
        if (category == null || category.isBlank()) {
            throw new IdeaException("Idea category cannot be empty."); 
        }
        if (title.length() > 100) {
            throw new IdeaException("Idea title cannot exceed 100 characters.");
        }
        if (description.length() > 500) {
            throw new IdeaException("Idea description cannot exceed 500 characters.");
        }
    }
}