package se.ltu.kitting.model;

import java.util.function.UnaryOperator;
import ch.rfin.util.Pair;

/**
 * Represents the rotation/orientation of a part.
 * Note that it does not represent a rotation operation in the sense
 * that a part is modified each time the operation is applied.
 * Rather it specifies the rotation the part should have.
 * So rotations are meant to be idempotent and absolute rather than relative.
 * (An analogy would be a servo rather than a motor.)
 * Can potentially support any angle.
 * This class is immutable.
 * @author Christoffer Fink
 */
public final class Rotation implements UnaryOperator<Pair<Dimensions,Dimensions>> {

  /**
   * For exact comparison. Comparing doubles is always precarious.
   */
  public static final double deg90 = Math.PI/2;

  // XXX: Some predefined rotations. How should these be interpreted?
  public static final Rotation ZERO = rotation(0.0, 0.0, 0.0);
  public static final Rotation X90 = rotation(deg90, 0.0, 0.0);
  public static final Rotation Y90 = rotation(0.0, deg90, 0.0);
  public static final Rotation Z90 = rotation(0.0, 0.0, deg90);
  public static final Rotation Z90X90 = rotation(deg90, 0.0, deg90);
  public static final Rotation Z90Y90 = rotation(0.0, deg90, deg90);
  
  private final double x,y,z;

  private Rotation(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Factory method - alias for {@link #of(double, double, double)}.
   * Useful for static imports.
   */
  public static Rotation rotation(double x, double y, double z) {
    return of(x,y,z);
  }

  /** Factory method. */
  public static Rotation of(double x, double y, double z) {
    return new Rotation(x, y, z);
  }

  /** Apply the rotation to a region. */
  @Override
  public Pair<Dimensions,Dimensions> apply(Pair<Dimensions,Dimensions> cuboid) {
    throw new UnsupportedOperationException("Not implemented.");
  }

}
