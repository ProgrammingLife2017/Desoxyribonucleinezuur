package programminglife.gui.controller;

import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import programminglife.gui.NumbersOnlyListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller class for the highlights.
 */
public class HighlightController {
    private static final Color HIGHLIGHT_MIN_MAX_COLOR = Color.YELLOW;

    private GraphController graphController;

    @FXML private TextField txtSearchGenomes;
    @FXML private Hyperlink lnkClearSearch;
    @FXML private ListView<String> lstUnselectedGenomes;
    @FXML private ListView<String> lstSelectedGenomes;
    @FXML private Button btnSelectGenomes;
    @FXML private Button btnUnselectGenomes;

    @FXML private TextField txtMin;
    @FXML private TextField txtMax;
    @FXML private CheckBox checkMin;
    @FXML private CheckBox checkMax;

    private Collection<String> genomes;
    private Color[] genomeColors;

    /**
     * Initialize method for HighlightController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        genomes = new ArrayList<>();
        btnUnselectGenomes.setDisable(true);

        lstUnselectedGenomes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lstSelectedGenomes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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

        lstSelectedGenomes.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t);
                            setStyle(String.format("-fx-background-color: %s;",
                                    hex(genomeColors[graphController.getGraph().getGenomeID(t)])));
                        } else {
                            setText("");
                            setStyle("");
                        }
                    }
                };
            }
        });

        lnkClearSearch.setOnMouseClicked(event -> txtSearchGenomes.clear());

        txtMax.textProperty().addListener((observable, oldValue, newValue) -> this.highlight());
        txtMin.textProperty().addListener((observable, oldValue, newValue) -> this.highlight());
        checkMax.selectedProperty().addListener((observable, oldValue, newValue) -> this.highlight());
        checkMin.selectedProperty().addListener((observable, oldValue, newValue) -> this.highlight());
        txtSearchGenomes.textProperty().addListener(this::search);
        btnSelectGenomes.setOnMouseClicked(this::selectGenomes);
        btnUnselectGenomes.setOnMouseClicked(this::unselectGenomes);
    }

    /**
     * Method to return the string of a colour.
     *
     * @param c Color to be converted.
     * @return String of the colour.
     */
    private String hex(Color c) {
        return "#" + Integer.toHexString(c.hashCode()).substring(0, 6).toUpperCase() + "B4";
    }

    /**
     * Method to highlight certain genomes.
     */
    void highlight() {
        // Highlight multiple genomes
        int numberOfGenomes = lstSelectedGenomes.getItems().size();
        graphController.removeHighlight();
        for (int i = 0; i < numberOfGenomes; i++) {
            String genomeName = lstSelectedGenomes.getItems().get(i);
            int genomeID = graphController.getGraph().getGenomeID(genomeName);
            graphController.highlightByGenome(genomeID, genomeColors[genomeID]);
        }

        highlightMinMax();
    }

    /**
     * Method to generate an array of colours to be used.
     *
     * @param n the amount of colours to be generated.
     * @return Color[] array which contains the colours.
     */
    private Color[] generateColors(int n) {
        Color[] colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = Color.hsb(i * 360.d / n, 0.6d, 1.d);
        }
        return colors;
    }

    /**
     * Method to remove the item from the list.
     *
     * @param mouseEvent MouseEvent where clicked.
     */
    private void unselectGenomes(MouseEvent mouseEvent) {
        List<String> selection = lstSelectedGenomes.getSelectionModel().getSelectedItems();
        lstSelectedGenomes.getItems().removeAll(selection);

        this.search(txtSearchGenomes.textProperty(), null, this.txtSearchGenomes.getText());
    }

    /**
     * Method to add the item to the list.
     *
     * @param mouseEvent MouseEvent where clicked.
     */
    private void selectGenomes(MouseEvent mouseEvent) {
        List<String> selection = lstUnselectedGenomes.getSelectionModel().getSelectedItems();
        lstSelectedGenomes.getItems().addAll(selection);
        lstUnselectedGenomes.getItems().removeAll(selection);
    }

    /**
     * Method to search through the items selected.
     *
     * @param o Observable to keep track of what happens.
     * @param oldValue String of the previous value.
     * @param query Query of the String.
     */
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

    /**
     * Method to match the query to the element.
     *
     * @param query Query to be processed.
     * @param element Element to be checked with the query.
     * @return Boolean if it is a match.
     */
    private boolean match(String query, String element) {
        return element != null && element.contains(query);
    }

    /**
     * Initializes the genomes.
     */
    void initGenome() {
        genomes = this.graphController.getGraph().getGenomeNames();
        genomeColors = generateColors(graphController.getGraph().getTotalGenomeNumber());
        lstSelectedGenomes.getItems().clear();
        txtSearchGenomes.setText("");
        search(txtSearchGenomes.textProperty(), null, txtSearchGenomes.getText());
    }

    /**
     * Initializes the Min and Max field + checkbox.
     */
    void initMinMax() {
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

    public Color[] getGenomeColors() {
        return genomeColors;
    }

    public Collection<Integer> getSelectedGenomes() {
        return lstSelectedGenomes.getItems().stream()
                .map(graphController.getGraph()::getGenomeID)
                .collect(Collectors.toSet());
    }
}
