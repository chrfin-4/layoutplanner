package se.ltu.kitting.model;

import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;

// Same properties in request and response, but different subsets are required.
public class Wagon {

  // Required in request and response.
  private String wagonId;
  private List<String> capabilities;

  // Required in response. Optional in request? (minItems = 1)
  private List<Surface> surfaces;

  // Optional.
  // TODO: derive from surfaces? What is this used for?
  private Dimensions dimensions;



  public static Wagon of(Surface surface) {
    return new Wagon("0", List.of(surface));
  }

  public static Wagon of(List<Surface> surfaces) {
    return new Wagon("0", surfaces);
  }

  public static Wagon of(String id, List<Surface> surfaces) {
    return new Wagon(id, surfaces);
  }

  private Wagon(String id, List<Surface> surfaces) {
    this.wagonId = id;
    this.surfaces = surfaces;
  }

  public String wagonId() {
    return wagonId;
  }

  public Collection<String> capabilities() {
    return capabilities;
  }

  public List<Surface> surfaces() {
    return surfaces;
  }

  public Surface surfaceOf(Dimensions position) {
    return surfaces.stream().filter(s -> s.origin.z == position.z).findAny().get();
  }

  public List<Dimensions> allPositions() {
    return surfaces.stream()
      .map(Surface::getSurfacePositions)
      .flatMap(Collection::stream)
      .collect(toList());
  }

}
