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
  
 //Below we override isEqual (note that this still relies on the object we
 // input to have implemented the equals() function appropriately.
 // This is required by the Object class, but it is still worth noting.
 @Override
 public int hashCode() {
   return new Integer(this.getX()).hashCode() + new Integer(this.getY()).hashCode() 
       + new Integer(this.getZ()).hashCode();
 }

 @Override
 public boolean equals(Object obj) {
   try {
     Coordinate3d castedToPair = (Coordinate3d) obj;

     if (castedToPair.getX() == castedToPair.getX()
         && castedToPair.getY() == castedToPair.getY() 
         && castedToPair.getZ() == castedToPair.getZ()) {
       return true;
     }
     return false;
   } catch (Exception e) {
     System.out.print("ERROR: Casting issue (see use of Coordinate3d datastructure.");
     return false;
   }
 }

  /**
   * Gets the x property.
   *
   * @return The x property (int).
   */
  public int getX() {
    return this.x;
  }

  /**
   * Gets the y property.
   *
   * @return The y property (int).
   */
  public int getY() {
    return this.y;
  }

  /**
   * Gets the z property.
   *
   * @return The z property (int).
   */
  public int getZ() {
    return this.z;
  }
}
