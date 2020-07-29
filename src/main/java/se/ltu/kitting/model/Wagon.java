package se.ltu.kitting.model;

import java.util.Collection;
import java.util.List;
import java.util.Comparator;
import static java.util.stream.Collectors.toList;

// Same properties in request and response, but different subsets are required.
public class Wagon {
  private String wagonId;
  private List<Surface> surfaces;
  private List<String> capabilities;
  // TODO: derive from surfaces? What is this used for?
  private Dimensions dimensions;


  public static Wagon of(Surface surface) {
    return new Wagon("0", List.of(surface));
  }

  public static Wagon of(List<Surface> surfaces) {
    return new Wagon("0", List.copyOf(surfaces));
  }

  public static Wagon of(String id, List<Surface> surfaces) {
    return new Wagon(id, List.copyOf(surfaces));
  }

  private Wagon(String id, List<Surface> surfaces) {
    this(id, surfaces, List.of(), null);
  }

  private Wagon(String id, List<Surface> surfaces, List<String> capabilities, Dimensions dimensions) {
    this.wagonId = id;
    this.surfaces = surfaces;
    this.capabilities = capabilities;
    this.dimensions = derive(dimensions);
  }

  private Dimensions derive(Dimensions dimensions) {
    if (dimensions != null) {
      return dimensions;
    }
    return surfaces.stream().map(Surface::size).max(Dimensions::compareTo).get();
  }

  public int volume() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  public Wagon withCapabilities(Collection<String> capabilities) {
    return new Wagon(wagonId, surfaces, List.copyOf(capabilities), dimensions);
  }

  public Wagon withDimensions(Dimensions dimensions) {
    return new Wagon(wagonId, surfaces, capabilities, dimensions);
  }

  public String wagonId() {
    return wagonId;
  }

  public List<String> capabilities() {
    return capabilities;
  }

  public List<Surface> surfaces() {
    return surfaces;
  }

  public Dimensions dimensions() {
    return dimensions;
  }

  public Surface getSurfaceById(int id) {
    return surfaces.stream().filter(s -> s.id == id).findAny().get();
  }

  public Surface surfaceOf(Dimensions position) {
    return surfaces.stream().filter(s -> s.origin.z == position.z).findAny().get();
  }

}
