package com.innovote.screens.judge;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.innovote.models.Judge;
import com.innovote.models.Idea;
import com.innovote.session.Session;
import com.innovote.services.IdeaService;
import com.innovote.utils.SceneManager;
import com.innovote.exceptions.IdeaException;
import com.innovote.utils.AlertHelper;
import javafx.scene.control.Alert.AlertType;

import javafx.beans.property.ReadOnlyStringWrapper;
import com.innovote.screens.judge.VotingScreen; 
import com.innovote.screens.common.IdeaDetailScreen; 
import javafx.scene.layout.Priority;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JudgeDashboard extends BorderPane {

    private Judge currentJudge;
    private TableView<Idea> ideaTable;
    private List<Idea> allIdeas; 

    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> sortOrder;

    public JudgeDashboard(Judge judge) {
        this.currentJudge = judge;

