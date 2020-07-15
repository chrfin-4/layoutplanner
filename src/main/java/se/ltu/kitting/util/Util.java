package se.ltu.kitting.util;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.SolverConfig;
import java.util.stream.Stream;
import se.ltu.kitting.model.*;
import static se.ltu.kitting.util.Preds.*;

public class Util {

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

  public static boolean feasibleSolution(Layout layout) {
    return layout.getScore().getHardScore() == 0;
  }

  /** Check whether this is a successfully solved layout. */
  public static boolean layoutSolved(Layout layout) {
   return feasibleSolution(layout) && allVariablesInitialized(layout);
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
