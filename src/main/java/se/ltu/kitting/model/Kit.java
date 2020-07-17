package se.ltu.kitting.model;

public class Kit {
  private final String kitId;
  private final String chassisId;
  private final Side side;

  public Kit(String kitId, String chassisId) {
    this(kitId, chassisId, null);
  }

  public Kit(String kitId, String chassisId, Side side) {
    this.kitId = kitId;
    this.chassisId = chassisId;
    this.side = side;
  }

  public String kitId() {
    return kitId;
  }

  public String chassisId() {
    return chassisId;
  }

  public Side side() {
    return side;
  }

}
