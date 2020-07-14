package se.ltu.kitting.test;

import java.util.*;
import se.ltu.kitting.model.*;

// TODO: rename to something like KitBuilder?
/**
 * Tool for building layouts (and the parts that should be added to them).
 * <strong>Note that this a very early, simple, incomplete version.</strong>
 * When adding multiple surfaces by only giving their dimensions, assigns
 * them origins so they are stacked starting with the first at the bottom.
 * @author Christoffer Fink
 */
public class LayoutBuilder {

  private final PartBuilder pb;
  private final List<Part> parts = new ArrayList<>();
  private final List<Surface> surfaces = new ArrayList<>();
  private String wagonId = "wagon";
  private final Set<Integer> ids = new HashSet<>();

  private int counter = 1;

  private LayoutBuilder() {
    pb = new PartBuilder(this);
  }

  public Layout build() {
    Wagon wagon = Wagon.of(wagonId, List.copyOf(surfaces));
    return new Layout(wagon, List.copyOf(parts));
  }

  public static LayoutBuilder builder() {
    return new LayoutBuilder();
  }

  public LayoutBuilder surfaces(Dimensions ... sizes) {
    Arrays.stream(sizes).forEach(this::surface);
    return this;
  }

  public LayoutBuilder surfaces(Surface ... surfaces) {
    Arrays.stream(surfaces).forEach(this::surface);
    return this;
  }

  public LayoutBuilder surface(int width, int depth, int height) {
    return surface(Dimensions.of(width, depth, height));
  }

  public LayoutBuilder surface(Dimensions size) {
    return surface(Surface.of(size, nextSurfaceOrigin()));
  }

  public LayoutBuilder surface(Surface surface) {
    if (!abovePreviousSurface(surface)) {
      throw new IllegalArgumentException("Surface collides with the one below");
    }
    surfaces.add(surface);
    return this;
  }

  public PartBuilder part() {
    pb.allowedSides.clear();
    return pb.id(counter);
  }

  public LayoutBuilder parts(Part ... parts) {
    Arrays.stream(parts).forEach(this::part);
    return this;
  }

  public LayoutBuilder parts(Dimensions ... sizes) {
    Arrays.stream(sizes).forEach(this::part);
    return this;
  }

  public LayoutBuilder part(int width, int depth, int height) {
    return part(Dimensions.of(width, depth, height));
  }

  public LayoutBuilder part(Dimensions size) {
    //return part(new Part(-1, "?", size));
    //return part().id(counter++).dimensions(size).add();
    return part().id(counter).dimensions(size).add();
  }

  // All parts are ultimately added by this method.
  public LayoutBuilder part(Part part) {
    int id = part.getId();
    if (ids.contains(id)) {
      throw new IllegalArgumentException("Illegal argument: " + id);
    }
    ids.add(id);
    counter = ids.stream().max(Integer::compare).get() + 1;
    parts.add(part);
    pb.id(counter);
    return this;
  }

  // Does the previously added surface (if any) end below this new surface?
  // FIXME: needs to check for a 0 height that means it's the top surface, with
  // unlimited height.
  private boolean abovePreviousSurface(Surface surface) {
    int nextZ = surface.origin.z;
    int prevZ = prevSurface().map(s -> s.origin.z + s.dimensions.z).orElse(0);
    return prevZ <= nextZ;
  }

  private Dimensions nextSurfaceOrigin() {
    return prevSurface().map(s -> s.origin.plus(s.dimensions)).orElse(Dimensions.ZERO);
  }

  private Optional<Surface> prevSurface() {
    int size = surfaces.size();
    if (size == 0) {
      return Optional.empty();
    }
    return Optional.of(surfaces.get(size-1));
  }

  /**
   * Build parts.
   * Some defaults:
   * - By default allows all sides.
   * - Preferes bottom if no preferred side set.
   */
  public static class PartBuilder {
    private final LayoutBuilder lb;
    private int id;
    private String pnr;
    private Dimensions dimensions;
    private Side preferredSide = Side.bottom;
    private Rotation rotation;
    private EnumSet<Side> allowedSides = EnumSet.noneOf(Side.class);
    private int margin = 0;

    private PartBuilder(LayoutBuilder lb) {
      this.lb = lb;
    }

    public LayoutBuilder add() {
      Part part = new Part(id, pnr, dimensions);
      part.setAllowedDown(EnumSet.copyOf(allowedSides));
      part.setPreferredDown(preferredSide);
      part.setMargin(margin);
      return lb.part(part);
    }

    public LayoutBuilder add(int count) {
      while (count-- > 0) {
        add();
      }
      return lb;
    }

    public PartBuilder id(int id) {
      this.id = id;
      return this;
    }

    public PartBuilder partNumber(String pnr) {
      this.pnr = pnr;
      return this;
    }

    public PartBuilder dimensions(int x, int y, int z) {
      return dimensions(Dimensions.of(x, y, z));
    }

    public PartBuilder dimensions(Dimensions size) {
      this.dimensions = size;
      return this;
    }

    public PartBuilder preferredSide(Side side) {
      this.preferredSide = side;
      return allowSide(side);
    }

    public PartBuilder disallowSides(Side ... sides) {
      Arrays.stream(sides).forEach(this::disallowSide);
      return this;
    }

    public PartBuilder disallowSides(Iterable<Side> sides) {
      sides.iterator().forEachRemaining(this::disallowSide);
      return this;
    }

    public PartBuilder disallowSide(Side side) {
      if (allowedSides.equals(EnumSet.noneOf(Side.class))) {
        allowedSides = Side.all();
      }
      allowedSides.remove(side);
      return this;
    }

    public PartBuilder allowSides(Side ... sides) {
      Arrays.stream(sides).forEach(this::allowSide);
      return this;
    }

    public PartBuilder allowSides(Iterable<Side> sides) {
      sides.iterator().forEachRemaining(this::allowSide);
      return this;
    }

    public PartBuilder allowSide(Side side) {
      if (allowedSides.equals(Side.all())) {
        allowedSides = EnumSet.noneOf(Side.class);
      }
      allowedSides.add(side);
      return this;
    }

    public PartBuilder margin(int margin) {
      this.margin = margin;
      return this;
    }

    public PartBuilder rotation(int z) {
      return rotation(Rotation.of(z));
    }

    public PartBuilder rotation(Rotation rotation) {
      this.rotation = rotation;
      return this;
    }

    public Part build() {
      throw new UnsupportedOperationException("Not implemented.");
    }

  }

}
