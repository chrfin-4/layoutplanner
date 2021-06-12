package se.ltu.kitting.api.json;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Surface)) {
        return false;
      }
      Surface other = (Surface) o;
      return this.id == other.id
        && Objects.equals(this.dimensions, other.dimensions)
        && Objects.equals(this.origin, other.origin);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, dimensions, origin);
    }

  }

  public String wagonId() {
    return wagonId;
  }

  public Wagon wagonId(final String wagonId) {
    this.wagonId = wagonId;
    return this;
  }

  public List<Wagon.Surface> surfaces() {
    return surfaces;
  }

  public Wagon surfaces(final List<Wagon.Surface> surfaces) {
    this.surfaces = surfaces;
    return this;
  }

  public List<String> capabilities() {
    return capabilities == null ? List.of() : capabilities;
  }

  public Wagon capabilities(final List<String> capabilities) {
    this.capabilities = capabilities;
    return this;
  }

  public Dimensions dimensions() {
    return dimensions;
  }

  public Wagon dimensions(Dimensions dimensions) {
    this.dimensions = dimensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Wagon)) {
      return false;
    }
    final Wagon other = (Wagon) o;
    return Objects.equals(wagonId, other.wagonId)
      && Objects.equals(capabilities, other.capabilities)
      && Objects.equals(surfaces, other.surfaces)
      && Objects.equals(dimensions, other.dimensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wagonId, capabilities, surfaces, dimensions);
  }

}
