package se.ltu.kitting.util;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.config.solver.SolverConfig;
import java.util.*;
import java.util.stream.Stream;
import se.ltu.kitting.model.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static se.ltu.kitting.util.Preds.*;

/**
 * Various utility methods.
 * @author Christoffer Fink
 */
public class Util {

  /** Construct a layout hint using the current placement. */
  public static LayoutHint hintFromCurrentPlacement(final Part part, final double weight) {
    if (countUninitializedVariables(part) > 0) {
      throw new IllegalArgumentException("Part must be fully initialized");
    }
    final var center = part.currentCenter();
    final var surface = part.getPosition().z;
    return LayoutHint.hint(center, surface)
      .withRotation(part.getRotation())
      .withSide(part.getSideDown())
      .withWeight(weight);
  }

  /** Returns the number of uninitialized planning variables for this part. */
  public static int countUninitializedVariables(Part part) {
    return (int) Stream.of(part.getSideDown(), part.getPosition(), part.getRotation())
      .filter(isNull())
      .count();
  }

  /** Returns the number of uninitialized planning variables for all parts. */
  public static int countUninitializedVariables(Layout layout) {
    return layout.getParts().stream().mapToInt(Util::countUninitializedVariables).sum();
  }

  /** Returns the number of parts that have ANY uninitialized variable. */
  public static int countUninitializedParts(Layout layout) {
    return (int) layout.getParts().stream()
      .mapToInt(Util::countUninitializedVariables)
      .filter(positive())
      .count();
  }

  public static boolean allVariablesInitialized(Layout layout) {
    return countUninitializedParts(layout) == 0;
  }

  @Deprecated(forRemoval = true)
  public static boolean feasibleSolution(Layout layout) {
    return layout.getScore().isFeasible();
  }

  @Deprecated(forRemoval = true)
  public static boolean optimalSolution(Layout layout) {
    return optimalScore(layout.getScore());
  }

  public static boolean optimalScore(final HardSoftLongScore score) {
    return score.isFeasible() // initialized and hard == 0
      && score.getSoftScore() == 0L;
  }

  public static boolean optimalScore(final HardMediumSoftLongScore score) {
    return score.isFeasible() // initialized and hard == 0
      && score.getMediumScore() == 0L
      && score.getSoftScore() == 0L;
  }

  /** Check whether this is a successfully solved layout. */
  @Deprecated(forRemoval = true)
  public static boolean layoutSolved(Layout layout) {
   return feasibleSolution(layout);
  }

  /**
   * Returns a set of all surface dimensions.
   * In practice, this will likely be a singleton set.
   */
  public static Set<Dimensions> surfaceDimensions(final Layout layout) {
    return layout
      .getWagon()
      .surfaces()
      .stream()
        .map(Surface::size)
        .collect(toSet());
  }

  /**
   * For which of the allowed sides is the part compatible with ALL of the
   * surface dimensions, using ALL rotations?
   * This notion of possibility provides stronger guarantees and makes filtering
   * less risky.
   * Note that this determination takes no other parts into account. So a side
   * is considered to be possible if the part could fit alone on all of the
   * available surfaces.
   */
  public static List<Side> possibleSidesStrong(final Set<Dimensions> surfaces, final Part part) {
    final var partSize = part.getSize();
    return part.getAllowedDown().stream()
      .filter(side -> {
          final var onSide = dimensionsOnSide(partSize, side);
          return surfaces.stream()
            .allMatch(surface -> couldFitStrong(surface, onSide));
      })
      .collect(toList());
  }

  /**
   * For which of the allowed sides is the part compatible with SOME of the
   * surface dimensions, using SOME rotation?
   * This notion of possibility provides weaker guarantees and makes filtering
   * more risky.
   * Note that this determination takes no other parts into account. So a side
   * is considered to be possible if the part could fit alone on any of the
   * available surfaces.
   */
  public static List<Side> possibleSidesWeak(final Set<Dimensions> surfaces, final Part part) {
    final var partSize = part.getSize();
    return part.getAllowedDown().stream()
      .filter(side -> {
          final var onSide = dimensionsOnSide(partSize, side);
          return surfaces.stream()
            .anyMatch(surface -> couldFitWeak(surface, onSide));
      })
      .collect(toList());
  }

  /**
   * Could the given box fit within the bounds with EITHER 0 OR 90 degree
   * rotation?
   */
  public static boolean couldFitWeak(final Dimensions bound, final Dimensions box) {
    return box.z <= bound.z
      && ((box.x <= bound.x && box.y <= bound.y)  // 0 rotation
      || (box.x <= bound.y && box.y <= bound.x)); // 90 rotation
  }

  /**
   * Could the given box fit within the bounds with BOTH 0 AND 90 degree
   * rotation?
   */
  public static boolean couldFitStrong(final Dimensions bound, final Dimensions box) {
    return box.z <= bound.z
      && (box.x <= bound.x && box.y <= bound.y)  // 0 rotation
      && (box.x <= bound.y && box.y <= bound.x); // 90 rotation
  }

  /** What are the new dimensions after fliping onto the given side? */
  public static Dimensions dimensionsOnSide(final Dimensions original, final Side side) {
    return Rotation.rotateOntoSide(side, original);
  }


  /** Convenience method for solving a problem. */
  public static Layout solve(Layout problem, String xmlResource) {
    return solver(xmlResource).solve(problem);
  }

  public static Solver<Layout> solver(String xmlResource) {
    return solver(SolverConfig.createFromXmlResource(xmlResource));
  }

  public static Solver<Layout> solver(SolverConfig cfg) {
    return SolverFactory.<Layout>create(cfg).buildSolver();
  }

}
