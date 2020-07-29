package se.ltu.kitting.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import se.ltu.kitting.LayoutPlanner;

/**
 * REST endpoint, takes requests and returns responses as JSON.
 * @author Christoffer Fink
 */
@SpringBootApplication
@RestController
public class SpringServer {

  public static void main(String[] args) {
    SpringApplication.run(SpringServer.class, args);
  }

  @PostMapping("/requestLayout")
  public String requestLayout(@RequestBody String json) {
    return LayoutPlanner.jsonResponse(json);
  }

}
