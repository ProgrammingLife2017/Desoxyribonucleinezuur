package programminglife.parser;

import java.util.Observable;

/**
 * Keeps track of how many lines are read from the file, percentage of total.
 */
public class ProgressCounter extends Observable {
    private int progress;
    private int total;
    private String description;

    /**
     * Constructor with description.
     * @param description String.
     */
    public ProgressCounter(String description) {
        this(Integer.MAX_VALUE, description);
    }

    /**
     * Constructor with description and integer of line count.
     * @param total int.
     * @param description String.
     */
    public ProgressCounter(int total, String description) {
        this.progress = 0;
        this.total = total;
        this.description = description;

        this.setChanged();
        this.notifyObservers(this);
    }

    /**
     * Counter for the progress.
     */
    public void count() {
        this.progress++;

        this.setChanged();
        this.notifyObservers(this);
    }

    public void setTotal(int total) {
        this.total = total;
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
        return this.progress / (double) this.total;
    }

    public int getProgress() {
        return progress;
    }

    /**
     * set the line count to the max so that it is 100%.
     * @param count int equal to the total
     */
    private void setProgress(int count) {
        this.progress = count;

        this.setChanged();
        this.notifyObservers(this);
    }

    /**
     * Tells the ProgressBar loading is finished so it becomes invisible.
     */
    public void finished() {
        this.setProgress(this.total);
    }

    /**
     * Resets the progressCounter.
     */
    public void reset() {
        this.setProgress(0);
    }
}
