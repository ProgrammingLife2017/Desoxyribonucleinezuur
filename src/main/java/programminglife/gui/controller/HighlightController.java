package programminglife.gui.controller;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;
import programminglife.gui.NumbersOnlyListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller class for the highlights.
 */
public class HighlightController {
    private static final Color HIGHLIGHT_MIN_MAX_COLOR = Color.HOTPINK;

    private GraphController graphController;

    @FXML private TextField txtSearchGenomes;
    @FXML private ListView<String> lstUnselectedGenomes;
    @FXML private ListView<String> lstSelectedGenomes;
    @FXML private Button btnSelectGenomes;
    @FXML private Button btnUnselectGenomes;

    @FXML private TextField txtMin;
    @FXML private TextField txtMax;
    @FXML private CheckBox checkMin;
    @FXML private CheckBox checkMax;

    private Collection<String> genomes;

    /**
     * Initialize method for HighlightController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        genomes = new ArrayList<>();
        btnUnselectGenomes.setDisable(true);

        lstUnselectedGenomes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        lstSelectedGenomes.setOnMouseClicked(event -> {
            lstUnselectedGenomes.getSelectionModel().clearSelection();
            if (event.getClickCount() >= 2) {
                unselectGenomes(event);
            }
        });

        lstUnselectedGenomes.setOnMouseClicked(event -> {
            lstSelectedGenomes.getSelectionModel().clearSelection();
            if (event.getClickCount() >= 2) {
                selectGenomes(event);
            }
        });

        lstSelectedGenomes.getItems().addListener((ListChangeListener<String>) c -> {
           if (lstSelectedGenomes.getItems().isEmpty()) {
               btnUnselectGenomes.setDisable(true);
           } else {
               btnUnselectGenomes.setDisable(false);
           }

           this.highlight();
        });

        lstUnselectedGenomes.getItems().addListener((ListChangeListener<String>) c -> {
            if (lstUnselectedGenomes.getItems().isEmpty()) {
                btnSelectGenomes.setDisable(true);
            } else {
                btnSelectGenomes.setDisable(false);
            }
        });

        txtSearchGenomes.textProperty().addListener(this::search);
        btnSelectGenomes.setOnMouseClicked(this::selectGenomes);
        btnUnselectGenomes.setOnMouseClicked(this::unselectGenomes);
    }

    private void highlight() {
        // Highlight multiple genomes
        int numberOfGenomes = lstSelectedGenomes.getItems().size();
        Color[] colors = generateColors(numberOfGenomes);
        graphController.removeHighlight();
        for (int i = 0; i < numberOfGenomes; i++) {
            String genomeName = lstSelectedGenomes.getItems().get(i);
            int genomeID = graphController.getGraph().getGenomeID(genomeName);

            System.out.println("Highlighting " + genomeName + " (" + genomeID + ") with color " + colors[i]);
            graphController.highlightByGenome(genomeID, colors[i]);
        }
    }

    private Color[] generateColors(int n)
    {
        Color[] colors = new Color[n];
        for(int i = 0; i < n; i++) {
            colors[i] = Color.hsb(i / (double) n, 0.85f, 1.0f);
            System.out.println("Color " + i + " " + colors[i]);
        }
        return colors;
    }

    private void unselectGenomes(MouseEvent mouseEvent) {
        List<String> selection = lstSelectedGenomes.getSelectionModel().getSelectedItems();
        lstSelectedGenomes.getItems().removeAll(selection);

        this.search(txtSearchGenomes.textProperty(), null, this.txtSearchGenomes.getText());
    }

    private void selectGenomes(MouseEvent mouseEvent) {
        List<String> selection = lstUnselectedGenomes.getSelectionModel().getSelectedItems();
        lstSelectedGenomes.getItems().addAll(selection);
        lstUnselectedGenomes.getItems().removeAll(selection);
    }

    private void search(Observable o, String oldValue, String query) {
        lstUnselectedGenomes.getItems().clear();

        Set<String> unselected = new LinkedHashSet<>(genomes);
        unselected.removeAll(lstSelectedGenomes.getItems());

        if (query == null || query.isEmpty()) {
            lstUnselectedGenomes.getItems().addAll(unselected);
        } else {
            lstUnselectedGenomes.getItems().addAll(unselected.stream()
                    .filter(s -> match(query, s))
                    .collect(Collectors.toList()));
        }
    }

    private boolean match(String query, String element) {
        return element != null && element.contains(query);
    }

    /**
     * Initializes the buttons.
     */
//    private void initButtons() {
//        btnHighlight.setOnAction(event -> {
//            if (txtGenome.getText().isEmpty()) {
//                highlightMinMax();
//            } else {
//                if (txtGenome.getEntries().contains(txtGenome.getText())) {
//                    int genomeID = graphController.getGraph().getGenomeID(txtGenome.getText());
//                    graphController.highlightByGenome(genomeID);
//                }
//            }
//        });
//    }

    /**
     * Initializes the genomes.
     */
    public void initGenome() {
        genomes = this.graphController.getGraph().getGenomeNames();
        lstSelectedGenomes.getItems().clear();
        txtSearchGenomes.setText("");
        search(txtSearchGenomes.textProperty(), null, txtSearchGenomes.getText());
    }

    /**
     * Initializes the Min and Max field + checkbox.
     */
    public void initMinMax() {
        checkMax.selectedProperty().addListener((observable, oldValue, newValue) ->
                txtMax.setDisable(!checkMax.isSelected()));

        checkMin.selectedProperty().addListener((observable, oldValue, newValue) ->
                txtMin.setDisable(!checkMin.isSelected()));

        txtMax.setDisable(true);
        txtMax.textProperty().addListener(new NumbersOnlyListener(txtMax));
        txtMax.setText(Integer.toString(graphController.getGraph().getTotalGenomeNumber()));

        txtMin.setDisable(true);
        txtMin.textProperty().addListener(new NumbersOnlyListener(txtMin));
        txtMin.setText("0");
    }

    /**
     * Checks the Min en Max to see if things need to be highlighted.
     */
    private void highlightMinMax() {
        int minGenome = 0;
        int maxGenome = Integer.MAX_VALUE;
        if (checkMin.isSelected()) {
            minGenome = Integer.valueOf(txtMin.getText());
        }
        if (checkMax.isSelected()) {
            maxGenome = Integer.valueOf(txtMax.getText());
        }

        if (checkMax.isSelected() || checkMin.isSelected()) {
            graphController.highlightMinMax(minGenome, maxGenome, HIGHLIGHT_MIN_MAX_COLOR);
        } else {
            graphController.highlightMinMax(0, 0, null);
        }

    }

    /**
     * Sets the guicontroller for controlling the menu.
     * Is used to call the graphController
     *
     * @param graphController The gui controller
     */
    void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }
}
