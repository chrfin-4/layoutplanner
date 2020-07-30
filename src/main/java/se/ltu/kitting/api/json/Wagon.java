package se.ltu.kitting.api.json;

import java.util.List;
import java.util.Optional;

public class Wagon {

  // Required in request and response.
  public String wagonId;
  public List<String> capabilities; // may be empty
  public List<Surface> surfaces;    // at least 1
  public Coordinate3D dimensions;

  public static class Surface {
    // All required.
    public int id;
    public Coordinate3D dimensions;
    public Coordinate3D origin;

    public int id() {
      return id;
    }

    public Coordinate3D dimensions() {
      return dimensions;
    }

    public Coordinate3D origin() {
      return origin;
    }

  }

  public List<Wagon.Surface> surfaces() {
    return surfaces;
  }

  public List<String> capabilities() {
    return capabilities == null ? List.of() : capabilities;
  }

  public Coordinate3D dimensions() {
    return dimensions;
  }

}
