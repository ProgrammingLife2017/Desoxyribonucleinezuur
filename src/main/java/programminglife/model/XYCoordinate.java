package programminglife.model;

/**
 * Created by Martijn van Meerten and Toine Hartman on 9-5-2017.
 * Class representing a 2D coordinate.
 */
public class XYCoordinate {
    private int x;
    private int y;

    /**
     * Constructor for coordinate
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public XYCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Substitue for constructor.
     * //TODO: remove
     * @param xOffest x coord
     * @param yOffset y coord
     * @return a XCoordinate
     */
    public static XYCoordinate coord(int xOffest, int yOffset) {
        return new XYCoordinate(xOffest, yOffset);
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

    public int getX() {
        return x;
    }

    public XYCoordinate setX(int x) {
        this.x = x;

        return this;
    }

    public int getY() {
        return y;
    }

    public XYCoordinate setY(int y) {
        this.y = y;

        return this;
    }
}
