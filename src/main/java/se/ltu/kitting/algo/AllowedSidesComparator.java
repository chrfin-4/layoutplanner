package se.ltu.kitting.algo;

import java.util.Comparator;
import se.ltu.kitting.model.Part;
import static se.ltu.kitting.model.Side.withoutRedundancy;

// TODO: Currently breaks ties using the min allowed area. In the future,
// do not break ties locally and instead rely on chaining this with
// another comparator for explicitly breaking ties.
/**
 * Compare parts based on the number of allowed sides.
 * Intended for use for entity ordering (such as First Fit
 * <strong>decreasing</strong>). More sides allowed implies more flexibility in
 * how the part can be placed, which should imply a lower difficulty.
 * @author Christoffer Fink
 */
public class AllowedSidesComparator implements Comparator<Part> {

  @Override
  public int compare(final Part p1, final Part p2) {
    final int count1 = withoutRedundancy(p1.getAllowedDown()).size();
    final int count2 = withoutRedundancy(p2.getAllowedDown()).size();
    //return Integer.compare(count2, count1);

    // Note the order! Larger count => easier.
    final var countResult = Integer.compare(count2, count1);
    // Break ties using area. (TODO: Remove this!)
    if (countResult == 0) {
      return Integer.compare(p1.minAllowedArea(), p2.minAllowedArea());
    }
    return countResult;
  }

}
