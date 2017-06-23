package programminglife.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * {@link ChangeListener} to make a {@link TextField} only accept numbers.
 */
public class NumbersOnlyListener implements ChangeListener<String> {
    private final TextField tf;

    /**
     * Constructor for the Listener.
     *
     * @param tf {@link TextField} is the text field on which the listener listens
     */
    public NumbersOnlyListener(TextField tf) {
        this.tf = tf;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d+")) {
            tf.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
}
