package se.ltu.kitting.model;

public class WagonHint {

  private final Wagon wagon;
  private final int weightFactor;

  public WagonHint(Wagon wagon, int weightFactor) {
    this.wagon = wagon;
    this.weightFactor = weightFactor;
  }

  public Wagon wagon() {
    return wagon;
  }

  public int weightFactor() {
    return weightFactor;
  }

}
