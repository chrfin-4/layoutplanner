package se.ltu.kitting.api.json;

import java.util.List;
import java.util.Optional;

import se.ltu.kitting.model.Dimensions;

public class Wagon {

  // Required in request and response.
  public String wagonId;
  public List<String> capabilities; // may be empty
  public List<Surface> surfaces;    // at least 1
  public Dimensions dimensions;

  public static class Surface {
    // All required.
    public int id;
    public Dimensions dimensions;
    public Dimensions origin;

    public int id() {
      return id;
    }

    public Surface id(int id) {
      this.id = id;
      return this;
    }

    public Dimensions dimensions() {
      return dimensions;
    }

    public Dimensions origin() {
      return origin;
    }

    public Surface dimensions(Dimensions dimensions) {
      this.dimensions = dimensions;
      return this;
    }

    public Surface origin(Dimensions origin) {
      this.origin = origin;
      return this;
    }

  }

  public List<Wagon.Surface> surfaces() {
    return surfaces;
  }

  public List<String> capabilities() {
    return capabilities == null ? List.of() : capabilities;
  }

  public Dimensions dimensions() {
    return dimensions;
  }

  public Wagon dimensions(Dimensions dimensions) {
    this.dimensions = dimensions;
    return this;
  }

}
