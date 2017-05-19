package programminglife.utility;

import java.util.Observable;

/**
 * Keeps track of how many lines are read from the file, percentage of total.
 */
public class FileProgressCounter extends Observable {
    private int lineCount;
    private int totalLineCount;
    private String description;

    /**
     * Constructor with description.
     * @param description String.
     */
    public FileProgressCounter(String description) {
        this(Integer.MAX_VALUE, description);
    }

    /**
     * Constructor with description and integer of line count.
     * @param totalLineCount int.
     * @param description String.
     */
    public FileProgressCounter(int totalLineCount, String description) {
        this.lineCount = 0;
        this.totalLineCount = totalLineCount;
        this.description = description;
    }

    /**
     * Counter for the progress.
     */
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

    /**
     * Calculates how much percent has been loaded.
     * @return percentage of file parsed.
     */
    private double percentage() {
        return this.lineCount * 100.d / this.totalLineCount;
    }

    public int getLineCount() {
        return lineCount;
    }
}
