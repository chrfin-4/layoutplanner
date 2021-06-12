package se.ltu.kitting.algo;

import se.ltu.kitting.model.Part;
import java.util.Comparator;

/**
 * Compare parts based on their hinted or minimum area.
 * In general, placing large parts first is often effective, because it's
 * easier to squeeze smaller parts into whatever gaps are left over.
 * However, it makes sense to prioritize placing parts with hints first, even
 * though that generally makes them easier rather than harder to place.
 * In practice, placing parts based on hints should probably be done with a
 * custom initialization algorithm (such as HintInit), but then this
 * comparison will still fall back on area. So it should work well either way.
 * @author Christoffer Fink
 */
public class HintThenMinComparator implements Comparator<Part> {

  @Override
  public int compare(final Part p1, final Part p2) {
    return Integer.compare(getArea(p1), getArea(p2));
  }

  private int getArea(final Part p) {
    if (p.hasHint() && p.getHint().side().isPresent()) {
      final var side = p.getHint().side().get();
      return p.areaOf(side);
    }
    return p.minAllowedArea();
  }

}
