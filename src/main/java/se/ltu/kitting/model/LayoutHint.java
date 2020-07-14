package se.ltu.kitting.model;

import java.util.Optional;

/**
 * A hint about where a part should (or must) be placed.
 * A position must be given. A rotation is optional.
 * A weight is optional and defaults to the lowest weight.
 * @author Christoffer Fink
 */
public class LayoutHint {

  public static final int defaultWeight = 1;
  public static final int mandatoryWeight = 10;

  private final Dimensions centerPosition;
  private final int weight;
  private final Optional<Rotation> rotation;

  public LayoutHint(Dimensions position) {
    this(position, defaultWeight);
  }

  public LayoutHint(Dimensions centerPosition, double weight) {
    this(centerPosition, Optional.empty(), weight);
  }

  public LayoutHint(Dimensions centerPosition, Rotation rotation) {
    this(centerPosition, rotation, defaultWeight);
  }

  public LayoutHint(Dimensions centerPosition, Rotation rotation, double weight) {
    this(centerPosition, Optional.ofNullable(rotation), weight);
  }

  public LayoutHint(Dimensions centerPosition, Optional<Rotation> rotation, double weight) {
    this.centerPosition = centerPosition;
    this.rotation = rotation;
    this.weight = (int)weight;
    assertValid(centerPosition, this.weight);
  }

  /** Factory for making mandatory hint (no rotation). */
  public static LayoutHint mandatory(Dimensions centerPosition) {
    return mandatory(centerPosition, null);
  }

  /** Factory for making mandatory hint (with rotation). */
  public static LayoutHint mandatory(Dimensions centerPosition, Rotation rotation) {
    return new LayoutHint(centerPosition, rotation, mandatoryWeight);
  }

  public LayoutHint withPosition(Dimensions centerPosition) {
    return new LayoutHint(centerPosition, rotation, weight);
  }

  public LayoutHint withRotation(Rotation rotation) {
    return new LayoutHint(centerPosition, rotation, weight);
  }

  public LayoutHint withWeight(double weight) {
    return new LayoutHint(centerPosition, rotation, weight);
  }

  public Dimensions centerPosition() {
    return centerPosition;
  }

  public Optional<Rotation> rotation() {
    return rotation;
  }

  public int weight() {
    return weight;
  }

  public boolean isMandatory() {
    return weight == mandatoryWeight;
  }

  private static void assertValid(Dimensions d, int w) {
    if (d == null) {
      throw new IllegalArgumentException("Illegal argument: dimensions = " + d);
    }
    if (w < 1) {
      throw new IllegalArgumentException("Illegal argument: weight < 1");
    }
    if (w > 10) {
      throw new IllegalArgumentException("Illegal argument: weight > 10");
    }
  }

}
