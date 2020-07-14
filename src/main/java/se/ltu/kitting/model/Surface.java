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

  /** The (width,depth,height) of the surface. */
  public final Dimensions dimensions;
  public final Dimensions origin;

  private Surface(Dimensions dimensions, Dimensions origin) {
    this.dimensions = dimensions;
    this.origin = origin;
  }

  /** Factory method. Origin defaults to (0,0). */
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

  public int width() {
    return dimensions.getX();
  }

  public int depth() {
    return dimensions.getY();
  }

  public int height() {
    return dimensions.getZ();
  }

  /**
   * Returns a collection of all the positions (all the (x,y,z) triples) of the
   * bottom surface.
   */
  public Collection<Dimensions> getSurfacePositions() {
    final Collection<Dimensions> positions = new ArrayList<>();
    final int x = origin.getX() + dimensions.getX();
    final int y = origin.getY() + dimensions.getY();
    final int z = origin.getZ();
    for (int i = 0; i < x; i+=10) {
      for (int j = 0; j < y; j+=10) {
        positions.add(Dimensions.of(i,j,z));
      }
    }
    return positions;
  }

}
