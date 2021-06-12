package se.ltu.kitting.algo;

import java.util.*;
import se.ltu.kitting.model.*;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

// Note that the implementation of this class relies on the method
// Part.setAllowedDown(sides) accepting the list of sides as-is and NOT
// doing any processing where the order may be changed.
/**
 * A custom phase that sorts the allowed sides of parts.
 * @author Christoffer Fink
 */
public class SortSidesPhase implements CustomPhaseCommand<Layout> {

  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    final Layout layout = scoreDirector.getWorkingSolution();
    layout.getParts().forEach(this::sortSides);
  }

  public void sortSides(final Part part) {
    part.setAllowedDown(sortSides(part.getAllowedDown(), comparator(part)));
  }

  public List<Side> sortSides(final List<Side> sides, final Comparator<Side> cmp) {
    final List<Side> sorted = new ArrayList<>(sides);
    Collections.sort(sorted, cmp);
    return sorted;
  }

  public Comparator<Side> comparator(final Part part) {
    return (s1, s2) -> Integer.compare(sideArea(part, s1), sideArea(part, s2));
  }

  public boolean isPreferredSide(final Part part, final Side side) {
    return side.equals(part.getPreferredDown());
  }

  public boolean isHintedSide(final Part part, final Side side) {
    if (!part.hasHint()) {
      return false;
    }
    return side.equals(part.getHint().side().orElse(null));
  }

  /**
   * Private because of the -1 and 0 special values for hinted and preferred.
   * That makes this unsuitable as a public method that may look more
   * general-purpose than it actually is.
   */
  private int sideArea(final Part part, final Side side) {
    if (isHintedSide(part, side)) {
      return -1;
    }
    if (isPreferredSide(part, side)) {
      return 0;
    }
    final var dim = Rotation.rotateOntoSide(side, part.getSize());
    return dim.x * dim.y;
  }

}
