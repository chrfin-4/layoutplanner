package se.ltu.kitting.model;

import java.util.Optional;

/**
 * A hint about where a part should (or must) be placed.
 * A position must be given. A rotation is optional.
 * A weight is optional and defaults to the lowest weight.
 * @author Christoffer Fink
 */
public class LayoutHint {

  public static final double defaultWeight = 1.0;
  public static final double mandatoryWeight = 10.0;

  private final Dimensions position;
  private final double weight;
  private final Optional<Rotation> rotation;

  public LayoutHint(Dimensions position) {
    this(position, defaultWeight);
  }

  public LayoutHint(Dimensions position, double weight) {
    this(position, null, weight);
  }

  public LayoutHint(Dimensions position, Rotation rotation) {
    this(position, rotation, defaultWeight);
  }

  public LayoutHint(Dimensions position, Rotation rotation, double weight) {
    assertValid(position, weight);
    this.position = position;
    this.rotation = Optional.ofNullable(rotation);
    this.weight = weight;
  }

  /** Factory for making mandatory hint (no rotation). */
  public static LayoutHint mandatory(Dimensions position) {
    return mandatory(position, null);
  }

  /** Factory for making mandatory hint (with rotation). */
  public static LayoutHint mandatory(Dimensions position, Rotation rotation) {
    return new LayoutHint(position, rotation, mandatoryWeight);
  }

  public Dimensions position() {
    return position;
  }

  public Optional<Rotation> rotation() {
    return rotation;
  }

  public double weight() {
    return weight;
  }

  public boolean isMandatory() {
    return weight == 10.0;
  }

  private static void assertValid(Dimensions d, double w) {
    if (d == null) {
      throw new IllegalArgumentException("Illegal argument: dimensions = " + d);
    }
    if (w < 1.0) {
      throw new IllegalArgumentException("Illegal argument: weight < 1.0");
    }
    if (w > 10.0) {
      throw new IllegalArgumentException("Illegal argument: weight > 10.0");
    }
  }

}
