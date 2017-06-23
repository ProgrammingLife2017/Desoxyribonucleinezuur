package programminglife.model;

/**
 * Created by Martijn van Meerten and Toine Hartman on 9-5-2017.
 * Class representing a 2D coordinate.
 */
public class XYCoordinate {
    private double x;
    private double y;

    /**
     * Constructor for X-Y coordinate.
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public XYCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sum coordinates.
     * @param other the coordinate to add to this one
     * @return the sum
     */
    public XYCoordinate add(XYCoordinate other) {
        return new XYCoordinate(this.getX() + other.getX(), this.getY() + other.getY());
    }

    /**
     * Add to x and y coordinate.
     * @param x The value to add to x
     * @param y The value to add to y
     * @return the new {@link XYCoordinate}
     */
    public XYCoordinate add(double x, double y) {
        return new XYCoordinate(this.getX() + x, this.getY() + y);
    }

    /**
     * Multiply the coordinate with a factor.
     * @param factor the factor
     * @return the new coordinate
     */
    public XYCoordinate multiply(double factor) {
        return new XYCoordinate((int) (this.getX() * factor), (int) (this.getY() * factor));
    }

    /**
     * Get the X coordinate.
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Set the X coordinate.
     * @param x the value to set
     * @return the {@link XYCoordinate} for chaining
     */
    public XYCoordinate setX(double x) {
        this.x = x;

        return this;
    }

    /**
     * Get the Y coordinate.
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * Set the Y coordinate.
     * @param y the value to set
     * @return the {@link XYCoordinate} for chaining
     */
    public XYCoordinate setY(double y) {
        this.y = y;

        return this;
    }

    @Override
    public String toString() {
        return "(" + this.getX() + ", " + this.getY() + ")";
    }

    public void set(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * Equals method for XYCoordinate
     * @param other the object to compare with.
     * @return boolean true if equals, false if not equal.
     */
    public boolean equals(Object other) {
        if (other instanceof XYCoordinate){
            XYCoordinate that = (XYCoordinate) other;
            return this.getX() == that.getX() && this.getY() == that.getY();
        }
        return false;
    }
}
