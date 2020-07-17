package se.ltu.kitting;

import org.optaplanner.core.api.solver.SolverFactory;
import se.ltu.kitting.api.PlanningRequest;
import se.ltu.kitting.api.PlanningResponse;
import se.ltu.kitting.api.json.JsonIO;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.test.Preprocess;

/**
 * Takes requests and returns responses.
 * @author Christoffer Fink
 */
public class LayoutPlanner {

  public static PlanningResponse requestLayout(PlanningRequest request) {
    try {
      Layout unsolved = request.getLayout();
      Preprocess.preprocess(unsolved);
      unsolved.setPositionStepSize(10);
      long start = System.currentTimeMillis();
      Layout solved = solve(unsolved);
      long end = System.currentTimeMillis();
      long time = end-start;
      System.out.println("time: " + time + " ms");
      return PlanningResponse.fromLayout(request, solved);
    } catch (Throwable e) {
      e.printStackTrace();
      return PlanningResponse.fromError(request, e);
    }
  }

  public static Layout solve(Layout unsolved) {
    return SolverFactory
      .<Layout>createFromXmlResource("solverConf.xml")
      .buildSolver()
      .solve(unsolved);
  }

  public static String jsonResponse(String jsonRequest) {
    System.out.println("Handling request: " + jsonRequest);
    PlanningRequest planningRequest = JsonIO.request(jsonRequest);
    String jsonResponse = JsonIO.toJson(requestLayout(planningRequest));
    System.out.println("Returning response: " + jsonResponse);
    return jsonResponse;
  }

}
