package se.ltu.kitting.test;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import se.ltu.kitting.api.*;
import se.ltu.kitting.model.*;

import static java.util.stream.Collectors.joining;

public class RestClient {

  // TODO: these should be given as CLI args and/or read from a properties file.
  private static final String jsonRequestFile = "demoRequest2.json";
  private static final String serviceUrl = "http://localhost:8080/kitting/requestLayout";

  public static void main(String[] args) throws Exception {
    String jsonRequest = getJsonString(jsonRequestFile);

    String jsonResponse = sendRequestToServer(jsonRequest);

    LayoutPlanningRequest req = LayoutPlanningRequest.fromJson(jsonRequest);
    LayoutPlanningResponse resp = LayoutPlanningResponse.fromJson(jsonResponse);
    Layout solution = getSolution(req, resp);
    Vis.draw(solution);
  }

  private static String sendRequestToServer(String jsonRequest) throws Exception {
    //return Server.jsonResponse(jsonRequest);  // Fake. Bypass the network.
    byte[] jsonBytes = jsonRequest.getBytes();
    URL url = new URL(serviceUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setDoInput(true);
    con.setDoOutput(true);
    con.setRequestProperty("Content-Type", "application/json");
    con.setFixedLengthStreamingMode(jsonBytes.length);
    OutputStream os = con.getOutputStream();
    os.write(jsonBytes);
    return new BufferedReader(new InputStreamReader(con.getInputStream()))
      .lines().collect(joining());
  }

  private static String getJsonString(String file) throws Exception {
    return Files.lines(Path.of(RestClient.class.getClassLoader().getResource(file).toURI())).collect(joining());
  }

  private static byte[] getJsonBytes(String file) throws Exception {
    return getJsonString(file).getBytes();
  }

  private static Layout getSolution(LayoutPlanningRequest req, LayoutPlanningResponse resp) {
    Layout problem = req.getLayout();
    for (var part : problem.getParts()) {
      Side side = resp.getSide(part.getId()).get();
      part.setSideDown(side);
      Rotation rot = resp.getRotation(part.getId()).get();
      part.setRotation(rot);
      Dimensions center = resp.getCenterPosition(part.getId()).get();
      part.setPosition(Part.centerToCorner(center, part.currentDimensions()));
    }
    return problem;
  }

}
