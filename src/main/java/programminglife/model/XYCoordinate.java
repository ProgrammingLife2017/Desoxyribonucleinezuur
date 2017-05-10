package programminglife.model;

/**
 * Created by Martijn van Meerten and Toine Hartman on 9-5-2017.
 * Class representing a 2D coordinate.
 */
public class XYCoordinate {
    private int x;
    private int y;

    /**
     * Constructor for X-Y coordinate.
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public XYCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sum coords.
     * @param other the coord to add to this one
     * @return the sum
     */
    public XYCoordinate add(XYCoordinate other) {
        return new XYCoordinate(this.getX() + other.getX(), this.getY() + other.getY());
    }

    /**
     * Multiply the coord with a factor.
     * @param factor the factor
     * @return the new coord
     */
    public XYCoordinate multiply(int factor) {
        return new XYCoordinate(this.getX() * factor, this.getY() * factor);
    }

    /**
     * Get the X coordinate.
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Set the X coordinate.
     * @param x the value to set
     * @return the {@link XYCoordinate} for chaining
     */
    public XYCoordinate setX(int x) {
        this.x = x;

        return this;
    }

    /**
     * Get the Y coordinate.
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Set the Y coordinate.
     * @param y the value to set
     * @return the {@link XYCoordinate} for chaining
     */
    public XYCoordinate setY(int y) {
        this.y = y;

        return this;
    }
}
