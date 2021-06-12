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
  private final int surfaceId;
  private final int weight;
  private final Optional<Rotation> rotation;
  private final Optional<Side> side;

  public LayoutHint(Dimensions position, int surfaceId) {
    this(position, surfaceId, Optional.empty(), Optional.empty(), defaultWeight);
  }

  @Deprecated(forRemoval = true)
  public LayoutHint(Dimensions centerPosition, double weight) {
    this(centerPosition, -1, Optional.empty(), Optional.empty(), weight);
  }

  @Deprecated(forRemoval = true)
  public LayoutHint(Dimensions centerPosition, Rotation rotation) {
    this(centerPosition, -1, Optional.of(rotation), Optional.empty(), defaultWeight);
  }

  @Deprecated(forRemoval = true)
  public LayoutHint(Dimensions centerPosition, Rotation rotation, double weight) {
    this(centerPosition, -1, Optional.ofNullable(rotation), Optional.empty(), weight);
  }

  @Deprecated(forRemoval = true)
  public LayoutHint(Dimensions centerPosition, Optional<Rotation> rotation, double weight) {
    this(centerPosition, -1, rotation, Optional.empty(), weight);
  }

  private LayoutHint(Dimensions centerPosition, int surfaceId, Optional<Rotation> rotation, Optional<Side> side, double weight) {
    this.centerPosition = centerPosition;
    this.surfaceId = surfaceId;
    this.rotation = rotation;
    this.weight = (int)weight;
    this.side = side;
    assertValid(centerPosition, this.weight);
  }

  /** Factory. */
  public static LayoutHint hint(Dimensions centerPosition, int surfaceId) {
    return new LayoutHint(centerPosition, surfaceId);
  }

  /** Factory for making mandatory hint (no rotation). */
  public static LayoutHint mandatory(Dimensions centerPosition, int surfaceId) {
    return hint(centerPosition, surfaceId).withWeight(mandatoryWeight);
  }

  /** Factory for making mandatory hint (with rotation). */
  public static LayoutHint mandatory(Dimensions centerPosition, int surfaceId, Rotation rotation) {
    return mandatory(centerPosition, surfaceId).withRotation(rotation);
  }

  public LayoutHint withPosition(Dimensions centerPosition) {
    return new LayoutHint(centerPosition, surfaceId, rotation, side, weight);
  }

  public LayoutHint withRotation(Rotation rotation) {
    return new LayoutHint(centerPosition, surfaceId, Optional.ofNullable(rotation), side, weight);
  }

  public LayoutHint withWeight(double weight) {
    return new LayoutHint(centerPosition, surfaceId, rotation, side, weight);
  }

  public LayoutHint withSide(Side side) {
    return new LayoutHint(centerPosition, surfaceId, rotation, Optional.ofNullable(side), weight);
  }

  public Dimensions centerPosition() {
    return centerPosition;
  }

  public int surfaceId() {
    return surfaceId;
  }

  public Optional<Side> side() {
    return side;
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
