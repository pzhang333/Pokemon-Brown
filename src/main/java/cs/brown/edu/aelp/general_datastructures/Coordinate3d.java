package cs.brown.edu.aelp.general_datastructures;

/**
 * Coordinate3d.java.
 *
 * @author abrevnov17
 */
public class Coordinate3d {

  private int x;
  private int y;
  private int z;

  /**
   * Constructor: Constructs a 3-dimensional coordinate from x,y,z components.
   *
   * @param x
   *          A int representing the x-component.
   * @param y
   *          A int representing the y-component.
   * @param z
   *          A int representing the z-component.
   */
  public Coordinate3d(int x, int y, int z) {
    // TODO Auto-generated constructor stub
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Gets the x property.
   *
   * @return The x property (int).
   */
  public double getX() {
    return this.x;
  }

  /**
   * Gets the y property.
   *
   * @return The y property (int).
   */
  public double getY() {
    return this.y;
  }

  /**
   * Gets the z property.
   *
   * @return The z property (int).
   */
  public double getZ() {
    return this.z;
  }
}
