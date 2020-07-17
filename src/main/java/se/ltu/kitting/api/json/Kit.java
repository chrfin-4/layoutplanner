package se.ltu.kitting.api.json;

import se.ltu.kitting.model.Side;

public class Kit {
  // Required.
  public String kitId;
  public String chassisId;
  // Optional.
  public Side side;

  public Side side() {
    return side == null ? Side.left : side;
  }

}
