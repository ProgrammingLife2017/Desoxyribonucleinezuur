package programminglife.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import programminglife.gui.AutoCompleteTextField;
import programminglife.utility.NumbersOnlyListener;

/**
 * Controller class for the highlights.
 */
public class HighlightController {

    private static final Color HIGHLIGHT_MIN_MAX_COLOR = Color.HOTPINK;
    private GraphController graphController;

    @FXML private Button btnClose;
    @FXML private Button btnHighlight;
    @FXML private AutoCompleteTextField txtGenome;
    @FXML private TextField txtMin;
    @FXML private TextField txtMax;
    @FXML private CheckBox checkMin;
    @FXML private CheckBox checkMax;



    /**
     * Initialize method for HighlightController.
     */
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        initButtons();
    }


    /**
     * Initializes the buttons.
     */
    private void initButtons() {

        btnHighlight.setOnAction(event -> {
            if (txtGenome.getText().isEmpty()) {
                highlightMinMax();
            } else {
                if (txtGenome.getEntries().contains(txtGenome.getText())) {
                    int genomeID = graphController.getGraph().getGenomeID(txtGenome.getText());
                    graphController.highlightByGenome(genomeID);
                }
            }
        });

        btnClose.setOnAction(event -> ((Stage) btnClose.getScene().getWindow()).close());
    }

    /**
     * Initializes the genomes.
     */
    public void initGenome() {
        txtGenome.getEntries().addAll(graphController.getGraph().getGenomeNames());
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
     * @param graphController The gui controller
     */
    void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }
}
