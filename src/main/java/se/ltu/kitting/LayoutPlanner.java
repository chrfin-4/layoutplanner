package se.ltu.kitting;

import java.math.BigDecimal;
import se.ltu.kitting.test.SearchSpace;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.optaplanner.core.api.solver.SolverFactory;
import se.ltu.kitting.api.PlanningRequest;
import se.ltu.kitting.api.PlanningResponse;
import se.ltu.kitting.api.Message;
import se.ltu.kitting.api.json.JsonIO;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.Preprocess;
import ch.rfin.util.Pair;

import static ch.rfin.util.Pair.pair;

/**
 * Takes requests and returns responses.
 * @author Christoffer Fink
 */
public class LayoutPlanner {

  public static PlanningResponse requestLayout(PlanningRequest request) {
    try {
      long start = System.currentTimeMillis();
      Layout unsolved = request.getLayout();
      // Add messages and edit layout
      request = Preprocess.preprocess(request, unsolved);
      if(request.messages().hasErrors()){
        return PlanningResponse.response(request);
      }
      final Layout solved = solve(unsolved, "unified.xml");
      long end = System.currentTimeMillis();
      long time = end-start;
      System.out.println(String.format("Finished after %5d ms: with score: %s", time, solved.getScore()));
      return PlanningResponse.response(request, solved);
    } catch (Throwable e) {
      e.printStackTrace();
      return PlanningResponse.response(request, e);
    }
  }

  public static Layout solve(Layout unsolved, String xml) {
    final var searchSpace = new BigDecimal(SearchSpace.compute(unsolved));
    System.out.println(String.format("Solving layout with search space: %e", searchSpace));
    return SolverFactory
      .<Layout>createFromXmlResource(xml)
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
