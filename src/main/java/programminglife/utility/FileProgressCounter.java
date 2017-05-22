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

        this.setChanged();
        this.notifyObservers(this);
    }

    /**
     * Counter for the progress.
     */
    public void count() {
        this.lineCount++;

        this.setChanged();
        this.notifyObservers(this);
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
    public double percentage() {
        return this.lineCount / (double) this.totalLineCount;
    }

    public int getLineCount() {
        return lineCount;
    }

    /**
     * set the line count to the max so that it is 100%.
     * @param count int equal to the totalLineCount
     */
    private void setLineCount(int count) {
        this.lineCount = count;

        this.setChanged();
        this.notifyObservers(this);
    }

    /**
     * Tells the ProgressBar loading is finished so it becomes invisible.
     */
    public void finished() {
        this.setLineCount(this.totalLineCount);
    }
}
