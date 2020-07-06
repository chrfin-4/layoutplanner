package se.ltu.kitting.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import se.ltu.kitting.LayoutPlanner;

import static java.util.stream.Collectors.joining;

/**
 * REST endpoint, takes requests and returns responses as JSON.
 * This is a quick and dirty firt version.
 * @author Christoffer Fink
 */
@WebServlet(name = "LayoutPlanning", urlPatterns = {"/requestLayout"}, loadOnStartup = 1)
public class Server extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LayoutPlanningRequest planningRequest = getPlanningRequest(request);
    LayoutPlanningResponse planningResponse = LayoutPlanner.requestLayout(planningRequest);
    sendPlanningResponse(planningResponse, response);
  }

  public static LayoutPlanningRequest getPlanningRequest(ServletRequest request) throws IOException {
    return LayoutPlanningRequest.fromJson(getJson(request));
  }

  public static void sendPlanningResponse(LayoutPlanningResponse planningResponse, ServletResponse servletResponse) throws IOException {
    servletResponse.getWriter().print(planningResponse.toJson());
  }

  public static String getJson(ServletRequest request) throws IOException {
    return request.getReader().lines().collect(joining());
  }

}
