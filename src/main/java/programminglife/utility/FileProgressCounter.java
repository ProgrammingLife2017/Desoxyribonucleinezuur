package programminglife.utility;

import java.util.Observable;

/**
 * Created by toinehartman on 18/05/2017.
 */
public class FileProgressCounter extends Observable {
    private int lineCount;
    private int totalLineCount;
    private String description;

    public FileProgressCounter(String description) {
        this(Integer.MAX_VALUE, description);
    }

    public FileProgressCounter(int totalLineCount, String description) {
        this.lineCount = 0;
        this.totalLineCount = totalLineCount;
        this.description = description;
    }

    public void count() {
        this.lineCount++;
    }

    public void setTotalLineCount(int totalLineCount) {
        this.totalLineCount = totalLineCount;
    }

    @Override
    public String toString() {
        return String.format("%s: %f%%", this.description, this.percentage());
    }

    private double percentage() {
        return this.lineCount * 100.d / this.totalLineCount;
    }

    public int getLineCount() {
        return lineCount;
    }
}
