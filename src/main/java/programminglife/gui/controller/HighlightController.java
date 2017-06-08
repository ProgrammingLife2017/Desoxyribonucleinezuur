package programminglife.gui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import programminglife.utility.NumbersOnlyListener;

/**
 * Created by iwanh on 08/06/2017.
 */
public class HighlightController {


    @FXML private Button btnClose;
    @FXML private Button btnHighlight;
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
        initMinMax();
    }


    /**
     * Initializes the buttons.
     */
    private void initButtons() {

        btnHighlight.setOnAction(event -> {
            highlightMinMax();
        });

        btnClose.setOnAction(event -> ((Stage) btnClose.getScene().getWindow()).close());
    }

    /**
     * Initializes the Min and Max field + checkbox.
     */
    private void initMinMax() {

        checkMax.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                txtMax.setDisable(!checkMax.isSelected());
            }
        });

        checkMin.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                txtMin.setDisable(!checkMin.isSelected());
            }
        });

        txtMax.setDisable(true);
        txtMax.textProperty().addListener(new NumbersOnlyListener(txtMax));

        txtMin.setDisable(true);
        txtMin.textProperty().addListener(new NumbersOnlyListener(txtMin));

    }

    /**
     * Checks the Min en Max to see if things need to be highlighted.
     */
    private void highlightMinMax(){
        int minGenome = 0;
        int maxGenome = Integer.MAX_VALUE;
        if (checkMin.isSelected()) {
            minGenome = Integer.valueOf(txtMin.getText());
        }
        if (checkMax.isSelected()) {
            maxGenome = Integer.valueOf(txtMax.getText());
        }



    }

}
