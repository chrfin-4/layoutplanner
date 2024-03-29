package se.ltu.kitting.model;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Represents the region(s) of a wagon where parts can be placed.
 * Note that "surface" may sound misleading because it's a 3D object,
 * but most of the time, the bottom surface is most relevant.
 * @author Christoffer Fink
 */
public class Surface {

  public final int id;
  /** The (width,depth,height) of the surface. */
  public final Dimensions dimensions;
  public final Dimensions origin;

  @Deprecated
  private Surface(Dimensions dimensions, Dimensions origin) {
    this(-1, dimensions, origin);
  }

  private Surface(int id, Dimensions dimensions, Dimensions origin) {
    this.dimensions = dimensions;
    this.origin = origin;
    this.id = id;
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface of(int id, Dimensions dimensions) {
    return surface(id, dimensions, Dimensions.ZERO);
  }

  /** Factory method. Origin defaults to (0,0), id defaults to -1. */
  public static Surface of(Dimensions dimensions) {
    return surface(dimensions, Dimensions.ZERO);
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface of(int x, int y, int z) {
    return surface(x, y, z);
  }

  /** Factory method. */
  public static Surface of(Dimensions dimensions, Dimensions origin) {
    return surface(dimensions, origin);
  }

  public int id() {
    return id;
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface surface(Dimensions dimensions) {
    return surface(dimensions, Dimensions.ZERO);
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface surface(int x, int y, int z) {
    return surface(Dimensions.of(x, y, z), Dimensions.ZERO);
  }

  /** Factory method. */
  public static Surface surface(Dimensions dimensions, Dimensions origin) {
    return new Surface(dimensions, origin);
  }

  /** Factory method. */
  public static Surface surface(int id, Dimensions dimensions, Dimensions origin) {
    return new Surface(id, dimensions, origin);
  }

  public int width() {
    return dimensions.getX();
  }

  public int depth() {
    return dimensions.getY();
  }

  public int height() {
    return dimensions.getZ();
  }

  public int volume() {
    return dimensions.x * dimensions.y * dimensions.z;
  }

  public Dimensions size() {
    return dimensions;
  }

}
