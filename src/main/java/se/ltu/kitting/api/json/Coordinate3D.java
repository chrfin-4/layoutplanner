package se.ltu.kitting.api.json;

import se.ltu.kitting.model.Dimensions;

public class Coordinate3D {

  public int x, y, z;

  public Coordinate3D(double x, double y, double z) {
    this((int) x, (int) y, (int) z);
  }

  public Coordinate3D(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Dimensions toDimensions() {
    return Dimensions.of(x, y, z);
  }

  public static Coordinate3D from(Dimensions d) {
    return new Coordinate3D(d.x, d.y, d.z);
  }

}
