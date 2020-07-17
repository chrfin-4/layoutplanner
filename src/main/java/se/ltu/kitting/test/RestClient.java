package se.ltu.kitting.test;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import se.ltu.kitting.api.*;
import se.ltu.kitting.api.json.JsonIO;
import se.ltu.kitting.model.*;
import se.ltu.kitting.LayoutPlanner;

import static java.util.stream.Collectors.joining;

public class RestClient {

  // TODO: these should be given as CLI args and/or read from a properties file.
  private static final String jsonRequestFile = "demoRequest2.json";
  private static final String serviceUrl = "http://localhost:8080/kitting/requestLayout";

  public static void main(String[] args) throws Exception {
    String jsonRequest = getJsonString(jsonRequestFile);
    System.out.println("Sending request:");
    System.out.println(jsonRequest);

    String jsonResponse = sendRequestToServer(jsonRequest);
    System.out.println("Got response:");
    System.out.println(jsonResponse);

    /* must implement getSolution to work with latest changes
    PlanningRequest req = JsonIO.request(jsonRequest);
    PlanningResponse resp = JsonIO.response(jsonResponse);
    Layout solution = getSolution(req, resp);
    Vis.draw(solution);
    */
  }

  private static String sendRequestToServer(String jsonRequest) throws Exception {
    return LayoutPlanner.jsonResponse(jsonRequest);  // Fake. Bypass the network.
    /*
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
      */
  }

  private static String getJsonString(String file) throws Exception {
    return Files.lines(Path.of(RestClient.class.getClassLoader().getResource(file).toURI())).collect(joining());
  }

  private static byte[] getJsonBytes(String file) throws Exception {
    return getJsonString(file).getBytes();
  }

  private static Layout getSolution(PlanningRequest req, PlanningResponse resp) {
    throw new UnsupportedOperationException("Not implemented.");
  }

}
