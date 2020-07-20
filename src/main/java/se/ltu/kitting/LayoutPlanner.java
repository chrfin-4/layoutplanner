package se.ltu.kitting;

import java.util.List;
import org.optaplanner.core.api.solver.SolverFactory;
import se.ltu.kitting.api.PlanningRequest;
import se.ltu.kitting.api.PlanningResponse;
import se.ltu.kitting.api.json.JsonIO;
import se.ltu.kitting.model.Layout;
//import se.ltu.kitting.test.Preprocess;
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
      //Preprocess.preprocess(unsolved);  // Throws exception when surfaces have z=1.
      List<Pair<String,Integer>> configs = List.of(
          pair("firstFit5s.xml",10),
          pair("late2s.xml",5),
          pair("late2s.xml",1)
        );
      Layout solved = runMultiResolution(unsolved, configs);
      long end = System.currentTimeMillis();
      long time = end-start;
      System.out.println(String.format("Finished after %5d ms: with score: %s", time, solved.getScore()));
      return PlanningResponse.fromLayout(request, solved);
    } catch (Throwable e) {
      e.printStackTrace();
      return PlanningResponse.fromError(request, e);
    }
  }

  public static Layout solve(Layout unsolved, String xml) {
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

  public static Layout runMultiResolution(Layout layout, Iterable<Pair<String,Integer>> configs) {
    int phase = 1;
    for (var conf : configs) {
      String xml = conf._1;
      int stepSize = conf._2;
      layout.setPositionStepSize(stepSize);
      long start = System.currentTimeMillis();
      layout = solve(layout, xml);
      long end = System.currentTimeMillis();
      long time = end - start;
      System.out.println(String.format("  %5d ms: %20s    (phase %2d, resolution %3d, config %s)", time, layout.getScore(), phase, conf._2, conf._1));
      if (layout.getScore().getHardScore() == 0 && layout.getScore().getSoftScore() == 0) {
        System.out.println("  Is optimal. Stopping.");
        break;
      }
      phase++;
    }
    return layout;
  }

}
