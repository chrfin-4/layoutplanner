package se.ltu.kitting.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import ch.rfin.util.Pair;

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
  private int partNumber = -1;
  /**
   * The size of the part when in its original, non-rotated position.
   * This never changes. To get the current dimensions, including rotation,
   * use {@link #currentRegion()}.
   */
  private Dimensions size;

  // Planning variables.
  // Should probably remain uninitialized, or OptaPlanner can get confused and
  // fail to initialize them during construction.

  /** The current position of the (left,back,bot) corner. */
  private Dimensions position;
  /** The current rotation. */
  private Rotation rotation;

  /** A no-arg constructor is required by OptaPlanner. */
  public Part() { }

  /** Initializes all the problem facts. */
  public Part(int id, int partNumber, Dimensions size) {
    this.id = id;
    this.partNumber = partNumber;
    this.size = size;
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
  @PlanningVariable(valueRangeProviderRefs = {"positionsAndRotationPairs"})
  public Pair<Dimensions,Rotation> getPositionAndRotation() {
    return Pair.of(position, rotation);
  }

  /** Always sets the position and rotation separately using the two setters. */
  public void setPositionAndRotation(Pair<Dimensions,Rotation> posRot) {
    setPosition(posRot._1);
    setRotation(posRot._2);
  }

  //@PlanningVariable(valueRangeProviderRefs = {"positions"})
  public Dimensions getPosition() {
    return position;
  }

  public void setPosition(Dimensions pos) {
    this.position = pos;
  }

  //@PlanningVariable(valueRangeProviderRefs = {"rotations"})
  public Rotation getRotation() {
    return rotation;
  }

  public void setRotation(Rotation rot) {
    this.rotation = rot;
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
  public int getPartNumber() {
    return partNumber;
  }

  public void setPartNumber(int pNr) {
    this.partNumber = pNr;
  }

  @ProblemFactProperty
  public Dimensions getSize() {
    return size;
  }

  public void setSize(Dimensions size) {
    this.size = size;
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
   */
  public Pair<Dimensions,Dimensions> currentRegion() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  public int width() {
	  return size.getX();
  }

  public int depth() {
	  return size.getY();
  }

  public int height() {
	  return size.getZ();
  }

  @Deprecated
  public int getWidth(){
	  return size.getX();
  }

  @Deprecated
  public int getDepth(){
	  return size.getY();
  }

  @Deprecated
  public int getHeight(){
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

  /** Minimum possible area. Never changes. */
  public int minArea() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** Maximum possible area. Never changes. */
  public int maxArea() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** The smallest side. Never changes. */
  public int minLength() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** The largest side. Never changes. */
  public int maxLength() {
    throw new UnsupportedOperationException("Not implemented.");
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
  public boolean intersectsIgnoringZ(Part part) {
    throw new UnsupportedOperationException("Not implemented.");
  }

}
