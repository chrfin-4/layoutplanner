package se.ltu.kitting.api.json;

import java.util.List;
import java.util.Optional;

public class Wagon {

  // Required in request and response.
  public String wagonId;
  public List<String> capabilities; // may be empty
  public List<Surface> surfaces;    // at least 1
  // Optional.
  public Coordinate3D dimensions;

  public static class Surface {
    public Coordinate3D origin;     // Optional! :o
    public Coordinate3D dimensions; // Optional! :o

    public Optional<Coordinate3D> origin() {
      return Optional.ofNullable(origin);
    }

    public Optional<Coordinate3D> dimensions() {
      return Optional.ofNullable(dimensions);
    }

  }

  public List<Wagon.Surface> surfaces() {
    return surfaces;
  }

  public List<String> capabilities() {
    return capabilities == null ? List.of() : capabilities;
  }

  public Optional<Coordinate3D> dimensions() {
    return Optional.ofNullable(dimensions);
  }

}
