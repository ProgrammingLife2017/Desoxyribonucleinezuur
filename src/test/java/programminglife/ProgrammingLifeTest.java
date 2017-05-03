package programminglife;

import org.junit.Test;

/**
 * Created by toinehartman on 03/05/2017.
 */
public class ProgrammingLifeTest {

    @Test
    public void existingFileTest() {
        ProgrammingLife.main(new String[] {"data/test/test.gfa"});
    }

    @Test
    public void nonexistingFileTest() {
        ProgrammingLife.main(new String[] {"data/test/best.gfa"});
    }

    @Test
    public void noFileTest() {
        ProgrammingLife.main(new String[] {});
    }
}
