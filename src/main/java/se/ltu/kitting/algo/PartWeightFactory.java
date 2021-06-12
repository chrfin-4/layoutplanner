package se.ltu.kitting.algo;

import se.ltu.kitting.model.*;
import se.ltu.kitting.util.Util;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

// TODO: It would make sense to have a special version that does not consider
// hints or preferred sides, based on the assumption that such parts have
// already been initialized by a custom phase (or by some selector that has
// made sure that those entities and variables are dealt with first).
// TODO: The name is too general and does not give any information about the
// comparison criterion used.
/**
 * Uses the minimum possible area as the difficulty metric.
 * The area is ensured to be weakly possible (according to
 * {@link se.ltu.kitting.util.Util#possibleSidesWeak(Set, Part)}).
 * @author Christoffer Fink
 */
public class PartWeightFactory implements SelectionSorterWeightFactory<Layout, Part> {

  /**
   * @return the area of the hinted side, the preferred side, or the minimum side,
   * in that order, whichever matches first.
   */
  @Override
  public Integer createSorterWeight(final Layout layout, final Part part) {
    final var surfaces = Util.surfaceDimensions(layout);
    final var possible = Util.possibleSidesWeak(surfaces, part);
    final var hint = part.getHint();
    if (part.hasHint()) {
      final var side = hint.side().orElse(null);
      if (possible.contains(side)) {
        return part.areaOf(side);
      }
    }
    final var preferred = part.getPreferredDown();
    if (possible.contains(preferred)) {
      return part.areaOf(preferred);
    }
    return possible.stream().mapToInt(part::areaOf).min().getAsInt();
  }

}
