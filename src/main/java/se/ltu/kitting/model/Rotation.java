package se.ltu.kitting.model;

import java.util.function.UnaryOperator;
import java.util.Map;
import java.util.HashMap;
import ch.rfin.util.Pair;

import static ch.rfin.util.Pair.pair;

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
public final class Rotation {

  public static final Rotation ZERO = rotation(0, 0, 0);
  @Deprecated
  public static final Rotation X90 = rotation(90, 0, 0);
  @Deprecated
  public static final Rotation Y90 = rotation(0, 90, 0);
  public static final Rotation Z90 = rotation(0, 0, 90);
  @Deprecated
  public static final Rotation Z90X90 = rotation(90, 0, 90);
  @Deprecated
  public static final Rotation Z90Y90 = rotation(0, 90, 90);

  // For conversion between the (now deprecated) x,y,z representation and the
  // newer side,z representation.
  private static final Map<Pair<Side,Rotation>, Rotation> sideZtoRotation = new HashMap<>();
  private static final Map<Rotation, Pair<Side,Rotation>> rotationToSideZ = new HashMap<>();
  static {
    sideZtoRotation.put(pair(Side.bottom, ZERO), ZERO);
    sideZtoRotation.put(pair(Side.top, ZERO), ZERO);
    sideZtoRotation.put(pair(Side.left, ZERO), Y90);
    sideZtoRotation.put(pair(Side.right, ZERO), Y90);
    sideZtoRotation.put(pair(Side.back, ZERO), X90);
    sideZtoRotation.put(pair(Side.front, ZERO), X90);
    sideZtoRotation.put(pair(Side.bottom, Z90), Z90);
    sideZtoRotation.put(pair(Side.top, Z90), Z90);
    sideZtoRotation.put(pair(Side.back, Z90), Z90Y90);
    sideZtoRotation.put(pair(Side.front, Z90), Z90Y90);
    sideZtoRotation.put(pair(Side.left, Z90), Z90X90);
    sideZtoRotation.put(pair(Side.right, Z90), Z90X90);

    rotationToSideZ.put(ZERO, pair(Side.bottom, ZERO));
    rotationToSideZ.put(Y90, pair(Side.left, ZERO));
    rotationToSideZ.put(X90, pair(Side.back, ZERO));
    rotationToSideZ.put(Z90, pair(Side.bottom, Z90));
    rotationToSideZ.put(Z90Y90, pair(Side.back, Z90));
    rotationToSideZ.put(Z90X90, pair(Side.left, Z90));
  }

  public final int x,y,z;

  private Rotation(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /*
  public static Rotation rotation(int z) {
    if (z == 0) {
      return ZERO;
    } else if (z == 90) {
      return Z90;
    } else {
      throw new IllegalArgumentException("Illegal argument: z not 0 or 90: " + z);
    }
  }
  */

  //** Factory method. */
  /*
  public static Rotation of(int z) {
    return rotation(z);
  }
  */

  @Deprecated
  public static Rotation rotation(int x, int y, int z) {
    return of(x,y,z);
  }

  @Deprecated
  public static Rotation of(int x, int y, int z) {
    if (x != 0 && x != 90) {
      return ZERO;
    }
    if (y != 0 && y != 90) {
      return ZERO;
    }
    if (z != 0 && z != 90) {
      return ZERO;
    }
    return new Rotation(x, y, z);
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }

  // Note: assumes only a predefined set of instances exist.
  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public int hashCode() {
    return z * 123 + y * 17 + x;
  }

  public static Pair<Side,Rotation> toSideAndZ(Rotation rotation) {
    return rotationToSideZ.get(rotation);
  }

  public static Rotation fromSideAndZ(Side side, Rotation z) {
    return fromSideAndZ(pair(side, z));
  }

  public static Rotation fromSideAndZ(Pair<Side,Rotation> sideZ) {
    return sideZtoRotation.get(sideZ);
  }

  /**
   * Returns an operator that can perform a rotation based on which side should
   * be down and rotation around the Z axis.
   */
  public static UnaryOperator<Dimensions> rotation(Side side, Rotation rotZ) {
    return d -> rotateZeroOr90Z(rotZ, rotateOntoSide(side, d));
  }

  /** null counts as ZERO. */
  public static Dimensions rotateZeroOr90Z(Rotation rotZ, Dimensions dim) {
    if (rotZ == null || rotZ == ZERO) {
      return dim;
    } else if (rotZ == Z90) {
      return Dimensions.of(dim.y, dim.x, dim.z);
    } else {
      throw new IllegalArgumentException("Only 0 or Z90 allowed. Illegal rotation: " + rotZ);
    }
  }

  /** null counts as bottom. */
  public static Dimensions rotateOntoSide(Side side, Dimensions dim) {
    if (side == null) {
      return dim;
    }
    switch (side) {
      case bottom: case top:
        return dim;  // NOP.
      case left: case right:
        return Dimensions.of(dim.z, dim.y, dim.x);
      case back: case front:
        return Dimensions.of(dim.x, dim.z, dim.y);
      default:
        throw new AssertionError("Impossible: unknown side: " + side);
    }
  }

}
