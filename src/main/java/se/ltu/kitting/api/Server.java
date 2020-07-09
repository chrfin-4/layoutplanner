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
@SuppressWarnings("serial")
@WebServlet(name = "LayoutPlanning", urlPatterns = {"/requestLayout"}, loadOnStartup = 1)
public class Server extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    sendJson(LayoutPlanner.jsonResponse(jsonRequest(request)), response);
  }

  public static void sendJson(String json, ServletResponse response) throws IOException {
    response.getWriter().print(json);
  }

  public static String jsonRequest(ServletRequest request) throws IOException {
    return request.getReader().lines().collect(joining());
  }

}
