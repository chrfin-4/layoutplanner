package se.ltu.kitting.model;

import java.util.List;
import java.util.ArrayList;

public class PositionProvider {

  public static List<Dimensions> positions(Wagon wagon, int stepSize) {
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
    return positions;
  }

}
