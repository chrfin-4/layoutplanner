package se.ltu.kitting.model;

/**
 * Three dimensions. Could be a position in 3D space or lenghts along the
 * three dimensions.
 * This class is immutable.
 * @author Christoffer Fink
 */
public final class Dimensions implements Comparable<Dimensions> {

  public static final Dimensions ZERO = Dimensions.of(0,0,0);
  public static final Dimensions UNIT = Dimensions.of(1,1,1);
  public static final Dimensions MINUS = Dimensions.of(-1,-1,-1);

  public final int x,y,z;

  public static Dimensions dimensions(int x, int y) {
    return dimensions(x,y,0);
  }

  public static Dimensions dimensions(int x, int y, int z) {
    return new Dimensions(x, y, z);
  }

  public static Dimensions of(int x, int y) {
    return of(x,y,0);
  }

  public static Dimensions of(int x, int y, int z) {
    return new Dimensions(x, y, z);
  }

  public Dimensions(int x, int y) {
    this(x,y,0);
  }

  public Dimensions(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public int compareTo(Dimensions other) {
    return Double.compare(distance(ZERO, this), distance(ZERO, other));
  }

  public double distance(Dimensions p1, Dimensions p2) {
    int dx = p1.x-p2.x;
    int dy = p1.y-p2.y;
    int dz = p1.z-p2.z;
    return Math.sqrt(dx + dy + dz);
  }

  public Dimensions withX(int x) {
    return Dimensions.of(x, y, z);
  }

  public Dimensions withY(int y) {
    return Dimensions.of(x, y, z);
  }

  public Dimensions withZ(int z) {
    return Dimensions.of(x, y, z);
  }

  /**
   * Creates new dimensions by adding the other dimensions to these dimensions:
   * (x1,y1,z1).plus((x2,y2,z2)) = (x3,y3,z3).
   */
  public Dimensions plus(Dimensions other) {
    return plus(this, other);
  }

  /** Adds two dimensions objects: (x1,y1,z1) + (x2,y2,z2) = (x3,y3,z3). */
  public static Dimensions plus(Dimensions p1, Dimensions p2) {
    // TODO: optimize for (0,0,0)
    return Dimensions.of(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z);
  }

  /**
   * Creates new dimensions by subtracting the other dimensions from these
   * dimensions: (x1,y1,z1).minus((x2,y2,z2)) = (x3,y3,z3).
   */
  public Dimensions minus(Dimensions other) {
    return minus(this, other);
  }

  /** Subtracts two dimensions objects: (x1,y1,z1) - (x2,y2,z2) = (x3,y3,z3). */
  public static Dimensions minus(Dimensions p1, Dimensions p2) {
    // TODO: optimize for (0,0,0)
    return Dimensions.of(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
  }

  /**
   * Creates new dimensions by scaling these dimensions:
   * (x,y,z).scale(a) = (a*x,a*y,a*z).
   */
  public Dimensions scale(int factor) {
    return scale(this, factor);
  }

  /** Scale dimensions objects: (x,y,z) * a = (a*x,a*y,a*z). */
  public static Dimensions scale(Dimensions p1, int factor) {
    // TODO: optimize for 0 and 1.
    return Dimensions.of(factor * p1.x, factor * p1.y, factor * p1.z);
  }

  /**
   * Creates new dimensions by scaling these dimensions:
   * (x,y,z).divide(a) = (x/a,y/a,z/a).
   */
  public Dimensions divide(int divisor) {
    return divide(this, divisor);
  }

  /** Scale dimensions objects: (x,y,z) / a = (x/a,y/a,z/a). */
  public static Dimensions divide(Dimensions p1, int divisor) {
    // TODO: optimize for 0 and 1.
    return Dimensions.of(p1.x/divisor, p1.y/divisor, p1.z/divisor);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Dimensions)) {
      return false;
    }
    Dimensions other = (Dimensions) o;
    return other.x == x && other.y == y && other.z == z;
  }

  @Override
  public int hashCode() {
    return x*123 + y*11 + z;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }


}
