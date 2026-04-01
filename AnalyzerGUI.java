import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class AnalyzerGUI extends Application {

    private final ObservableList<StudentRecord> masterData = FXCollections.observableArrayList();
    private XYChart.Series<String, Number> chartSeries;
    private Label cwaLabel;
    private Label cwaSubLabel;
    private Label recordCountLabel;
    private static final String BG_DARK       = "#0f1117";
    private static final String CARD_BG       = "#1a1d27";
    private static final String CARD_BORDER   = "#2a2d3e";
    private static final String ACCENT_GREEN  = "#00c96e";
    private static final String ACCENT_BLUE   = "#3b82f6";
    private static final String ACCENT_PURPLE = "#a855f7";
    private static final String TEXT_PRIMARY  = "#e2e8f0";
    private static final String TEXT_MUTED    = "#64748b";

    @Override
    public void start(Stage stage) {

        VBox header = buildHeader();

        TextField nameField = styledField("Student Full Name");
        TextField idField   = styledField("Student ID (e.g. UG0012345)");

        ComboBox<String> facultyBox   = styledCombo("Select Faculty");
        ComboBox<String> programmeBox = styledCombo("Select Programme");
        ComboBox<String> courseBox    = styledCombo("Select Course");
        TextField scoreField  = styledField("Score (0 – 100)");
        TextField creditField = styledField("Credits (1 – 4)");

        facultyBox.setItems(FXCollections.observableArrayList(
                "Computing and Math (FCMS)",
                "Mining and Minerals (FMMT)"
        ));

        Map<String, List<String>> progMap = new HashMap<>();
        progMap.put("Computing and Math (FCMS)",   Arrays.asList("BSc Computer Science", "BSc Data Science and Analytics"));
        progMap.put("Mining and Minerals (FMMT)",  Arrays.asList("BSc Mining Engineering", "BSc Minerals Engineering"));

        Map<String, List<String>> courseMap = new HashMap<>();
        courseMap.put("BSc Computer Science",            Arrays.asList("Data Structures", "Operating Systems", "Software Engineering", "Algorithms", "Programming with R", "Programming with java", "Academic writing", "Differential equations", "Big data", "Discrete maths"));
        courseMap.put("BSc Data Science and Analytics",  Arrays.asList("Programming with R", "Programming with java", "calculus I", "Advanced database", "calculus II", "SQL", "Academic writing", "Differential equations", "Big data", "Data mining","Discrete maths", "Machine Learning", "Fundamentals of Big Data", "Data Visualization"));
        courseMap.put("BSc Mining Engineering",           Arrays.asList("Surface Mining", "Rock Mechanics", "Mine Surveying", "Drilling and Blasting"));
        courseMap.put("BSc Minerals Engineering",         Arrays.asList("Mineral Processing", "Hydrometallurgy", "Assaying"));

        facultyBox.setOnAction(e -> {
            if (facultyBox.getValue() != null) {
                programmeBox.setItems(FXCollections.observableArrayList(progMap.get(facultyBox.getValue())));
                courseBox.getItems().clear();
            }
        });
        programmeBox.setOnAction(e -> {
            if (programmeBox.getValue() != null)
                courseBox.setItems(FXCollections.observableArrayList(courseMap.get(programmeBox.getValue())));
        });

        GridPane identityGrid = new GridPane();
        identityGrid.setHgap(12); identityGrid.setVgap(12);
        identityGrid.addRow(0, labelFor("Full Name"), nameField, labelFor("Student ID"), idField);
        identityGrid.addRow(1, labelFor("Faculty"),   facultyBox, labelFor("Programme"), programmeBox);
        GridPane.setHgrow(nameField,   Priority.ALWAYS);
        GridPane.setHgrow(idField,     Priority.ALWAYS);
        GridPane.setHgrow(facultyBox,  Priority.ALWAYS);
        GridPane.setHgrow(programmeBox,Priority.ALWAYS);
        VBox identityCard = card("🎓  Student Identity", identityGrid);

        Button addBtn = new Button("＋  Add Record");
        addBtn.setStyle(
                "-fx-background-color: " + ACCENT_GREEN + ";" +
                        "-fx-text-fill: #0f1117;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 22;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(addBtn.getStyle().replace(ACCENT_GREEN, "#00a85a")));
        addBtn.setOnMouseExited(e  -> addBtn.setStyle(addBtn.getStyle().replace("#00a85a", ACCENT_GREEN)));

        Button clearBtn = new Button("🗑  Clear All");
        clearBtn.setStyle(
                "-fx-background-color: #ef4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 18;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        GridPane courseGrid = new GridPane();
        courseGrid.setHgap(12); courseGrid.setVgap(12);
        courseGrid.addRow(0, labelFor("Course"), courseBox, labelFor("Score"), scoreField, labelFor("Credits"), creditField);
        GridPane.setHgrow(courseBox,    Priority.ALWAYS);
        GridPane.setHgrow(scoreField,   Priority.ALWAYS);
        GridPane.setHgrow(creditField,  Priority.ALWAYS);

        HBox btnRow = new HBox(10, addBtn, clearBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox courseCard = card("📚  Course Entry", new VBox(14, courseGrid, btnRow));

        cwaLabel     = new Label("0.00");
        cwaLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_GREEN + ";");

        cwaSubLabel  = new Label("Cumulative Weighted Average");
        cwaSubLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

        recordCountLabel = new Label("0 records");
        recordCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        VBox cwaBox = new VBox(4, cwaLabel, cwaSubLabel, recordCountLabel);
        cwaBox.setAlignment(Pos.CENTER);
        cwaBox.setPadding(new Insets(16, 24, 16, 24));
        cwaBox.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-border-color: " + ACCENT_GREEN + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;"
        );
        cwaBox.setMinWidth(220);

        // ── Stats mini-cards ──────────────────────────────────────────────────
        Label highLabel = buildStatCard("Highest Score", "—", ACCENT_BLUE);
        Label lowLabel  = buildStatCard("Lowest Score",  "—", "#ef4444");
        Label avgLabel  = buildStatCard("Average Score", "—", ACCENT_PURPLE);

        HBox statsRow = new HBox(12, wrapStatCard("Highest Score", highLabel, ACCENT_BLUE),
                wrapStatCard("Lowest Score",  lowLabel,  "#ef4444"),
                wrapStatCard("Average Score", avgLabel,  ACCENT_PURPLE));
        statsRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(statsRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(statsRow.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(statsRow.getChildren().get(2), Priority.ALWAYS);

        HBox topMetrics = new HBox(20, cwaBox, statsRow);
        topMetrics.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statsRow, Priority.ALWAYS);

        TableView<StudentRecord> table = buildTable();
        table.setItems(masterData);
        table.setPrefHeight(220);
        VBox tableCard = card("📋  Course Records", table);


        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFill(Color.web(TEXT_MUTED));
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setTickLabelFill(Color.web(TEXT_MUTED));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Performance Trend");
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        barChart.setPrefHeight(250);
        barChart.setStyle("-fx-background-color: transparent;");

        chartSeries = new XYChart.Series<>();
        barChart.getData().add(chartSeries);
        VBox chartCard = card("📊  Score Chart", barChart);

        addBtn.setOnAction(e -> {
            try {
                String course = courseBox.getValue();
                if (course == null || course.isBlank()) throw new IllegalArgumentException();
                double score  = Double.parseDouble(scoreField.getText().trim());
                int    credits = Integer.parseInt(creditField.getText().trim());
                if (score < 0 || score > 100 || credits < 1 || credits > 4) throw new NumberFormatException();

                String grade = gradeFor(score);
                masterData.add(new StudentRecord(course, score, grade, credits));
                updateDashboard(barChart, highLabel, lowLabel, avgLabel);
                scoreField.clear();
                creditField.clear();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Please fill all fields correctly.\n• Score: 0 – 100\n• Credits: 1 – 4",
                        ButtonType.OK);
                alert.setHeaderText("Input Error");
                alert.showAndWait();
            }
        });

        clearBtn.setOnAction(e -> {
            masterData.clear();
            updateDashboard(barChart, highLabel, lowLabel, avgLabel);
        });


        VBox root = new VBox(20,
                header,
                identityCard,
                courseCard,
                topMetrics,
                tableCard,
                chartCard
        );
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG_DARK + "; -fx-background: " + BG_DARK + ";");

        Scene scene = new Scene(scroll, 1200, 900);
        applyGlobalCSS(scene);

        stage.setScene(scene);
        stage.setTitle("UMaT Academic Performance Portal");
        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.show();
    }


    private VBox buildHeader() {
        Label title = new Label("UMaT Academic Performance Portal");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        Label sub = new Label("University of Mines and Technology  •  Student Grade Tracker");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

        VBox box = new VBox(4, title, sub);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(18, 24, 18, 24));
        box.setStyle(
                "-fx-background-color: linear-gradient(to right, #1a1d27, #1e2235);" +
                        "-fx-border-color: " + CARD_BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;"
        );
        return box;
    }

    private VBox card(String title, javafx.scene.Node content) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-padding: 0 0 10 0;");
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + CARD_BORDER + ";");

        VBox box = new VBox(10, heading, sep, content);
        box.setPadding(new Insets(18));
        box.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-border-color: " + CARD_BORDER + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );
        return box;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(fieldStyle());
        tf.setMinWidth(140);
        return tf;
    }

    private ComboBox<String> styledCombo(String prompt) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle(fieldStyle());
        return cb;
    }

    private String fieldStyle() {
        return "-fx-background-color: #252836;" +
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 12;";
    }

    private Label labelFor(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
        return l;
    }

    /** Small number label used inside stat cards */
    private Label buildStatCard(String title, String value, String color) {
        Label l = new Label(value);
        l.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        return l;
    }

    private VBox wrapStatCard(String title, Label valueLabel, String accentColor) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        VBox box = new VBox(4, valueLabel, t);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );
        return box;
    }

    private TableView<StudentRecord> buildTable() {
        TableView<StudentRecord> table = new TableView<>();
        table.setStyle("-fx-background-color: transparent; -fx-table-cell-border-color: " + CARD_BORDER + ";");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StudentRecord, String> cCol = new TableColumn<>("Course");
        cCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        cCol.setPrefWidth(280);

        TableColumn<StudentRecord, Double> sCol = new TableColumn<>("Score");
        sCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        sCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                setText(String.format("%.1f", item));
                String color = item >= 80 ? ACCENT_GREEN : item >= 70 ? ACCENT_BLUE : item >= 60 ? ACCENT_PURPLE : "#ef4444";
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });

        TableColumn<StudentRecord, String> gCol = new TableColumn<>("Grade");
        gCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText("  " + item + "  ");
                String bg = switch (item) {
                    case "Excellent" -> ACCENT_GREEN;
                    case "Very Good" -> ACCENT_BLUE;
                    case "Good"      -> ACCENT_PURPLE;
                    default          -> "#ef4444";
                };
                setStyle("-fx-background-color: " + bg + "22; -fx-text-fill: " + bg +
                        "; -fx-font-weight: bold; -fx-background-radius: 4;");
            }
        });

        TableColumn<StudentRecord, Integer> crCol = new TableColumn<>("Credits");
        crCol.setCellValueFactory(new PropertyValueFactory<>("credits"));

        table.getColumns().addAll(cCol, sCol, gCol, crCol);
        return table;
    }

    private void updateDashboard(BarChart<String, Number> barChart,
                                 Label highLabel, Label lowLabel, Label avgLabel) {
        chartSeries.getData().clear();
        double wSum = 0, total = 0;
        int tCred = 0;
        double high = Double.MIN_VALUE, low = Double.MAX_VALUE;

        for (StudentRecord r : masterData) {
            chartSeries.getData().add(new XYChart.Data<>(r.getCourse(), r.getScore()));
            wSum  += r.getScore() * r.getCredits();
            total += r.getScore();
            tCred += r.getCredits();
            if (r.getScore() > high) high = r.getScore();
            if (r.getScore() < low)  low  = r.getScore();
        }

        int n = masterData.size();
        if (tCred > 0) {
            cwaLabel.setText(String.format("%.2f", wSum / tCred));
            highLabel.setText(String.format("%.1f", high));
            lowLabel.setText(String.format("%.1f", low));
            avgLabel.setText(String.format("%.1f", total / n));
        } else {
            cwaLabel.setText("0.00");
            highLabel.setText("—");
            lowLabel.setText("—");
            avgLabel.setText("—");
        }
        recordCountLabel.setText(n + (n == 1 ? " record" : " records"));
    }

    private String gradeFor(double score) {
        if (score >= 80) return "Excellent";
        if (score >= 70) return "Very Good";
        if (score >= 60) return "Good";
        return "Pass";
    }

    private void applyGlobalCSS(Scene scene) {
        scene.getStylesheets().add("data:text/css," +
                ".chart-plot-background{-fx-background-color:#1a1d27;}" +
                ".chart-vertical-grid-lines{-fx-stroke:#2a2d3e;}" +
                ".chart-horizontal-grid-lines{-fx-stroke:#2a2d3e;}" +
                ".chart-title{-fx-text-fill:#e2e8f0;-fx-font-size:14px;}" +
                ".axis-label{-fx-text-fill:#64748b;}" +
                ".chart-bar{-fx-bar-fill:" + ACCENT_BLUE + ";}" +
                ".table-row-cell{-fx-background-color:#1a1d27;-fx-border-color:#2a2d3e;}" +
                ".table-row-cell:selected{-fx-background-color:#252836;}" +
                ".table-row-cell:odd{-fx-background-color:#1e2135;}" +
                ".column-header-background{-fx-background-color:#252836;}" +
                ".column-header,.nested-column-header{-fx-background-color:#252836;-fx-text-fill:#e2e8f0;}" +
                ".table-column{-fx-text-fill:#e2e8f0;}" +
                ".scroll-bar:vertical,.scroll-bar:horizontal{-fx-background-color:#1a1d27;}" +
                ".scroll-bar .thumb{-fx-background-color:#2a2d3e;-fx-background-radius:4;}" +
                ".separator .line{-fx-border-color:#2a2d3e;}" +
                ".combo-box-popup .list-view{-fx-background-color:#252836;}" +
                ".combo-box-popup .list-cell{-fx-background-color:#252836;-fx-text-fill:#e2e8f0;}" +
                ".combo-box-popup .list-cell:hover{-fx-background-color:#2a2d3e;}"
        );
    }

    public static void main(String[] args) { launch(args); }


    public static class StudentRecord {
        private final String course;
        private final String grade;
        private final double score;
        private final int    credits;

        public StudentRecord(String course, double score, String grade, int credits) {
            this.course  = course;
            this.score   = score;
            this.grade   = grade;
            this.credits = credits;
        }

        public String getCourse()  { return course;  }
        public double getScore()   { return score;   }
        public String getGrade()   { return grade;   }
        public int    getCredits() { return credits; }
    }
}