package se.ltu.kitting.model;

import java.util.Collection;
import java.util.List;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import ch.rfin.util.Pair;

import static java.util.stream.Collectors.toList;

// TODO:
//  - Add check that ensures parts have unique IDs.

/**
 * The layout of a kit.
 * @author Christoffer Fink
 */
@PlanningSolution
public class Layout {

  // TODO: should possibly take a wagon (containing several surfaces) instead.
  /** The surface on which the parts will be placed. */
  private Surface surface;
  /** The parts that will be placed on the surface(s) - planning entities. */
  private List<Part> parts;
  private HardSoftScore score;

  /** A no-arg constructor is required by OptaPlanner. */
  public Layout() { }

  public Layout(Surface surface, List<Part> parts) {
    this.surface = surface;
    this.parts = parts;
  }

  // --- START of OptaPlanner things ---

  @PlanningEntityCollectionProperty
  public List<Part> getParts() {
    return parts;
  }

  public void setParts(List<Part> parts) {
    this.parts = parts;
  }

  // TODO: Use ValueRange instead of collection.
  // Does not necessarily have to be defined on this class.
  //@ValueRangeProvider(id = "positionsAndRotationPairs")
  public Collection<Pair<Dimensions,Rotation>> getPositionsAndRotations() {
    return getPositions().stream()
      .flatMap(p -> getRotations().stream().map(r -> Pair.of(p, r)))
      .collect(toList());
  }

  @ValueRangeProvider(id = "positions")
  public Collection<Dimensions> getPositions() {
    return surface.getSurfacePositions();
  }

  // Does not necessarily have to be defined on this class.
  @ValueRangeProvider(id = "rotations")
  public Collection<Rotation> getRotations() {
    return List.of(Rotation.ZERO, Rotation.Z90);  // Rely on side + Z.
  }

  @PlanningScore
  public HardSoftScore getScore() {
    return score;
  }

  public void setScore(HardSoftScore score) {
    this.score = score;
  }

  @ProblemFactProperty
  public Surface getSurface() {
    return surface;
  }

  public void setSurface(Surface surface) {
    this.surface = surface;
  }

  // --- END of OptaPlanner things ---

  /** Total volume required to fit all parts. */
  public int totalVolume() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** Min area achievable and therefore required to fit all parts. */
  public int minArea() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** Min width required to fit all parts - max width of all parts. */
  public int minWidth() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** Min hight required to fit all parts - max hight of all parts. */
  public int minHeight() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /** Min depth required to fit all parts - max depth of all parts. */
  public int minDepth() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  /**
   * Min dimensions required to fit all parts - max dimensions of all parts.
   * Convenience method that bundles the results of
   * {@link #minWidth()}, {@link #minDepth()}, and {@link #minHeight()}.
   * Note that these are the minimum dimensions required along each axis, not
   * the minimum overall dimensions for any particular part.
   */
  public Dimensions minDimensions() {
    throw new UnsupportedOperationException("Not implemented.");
  }

}
