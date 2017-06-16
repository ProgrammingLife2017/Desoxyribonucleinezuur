package programminglife.util;

import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import programminglife.utility.InitFXThread;
import programminglife.utility.NumbersOnlyListener;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the NumberOnlyListener.
 */
public class NumberOnlyListenerTest {

    TextField tf;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitFXThread.setupClass();
    }

    @Before
    public void setUp(){
        tf = new TextField();
        tf.textProperty().addListener(new NumbersOnlyListener(tf));
    }



    @Test
    public void numberOnlyTest() {
        for (char c : "1234567890".toCharArray()) {
            tf.appendText(String.valueOf(c));
        }
        assertEquals("1234567890", tf.getText());
        tf.setText("");

        tf.appendText("121212");
        assertEquals("121212", tf.getText());

        tf.appendText("abc");
        assertEquals("121212", tf.getText());
    }
}
