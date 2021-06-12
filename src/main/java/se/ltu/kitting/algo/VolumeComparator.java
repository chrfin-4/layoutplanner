package se.ltu.kitting.algo;

import java.util.Comparator;
import se.ltu.kitting.model.Part;

/**
 * Compare parts based on their size (volume).
 * Smaller volume implies lower difficulty.
 * @author Christoffer Fink
 */
public class VolumeComparator implements Comparator<Part> {

  @Override
  public int compare(final Part p1, final Part p2) {
    return Integer.compare(p1.volume(), p2.volume());
  }

}
