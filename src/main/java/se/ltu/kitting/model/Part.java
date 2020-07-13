package se.ltu.kitting.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import ch.rfin.util.Pair;
import java.util.Set;
import java.util.List;
import java.util.Collection;

import static java.util.Comparator.comparing;
import static se.ltu.kitting.model.Rotation.rotation;

/**
 * A part in the kit, must be assigned a position and a rotation.
 * The id identifies a particular, individual object within the current kit.
 * The part number identifies a kind of part of which there could be multiple
 * instances in a kit.
 * The position is the (left,back,bot) point.
 * @author Christoffer Fink
 */
// TODO: add comparison
@PlanningEntity
public class Part {

  // Problem facts.

  /** Identifies a specific object. */
  private int id = -1;
  /** Identifies a kind of part. */
  private String partNumber = "?";
  /**
   * The size of the part when in its original, non-rotated position.
   * This never changes. To get the current dimensions, including rotation,
   * use {@link #currentRegion()}.
   */
  private Dimensions size;
  /** The part may be placed on these sides. */
  private Set<Side> allowedDown = Set.of(Side.bottom, Side.left, Side.back);
  /** The part should preferentially be placed on this side. */
  private Side preferredDown;
  /** Minimum free margin around all sides. */
  private int minMargin;

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
    currentRegion = computeCurrentRegion(sideDown, rotation);
  }

  @PlanningVariable(valueRangeProviderRefs = {"rotations"})
  public Rotation getRotation() {
    return rotation;
  }

  public void setRotation(Rotation rotation) {
    // XXX: need to consider what to do when rotation == null.
    if (rotation != null && rotation != this.rotation) {
      currentRegion = computeCurrentRegion(sideDown, rotation);
    }
    this.rotation = rotation;
  }

  @PlanningVariable(valueRangeProviderRefs = {"sides"})
  public Side getSideDown() {
    return sideDown;
  }

  public void setSideDown(Side sideDown) {
    // XXX: need to consider what to do when sideDown == null.
    if (sideDown != null && sideDown != this.sideDown) {
      currentRegion = computeCurrentRegion(sideDown, rotation);
    }
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
  public Set<Side> getAllowedDown() {
    return allowedDown;
  }

  public void setAllowedDown(Set<Side> sides) {
    allowedDown = sides;
  }

  @ProblemFactProperty
  public Side getPreferredDown() {
    return preferredDown;
  }

  public void setPreferredDown(Side side) {
    // XXX: is this really a good idea?
    // It seems to make sense to use the preferred side by default. You gotta
    // start somewhere, might as well take advantage of the hint.
    // But what if a different side turns out to overall be much better?
    // A construction heuristic would refuse to try.
    if (sideDown == null) {
      sideDown = preferredDown;
    }
    preferredDown = side;
  }

  @ValueRangeProvider(id = "sides")
  public Collection<Side> getAllowedSidesDown() {
    //return Side.normalize(allowedDown); // Reduce number of variables.
    return List.of(Side.bottom, Side.top, Side.back, Side.left);  // Optimal for demo.
  }

  // --- END of OptaPlanner facts and variables ---

  public static Part of(Dimensions size) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  public static Part of(Dimensions size, Dimensions position) {
    throw new UnsupportedOperationException("Not implemented.");
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
    if (currentRegion == null) {
      currentRegion = computeCurrentRegion(sideDown, rotation);
    }
    return currentRegion;
  }

  // Uses side + rotationZ.
  private Pair<Dimensions,Dimensions> computeCurrentRegion(Side side, Rotation rot) {
    if (position == null) {
      return currentRegion; // Undefined when there is no position.
    }
    // default to bottom side.
    if (side == null) {
      side = Side.bottom; // XXX: hmm, maybe rather default to preferred side? If anything, that is.
    }
    // default to zero rotation.
    if (rot == null) {
      rot = Rotation.ZERO;  // XXX: default to layout hint, if available?
    }
    return Pair.of(position, position.plus(rotation(side, rot).apply(size)).minus(Dimensions.UNIT));
  }

  public Dimensions currentDimensions() {
    return rotation(sideDown, rotation).apply(size);
  }

  /** Warning: Subject to rounding errors! */
  public Dimensions currentCenter() {
    return cornerToCenter(position, currentDimensions());
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
  
  public int originalWidth() {
    return size.getX();
  }

  public int originalDepth() {
    return size.getY();
  }

  public int originalHeight() {
    return size.getZ();
  }

  /**
   * The total volume taken up by the part (as a cuboid).
   * Note that the volume never changes, since it is based on the size.
   */
  public int volume() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * The surface area taken up by the base of this part (as a cuboid), based
   * on the current rotation.
   * Note that the area changes depending on rotation.
   */
  public int currentArea() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  // XXX: minArea and maxArea do not take allowed sides into consideration!
  // Change this behavior or add both versions?

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

  /** Minimum actually possible area. Never changes. */
  public int minAllowedAreaWithZ(int z) {
    return allowedDown.stream()
      .filter(side -> {
        Dimensions newSize = Rotation.rotateOntoSide(side, size);
        return newSize.z <= z;
      })
      .mapToInt(this::areaOf).min().getAsInt();
  }

  /** Minimum actually possible area. Never changes. */
  public int maxAllowedAreaWithZ(int z) {
    return allowedDown.stream()
      .filter(side -> {
        Dimensions newSize = Rotation.rotateOntoSide(side, size);
        return newSize.z <= z;
      })
      .mapToInt(this::areaOf).max().getAsInt();
  }

  /** Minimum actually possible area. Never changes. */
  public int minAllowedArea() {
    return allowedDown.stream().mapToInt(this::areaOf).min().getAsInt();
  }

  /** Maximum actually possible area. Never changes. */
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
  
  public Pair<Integer,Integer> dimensionsOf(Side side){
    Dimensions dim = Rotation.rotateOntoSide(side, size);
	return Pair.of(dim.getX(),dim.getY());
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

}
