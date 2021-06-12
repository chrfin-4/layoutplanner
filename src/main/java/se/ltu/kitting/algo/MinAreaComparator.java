package se.ltu.kitting.algo;

import se.ltu.kitting.model.Part;
import java.util.Comparator;

/**
 * Compare parts based on their minimum allowed area - the smallest area of
 * all allowed sides. Note that there is no guarantee that the area is
 * achievable in practice. Just because a part is allowed to be placed on
 * a certain side does not mean it can fit on a specific surface when placed
 * on that side. So the area may be allowed but impossible.
 * <p>
 * This comparison also does not take hints or the preferred side into account.
 * This means that, although the part is more likely to be placed on the hinted
 * or preferred side, which may not be the minimum side, the minimum side is
 * still used for comparison.
 * <p>
 * Of course, some of these considerations become moot if a custom phase is
 * used to "manually" initialize parts using hints.
 * <p>
 * In addition, the area may not be the only factor impacting difficulty.
 * For example, the fact that a hint exists could be viewed as an increased
 * difficulty. After all, the feasible/optimal placements of that part are
 * then restricted. It cannot be placed arbitrarily.
 * <p>
 * The number of allowed sides could also be considered. A part with more
 * sides allowed is more flexible. It can be placed in more different ways.
 * <p>
 * This class aims to make the comparison simple rather than clever. So it
 * avoids the tricky business of weighing up different considerations.
 * @see se.ltu.kitting.algo.HintThenMinComparator
 * @author Christoffer Fink
 */
public class MinAreaComparator implements Comparator<Part> {

  @Override
  public int compare(final Part p1, final Part p2) {
    return Integer.compare(p1.minAllowedArea(), p2.minAllowedArea());
  }

}
