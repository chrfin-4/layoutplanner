package se.ltu.kitting.model;

import java.util.List;
import java.util.ArrayList;

// TODO: Check whether OptaPlanner needs to be notified of a resolution
// change somehow.
// TODO: Add ability to control seed.
// TODO: Add a way to specify the desired number of positions.
// (Instead of setting a step size/resolution, figure out what the step
// size needs to be to cut the space down to N positions.)
public class PositionProvider {

  private final Wagon wagon;
  public int stepSize = 1;
  public boolean randomize = false;

  public PositionProvider(final Wagon wagon) {
    this.wagon = wagon;
  }

  public void setStepSize(final int setting) {
    this.stepSize = setting;
  }

  public void setRandom(final boolean setting) {
    this.randomize = setting;
  }

  public List<Dimensions> positions() {
    return positions(wagon, stepSize, randomize);
  }

  // Backward compatibility.
  @Deprecated(forRemoval = true)
  public static List<Dimensions> positions(Wagon wagon, int stepSize) {
    return positions(wagon, stepSize, false);
  }

  public static List<Dimensions> positions(Wagon wagon, int stepSize, boolean randomize) {
    final List<Dimensions> positions = new ArrayList<>();
    for (Surface surface : wagon.surfaces()) {
      final int x = surface.origin.getX() + surface.dimensions.getX();
      final int y = surface.origin.getY() + surface.dimensions.getY();
      final int z = surface.id;
      for (int i = 0; i < x; i += stepSize) {
        for (int j = 0; j < y; j += stepSize) {
          positions.add(Dimensions.of(i,j,z));
        }
      }
    }
    if (randomize) {
      java.util.Collections.shuffle(positions);
    }
    return positions;
  }

}
