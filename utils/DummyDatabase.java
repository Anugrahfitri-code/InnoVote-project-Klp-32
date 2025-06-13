package utils;

import java.util.ArrayList; 
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; 

import models.Idea;
import models.Judge;
import models.Participant;
import models.User;
import models.Vote;

public class DummyDatabase {
    private static final List<User> users = new ArrayList<>();
    private static final List<Idea> ideas = new ArrayList<>();
    private static final List<Vote> votes = new ArrayList<>();

    static {
        clearAll(); 
        initializeDummyData();
    }

    private static void initializeDummyData() {
        Participant p1 = new Participant(UUID.randomUUID().toString(), "participant1", "pass123");
        Participant p2 = new Participant(UUID.randomUUID().toString(), "participant2", "pass123");
        Judge j1 = new Judge(UUID.randomUUID().toString(), "judge1", "pass123");
        Judge j2 = new Judge(UUID.randomUUID().toString(), "judge2", "pass123");

        users.add(p1);
        users.add(p2);
        users.add(j1);
        users.add(j2);

        Idea idea1 = new Idea(UUID.randomUUID().toString(), "Green Energy Solution", "A new method for harnessing solar energy with higher efficiency.", "Environment", p1);
        Idea idea2 = new Idea(UUID.randomUUID().toString(), "Smart City Traffic Management", "AI-driven system to optimize traffic flow in urban areas.", "AI", p2);
        Idea idea3 = new Idea(UUID.randomUUID().toString(), "Community Recycling App", "Mobile app to connect residents with local recycling centers.", "Environment", p1);
        Idea idea4 = new Idea(UUID.randomUUID().toString(), "Personalized Learning Platform", "AI-powered platform that adapts to individual student learning styles.", "Education", p2);

        ideas.add(idea1);
        ideas.add(idea2);
        ideas.add(idea3);
        ideas.add(idea4);

        Vote vote1_j1 = new Vote(UUID.randomUUID().toString(), idea1, j1, 5, "Very promising idea for sustainability.");
        Vote vote2_j2 = new Vote(UUID.randomUUID().toString(), idea1, j2, 4, "Good concept, needs more technical detail.");
        Vote vote3_j1 = new Vote(UUID.randomUUID().toString(), idea2, j1, 3, "Interesting, but implementation seems complex.");

        addVote(vote1_j1);
        addVote(vote2_j2);
        addVote(vote3_j1);

        idea1.updateAverageScore();
        idea2.updateAverageScore();
        idea3.updateAverageScore();
        idea4.updateAverageScore();
    }


    public static User findUser(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElse(null);
    }

    public static User findUserById(String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public static void addUser(User user) {
        if (findUserById(user.getId()) == null && findUser(user.getUsername()) == null) {
            users.add(user);
            System.out.println("User added: " + user.getUsername() + " (" + user.getRole() + ")"); 
        } else {
            System.out.println("User '" + user.getUsername() + "' already exists. Not adding duplicate.");
        }
    }

    public static void addIdea(Idea idea) {
        if (ideas.stream().noneMatch(i -> i.getId().equals(idea.getId()))) {
             ideas.add(idea);
             System.out.println("Idea added: " + idea.getTitle()); 
        } else {
            System.out.println("Idea '" + idea.getTitle() + "' already exists. Not adding duplicate.");
        }
    }

    public static List<Idea> getAllIdeas() {
        return new ArrayList<>(ideas);
    }

    public static List<Idea> getIdeasByParticipant(Participant p) {
        return ideas.stream()
            .filter(i -> i.getParticipant().equals(p)) 
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked") 
    public static List<Vote> getVotesForIdea(String ideaId) {
        return votes.stream()
                .filter(vote -> vote.getIdea().getId().equals(ideaId))
                .collect(Collectors.toList());
    }


    public static void addVote(Vote vote) {
        if (votes.stream().noneMatch(v -> v.getId().equals(vote.getId()))) {
            votes.add(vote);
            vote.getIdea().addVote(vote); 
            System.out.println("Vote added: by " + vote.getVoter().getUsername() + " for " + vote.getIdea().getTitle() + " (Score: " + vote.getScore() + ")"); // Debugging
        } else {
            System.out.println("Vote with ID " + vote.getId() + " already exists. Not adding duplicate.");
        }
    }

    public static List<Vote> getVotes() {
        return new ArrayList<>(votes);
    }

    public static void clearAll() {
        users.clear();
        ideas.clear();
        votes.clear();
        System.out.println("DummyDatabase cleared.");
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static boolean removeIdea(String ideaId) {
        boolean removed = ideas.removeIf(idea -> idea.getId().equals(ideaId));
        if (removed) {
            System.out.println("Idea with ID " + ideaId + " removed.");
            removeVotesForIdea(ideaId);
            return true;
        }
        System.out.println("Idea with ID " + ideaId + " not found for removal.");
        return false;
    }

    public static void removeVotesForIdea(String ideaId) {
        votes.removeIf(vote -> vote.getIdea().getId().equals(ideaId));

        System.out.println("Votes for idea ID " + ideaId + " removed.");
    }
}
