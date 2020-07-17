package se.ltu.kitting.api.json;

public class Rotation {

  public int rotation_x;
  public int rotation_y;
  public int rotation_z;

  public Rotation(double x, double y, double z) {
    this((int) x, (int) y, (int) z);
  }

  public Rotation(int x, int y, int z) {
    this.rotation_x = x;
    this.rotation_y = y;
    this.rotation_z = z;
  }

  public se.ltu.kitting.model.Rotation toRotation() {
    return se.ltu.kitting.model.Rotation.of(rotation_z);
  }

  public static Rotation from(se.ltu.kitting.model.Rotation r) {
    return new Rotation(r.x, r.y, r.z);
  }

}
