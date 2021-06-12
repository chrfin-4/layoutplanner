package se.ltu.kitting.model;

import java.util.Collection;
import java.util.Set;
import java.util.EnumSet;

/**
 * A side of a part. Implicitly represents rotation by specifying which of its
 * six sides the part is resting on (which side is facing downward).
 * In terms of dimensions, the pairs of opposite sides are equivalent.
 * The sides left, back, and bottom, are considered the default or
 * "representative" or the "canonical" of the pair.
 * What may distinguish the otherwise equivalent sides from each other is
 * when one of them is the preferred side or only one of them is allowed.
 * @author Christoffer Fink
 */
public enum Side {
  left, right, back, front, bottom, top;

  /** Return a set of all sides. */
  public static EnumSet<Side> all() {
    return EnumSet.allOf(Side.class);
  }

  /** Return an empty set. */
  public static EnumSet<Side> none() {
    return EnumSet.noneOf(Side.class);
  }

  /**
   * Return the representative sides without any redundantly equivalent sides.
   * Returns {left, back, bottom}.
   */
  public static EnumSet<Side> canonical() {
    return EnumSet.of(left, back, bottom);
  }

  /**
   * Returns a set that is equivalent to the given set, but with no redundant
   * (equivalent) sides. Does not modify the set given as a parameter.
   */
  public static EnumSet<Side> withoutRedundancy(Collection<Side> sides) {
    EnumSet<Side> result = EnumSet.noneOf(Side.class);
    for (var side : sides) {
      if (!result.contains(side.opposite())) {
        result.add(side);
      }
    }
    return result;
  }

  /** Removes redundancy by removing the non-canonical side if possible. */
  public static EnumSet<Side> normalize(Set<Side> sides) {
    EnumSet<Side> result = EnumSet.noneOf(Side.class);
    for (var side : sides) {
      if (sides.contains(side) && sides.contains(side.opposite())) {
        result.add(side.toCanonical());
      } else {
        result.add(side);
      }
    }
    return result;
  }

  /** Returns the opposite side. */
  public Side opposite() {
    switch (this) {
      case left: return right;
      case right: return left;
      case back: return front;
      case front: return back;
      case bottom: return top;
      case top: return bottom;
      default: throw new AssertionError("Forgot a Side.");
    }
  }

  public static EnumSet<Side> opposites(Collection<Side> sides) {
    EnumSet<Side> result = EnumSet.noneOf(Side.class);
    sides.stream().map(Side::opposite).forEach(result::add);
    return result;
  }

  /**
   * Sides that are opposite are equivalent in the sense that placing a cuboid
   * on either side both result in the same cuboid, with the same dimensions.
   */
  public boolean equivalentTo(Side other) {
    return other == this || other == opposite();
  }

  /**
   * Returns one of {left, back, bottom}, depending on which is equivalent
   * to this side.
   */
  public Side toCanonical() {
    switch (this) {
      case left: case right: return left;
      case back: case front: return back;
      case bottom: case top: return bottom;
      default: throw new AssertionError("Forgot a Side.");
    }
  }

  /** Is in {left, back, bottom}? */
  public boolean isCanonical() {
    return this == left || this == back || this == bottom;
  }

}
