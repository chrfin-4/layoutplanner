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
  private final Dimensions size;
  private final Dimensions origin;

  private Surface(Dimensions size, Dimensions origin) {
    this.size = size;
    this.origin = origin;
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface of(Dimensions size) {
    return surface(size, Dimensions.ZERO);
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface of(int x, int y, int z) {
    return surface(x, y, z);
  }

  /** Factory method. */
  public static Surface of(Dimensions size, Dimensions origin) {
    return surface(size, origin);
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface surface(Dimensions size) {
    return surface(size, Dimensions.ZERO);
  }

  /** Factory method. Origin defaults to (0,0). */
  public static Surface surface(int x, int y, int z) {
    return surface(Dimensions.of(x, y, z), Dimensions.ZERO);
  }

  /** Factory method. */
  public static Surface surface(Dimensions size, Dimensions origin) {
    return new Surface(size, origin);
  }

  /**
   * Returns a collection of all the positions (all the (x,y,z) triples) of the
   * bottom surface.
   */
  public Collection<Dimensions> getSurfacePositions() {
    final Collection<Dimensions> positions = new ArrayList<>();
    final int x = origin.getX() + size.getX();
    final int y = origin.getY() + size.getY();
    final int z = origin.getZ();
    for (int i = 0; i < x; i+=1) {
      for (int j = 0; j < y; j+=1) {
        positions.add(Dimensions.of(i,j,z));
      }
    }
    return positions;
  }

}
