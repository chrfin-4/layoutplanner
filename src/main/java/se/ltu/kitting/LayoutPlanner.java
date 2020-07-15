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
    try {
      // TODO: add preprocessing
      Layout solved = solve(request.getLayout());
      return LayoutPlanningResponse.fromLayout(request, solved);
    } catch (Exception e) {
      return LayoutPlanningResponse.fromError(request, e);
    }
  }

  public static Layout solve(Layout unsolved) {
    return SolverFactory
      .<Layout>createFromXmlResource("solverConf.xml")
      .buildSolver()
      .solve(unsolved);
  }

  public static String jsonResponse(String jsonRequest) {
    LayoutPlanningRequest planningRequest = LayoutPlanningRequest.fromJson(jsonRequest);
    return requestLayout(planningRequest).toJson();
  }

}
