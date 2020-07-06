package se.ltu.kitting;

import org.optaplanner.core.api.solver.SolverFactory;
import se.ltu.kitting.api.LayoutPlanningRequest;
import se.ltu.kitting.api.LayoutPlanningResponse;
import se.ltu.kitting.model.Layout;

/**
 * Takes requests and returns responses.
 * @author Christoffer Fink
 */
public class LayoutPlanner {

  public static LayoutPlanningResponse requestLayout(LayoutPlanningRequest request) {
    Layout solved = solve(request.getLayout());
    LayoutPlanningResponse response = LayoutPlanningResponse.fromLayout(solved);
    return response;
  }

  public static Layout solve(Layout unsolved) {
    Layout solved = SolverFactory
      .<Layout>createFromXmlResource("solverConf.xml")
      .buildSolver()
      .solve(unsolved);
    return solved;
  }

}
