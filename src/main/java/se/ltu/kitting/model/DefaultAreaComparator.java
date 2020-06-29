package se.ltu.kitting.model;

import java.util.Comparator;

/**
 * Compare parts based on their default (rotation = ZERO) areas.
 * Hence it's the "default" area.
 * Note that this may not be the area that the part actually ends up
 * taking up on the surface, depending on how it gets rotated.
 */
public class DefaultAreaComparator implements Comparator<Part> {

  @Override
  public int compare(Part p1, Part p2) {
    int x1 = p1.getSize().getX();
    int y1 = p1.getSize().getY();
    int x2 = p2.getSize().getX();
    int y2 = p2.getSize().getY();
    return Integer.compare(x1*y1, x2*y2);
  }

}
