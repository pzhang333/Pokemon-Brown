package cs.brown.edu.aelp.util;

/**
 * A Pair object holds exactly two objects and is immutable. However, the
 * objects returned by one() and two() are not copies and may be mutated.
 *
 * @param <X>
 *          the type of the first object in this Pair
 * @param <Y>
 *          the type of the second object in this Pair
 *
 * @author Louis Kilfoyle
 */
public class Pair<X, Y> {

  private final X one;
  private final Y two;

  /**
   * Create a new Pair object.
   *
   * @param a
   *          an object of type X to be stored in this Pair
   * @param b
   *          an object of type Y to be stored in this Pair
   */
  public Pair(X a, Y b) {
    this.one = a;
    this.two = b;
  }

  @Override
  public String toString() {
    return String.format("Pair Object: [%s, %s]", this.one.toString(),
        this.two.toString());
  }

  /**
   * Get a direct reference to the first object from this Pair.
   *
   * @return the object of type X used to construct this Pair
   */
  public X one() {
    return one;
  }

  /**
   * Get a direct reference to the second object from this Pair.
   *
   * @return the object of type Y used to construct this Pair
   */
  public Y two() {
    return two;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((one == null) ? 0 : one.hashCode());
    result = prime * result + ((two == null) ? 0 : two.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("rawtypes")
    Pair other = (Pair) obj;
    if (one == null) {
      if (other.one != null) {
        return false;
      }
    } else if (!one.equals(other.one)) {
      return false;
    }
    if (two == null) {
      if (other.two != null) {
        return false;
      }
    } else if (!two.equals(other.two)) {
      return false;
    }
    return true;
  }

}
