package se.ltu.kitting.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import ch.rfin.util.Pair;
import java.util.*;
import se.ltu.kitting.algo.*;

import static java.util.Comparator.comparing;
import static se.ltu.kitting.model.Rotation.rotation;
import static java.util.stream.Collectors.toSet;

/**
 * A part in the kit; must be assigned a position, side, and a rotation.
 * The ID identifies a particular, individual object within the current kit.
 * The part number identifies a kind of part of which there could be multiple
 * instances in a kit.
 * The position is the (left,back,bot) point.
 * @author Christoffer Fink
 */
//@PlanningEntity(difficultyWeightFactoryClass = PartWeightFactory.class) // example of using a weight factory for comparison
@PlanningEntity(difficultyComparatorClass = VolumeComparator.class)
public class Part {

  // Problem facts.

  /** Identifies a specific object. */
  private int id = -1;
  /** Identifies a kind of part. */
  private String partNumber = "?";
  /**
   * The size of the part when in its original, non-rotated position.
   * This never changes. To get the current dimensions, including rotation,
   * use {@link #currentRegion()} or {@link #currentDimensions()}.
   */
  private Dimensions size;
  /** The part may be placed on these sides. */
  private List<Side> allowedDown;
  /** The part should preferentially be placed on this side. */
  private Side preferredDown;
  /** Minimum free margin around all sides. */
  private int margin;
  /** Hint about how the part should be placed. */
  private LayoutHint hint;

  // Planning variables.
  // Should probably remain uninitialized, or OptaPlanner can get confused and
  // fail to initialize them during construction.

  /** The current position of the (left,back,bot) corner. */
  private Dimensions position;
  /** The current rotation. */
  private Rotation rotation;
  /** The side that is currently down. */
  private Side sideDown;

  // Derived. TODO: need to use some OptaPlanner annotation? (Maybe Shadow?)

  private Pair<Dimensions,Dimensions> currentRegion;

  /** A no-arg constructor is required by OptaPlanner. */
  public Part() { }

  /**
   * Copy constructor.
   * @see #copyOf(Part)
   */
  public Part(final Part part) {
    // Facts
    this.id = part.id;
    this.partNumber = part.partNumber;
    this.size = part.size;
    this.preferredDown = part.preferredDown;
    this.allowedDown = new ArrayList<>(part.allowedDown);
    this.hint = part.hint;
    this.margin = part.margin;
    // Variables
    this.position = part.position;
    this.sideDown = part.sideDown;
    this.rotation = part.rotation;
    // Other
    this.currentRegion = part.currentRegion;
  }

  @Deprecated
  public Part(int id, int partNumber, Dimensions size) {
    this(id, "" + partNumber, size);
  }

  /** Initializes all the problem facts. */
  public Part(int id, String partNumber, Dimensions size) {
    this(id, partNumber, size, null);
  }

  public Part(int id, String partNumber, Dimensions size, Dimensions position) {
    this.id = id;
    this.partNumber = partNumber;
    this.size = size;
    this.position = position;
  }

  /**
   * Clone this part.
   * @return a new Part instance that is a deep and independent copy.
   */
  public static Part copyOf(final Part part) {
    return new Part(part);
  }

  // --- START of OptaPlanner facts and variables ---

  // Note that this class can easily be switched between either using
  // (position,rotation) pairs as the single planning variable or using
  // position and rotation as two separate planning variables.
  // Using both together as a single planning variable (more easily) allows
  // moves to be filtered without getting stuck in local optima.

  /**
   * Allows the two real planning variables to be combined and treated like a
   * single variable.
   */
  @Deprecated(forRemoval = true)
  public Pair<Dimensions,Rotation> getPositionAndRotation() {
    return Pair.of(position, rotation);
  }

  /** Always sets the position and rotation separately using the two setters. */
  public void setPositionAndRotation(Pair<Dimensions,Rotation> posRot) {
    setPosition(posRot._1);
    setRotation(posRot._2);
  }

  @PlanningVariable(valueRangeProviderRefs = {"positions"})
  public Dimensions getPosition() {
    return position;
  }

  public void setPosition(Dimensions pos) {
    this.position = pos;
  }

  @PlanningVariable(valueRangeProviderRefs = {"rotations"})
  public Rotation getRotation() {
    return rotation;
  }

  public void setRotation(Rotation rotation) {
    this.rotation = rotation;
  }

  @PlanningVariable(valueRangeProviderRefs = {"sides"})
  public Side getSideDown() {
    return sideDown;
  }

  public void setSideDown(Side sideDown) {
    this.sideDown = sideDown;
  }

  // These setters and getters are required by OptaPlanner (even though they
  // never change) so it can make clones.

  @ProblemFactProperty
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @ProblemFactProperty
  public String getPartNumber() {
    return partNumber;
  }

  public void setPartNumber(String pNr) {
    this.partNumber = pNr;
  }

  /**
   * Use {@link #currentDimensions()} or {@link #currentRegion()} to get the
   * current dimensions (and position) taking the current rotation into account.
   */
  @ProblemFactProperty
  public Dimensions getSize() {
    return size;
  }

  public void setSize(Dimensions size) {
    this.size = size;
  }

  @ProblemFactProperty
  public List<Side> getAllowedDown() {
    return allowedDown;
  }

  @Deprecated
  public void setAllowedDown(Collection<Side> sides) {
    allowedDown = new ArrayList<>(sides);
  }

  public void setAllowedDown(List<Side> sides) {
    allowedDown = sides;
  }

  @ProblemFactProperty
  public Side getPreferredDown() {
    return preferredDown;
  }

  @ProblemFactProperty
  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  @ProblemFactProperty
  public LayoutHint getHint() {
    return hint;
  }

  public void setHint(LayoutHint hint) {
    this.hint = hint;
  }

  public void setPreferredDown(Side side) {
    preferredDown = side;
  }

  @ValueRangeProvider(id = "sides")
  public List<Side> getAllowedSidesDown() {
    return allowedDown;
  }

  // --- END of OptaPlanner facts and variables ---

  /** Has a layout hint, which may or may not be mandatory. */
  public boolean hasHint() {
    return hint != null;
  }

  /** Has a layout hint that is NOT mandatory. */
  public boolean hasOptionalHint() {
    return hint != null && !hint.isMandatory();
  }

  /** Has a *mandatory* layout hint. */
  public boolean hasMandatoryHint() {
    return hint != null && hint.isMandatory();
  }

  public boolean fullyInitialized() {
    return position != null && sideDown != null && rotation != null;
  }

  /**
   * Returns a clone with the new position and rotation.
   * This will ALWAYS return a brand new instance.
   */
  public Part cloneWith(Pair<Dimensions,Rotation> posRot) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Returns a clone with the new position.
   * This will ALWAYS return a brand new instance.
   */
  public Part cloneWith(Dimensions pos) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Returns a clone with the new rotation.
   * This will ALWAYS return a brand new instance.
   */
  public Part cloneWith(Rotation rot) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Returns a part with the given position and rotation.
   * Does NOT create a new instance if the new value is the same as the old.
   */
  public Part withPositionAndRotation(Pair<Dimensions,Rotation> posRot) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Returns a part with the given position and rotation.
   * Does NOT create a new instance if the new value is the same as the old.
   */
  public Part withPosition(Dimensions pos) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Returns a part with the given position and rotation.
   * Does NOT create a new instance if the new value is the same as the old.
   */
  public Part withRotation(Rotation rot) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Return two points that together represent the area/volume currently taken
   * up by the part, taking rotation into account.
   * Note that this changes both depending on the current position and the
   * current rotation.
   * Should only be called if a position has been assigned.
   * Returns null otherwise.
   */
  public Pair<Dimensions,Dimensions> currentRegion() {
    // TODO: throw an illegal state exception when there is no position set?
    return computeCurrentRegion(sideDown, rotation);
  }

  // Uses side + rotationZ.
  private Pair<Dimensions,Dimensions> computeCurrentRegion(Side side, Rotation rot) {
    if (!fullyInitialized()) {
      return null;
    }
    Dimensions newSize = rotation(side, rot).apply(size);
    // Position uses (x,y,surface), but the region is relative to some surface.
    // So we ignore the Z coordinate of the position.
    return Pair.of(position.withZ(0), position.withZ(0).plus(newSize).minus(Dimensions.UNIT));
  }

  // TODO: add applyHint() method?

  public Dimensions currentDimensions() {
    return rotation(sideDown, rotation).apply(size);
  }

  /** Warning: Subject to rounding errors! */
  public Dimensions currentCenter() {
    var size = currentDimensions();
    return Dimensions.of(position.x + size.x/2, position.y + size.y/2, size.z/2);
  }

  // TODO: Move to separate util class.
  public static Dimensions cornerToCenter(Dimensions corner, Dimensions dimensions) {
    return corner.plus(dimensions.divide(2));
  }

  // TODO: Move to separate util class.
  public static Dimensions centerToCorner(Dimensions center, Dimensions dimensions) {
    return center.minus(dimensions.divide(2));
  }

  public int width() {
    return currentDimensions().x;
  }

  public int depth() {
    return currentDimensions().y;
  }

  public int height() {
    return currentDimensions().z;
  }

  /**
   * The total volume taken up by the part (as a cuboid).
   * Note that the volume never changes, since it is based on the size.
   */
  public int volume() {
    return width()*depth()*height();
  }

  /**
   * The surface area taken up by the base of this part (as a cuboid), based
   * on the current rotation.
   * Note that the area changes depending on rotation.
   */
  public int currentArea() {
    var dim = currentDimensions();
    return dim.x * dim.y;
  }

  /** Minimum theoretically possible area. Never changes. */
  public int minArea() {
    final int xy = size.x * size.y;
    final int xz = size.x * size.z;
    final int yz = size.y * size.z;
    return Math.min(xy, Math.min(xz, yz));
  }

  /** Maximum theoretically possible area. Never changes. */
  public int maxArea() {
    final int xy = size.x * size.y;
    final int xz = size.x * size.z;
    final int yz = size.y * size.z;
    return Math.max(xy, Math.max(xz, yz));
  }

  // XXX: should probably check the surface's xy area as well.
  /** Minimum possible area. Never changes. */
  public OptionalInt minAllowedArea(Surface surface) {
    return allowedAndPossibleSides(surface).stream()
      .mapToInt(this::areaOf)
      .min();
  }

  // XXX: should probably check the surface's xy area as well.
  /** Minimum possible area. Never changes. */
  public OptionalInt maxAllowedArea(Surface surface) {
    return allowedAndPossibleSides(surface).stream()
      .mapToInt(this::areaOf)
      .max();
  }

  /** Minimum possible area. Never changes. */
  public int minAllowedArea() {
    return allowedDown.stream().mapToInt(this::areaOf).min().getAsInt();
  }

  /** Maximum possible area. Never changes. */
  public int maxAllowedArea() {
    return allowedDown.stream().mapToInt(this::areaOf).max().getAsInt();
  }

  /** The smallest side. Never changes. */
  public int minLength() {
    return Math.min(size.x, Math.min(size.y, size.z));
  }

  /** The largest side. Never changes. */
  public int maxLength() {
    return Math.max(size.x, Math.max(size.y, size.z));
  }

  /**
   * Check whether this part intersects with the other part.
   * Depends on the current region of both parts.
   */
  public boolean intersects(Part part) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Check whether this part intersects with the other part, treating both
   * like 2D rectangles.
   */
  @Deprecated // TODO: Is this actually useful?
  // Yes, one way it could be useful is if parts are guaranteed to fit within
  // the surface (in 3D) on which they are placed, and we only want to make sure
  // that parts on the same surface don't overlap.
  public boolean intersectsIgnoringZ(Part part) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** Returns the area of this side of the part. */
  public int areaOf(Side side) {
    switch (side) {
      case bottom: case top: return size.x * size.y;
      case back: case front: return size.x * size.z;
      case left: case right: return size.y * size.z;
      default: throw new IllegalArgumentException("Unexpected side: " + side);
    }
  }

  // XXX: should probably check the surface's xy area as well.
  public Set<Side> allowedAndPossibleSides(Surface surface) {
    return allowedDown.stream()
      .filter(side -> Rotation.rotateOntoSide(side, size).z <= surface.height())
      .collect(toSet());
  }

  /**
   * Returns an allowed side for which the part has the minimum area.
   * In other words, out of all the sides that are allowed, which has the
   * smallest area?
   * Favors the preferred side if equivalent to the minimum.
   * If the preferred side is not equivalent to the minimum, the preferred
   * side is ignored.
   * All else being equal, defaults to the canonical side.
   */
  public Side minAreaSide() {
    assert !allowedDown.isEmpty();
    Side min = allowedDown.stream().min(comparing(this::areaOf)).get();
    if (min == preferredDown) {
      return min;
    }
    Side opposite = min.opposite();
    if (opposite == preferredDown) {
      return opposite;
    }
    Side canonical = min.toCanonical();
    if (allowedDown.contains(canonical)) {
      return canonical;
    }
    return min;
  }

  // TODO: refactor
  public Optional<Side> minAreaSide(Surface surface) {
    assert !allowedDown.isEmpty();
    Optional<Side> minSide = allowedAndPossibleSides(surface).stream().min(comparing(this::areaOf));
    if (!minSide.isPresent()) {
      return minSide;
    }
    Side min = minSide.get();
    if (min == preferredDown) {
      return Optional.of(min);
    }
    Side opposite = min.opposite();
    if (opposite == preferredDown) {
      return Optional.of(opposite);
    }
    Side canonical = min.toCanonical();
    if (allowedDown.contains(canonical)) {
      return Optional.of(canonical);
    }
    return minSide;
  }

  @Override
  public String toString() {
    return "Part(id: " + id + ", dimensions: " + size + ")";
  }

}
