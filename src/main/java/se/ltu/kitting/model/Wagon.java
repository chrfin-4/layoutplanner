package se.ltu.kitting.model;

import java.util.List;

// Same properties in request and response, but different subsets are required.
public class Wagon {
  // Required in request and response.
  private String wagonId;
  private List<String> capabilities;

  // Required in response. Optional in request? (minItems = 1)
  private List<Surface> surfaces;

  // Optional.
  private Dimensions dimensions;

  public List<Surface> surfaces() {
    return surfaces;
  }

}
