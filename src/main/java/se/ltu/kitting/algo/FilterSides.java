package se.ltu.kitting.algo;

import java.util.*;
import se.ltu.kitting.model.*;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

// Note: the possibility-filtering has to be strict! If we are going to remove
// all sides except for one, then we had better be sure that that side really is possible!
/**
 * A custom phase that filters the allowed sides of parts.
 *
 * There are two hard rules to keep in mind.
 * <ol>
 * <li>Mandatory sides are ALWAYS included.</li>
 * <li>Only possible sides count. If a side is allowed but cannot physically
 * be used, it is not considered (unless it is mandatory).</li>
 * </ol>
 *
 * Additionally, the following rules apply conditionally, subject to the previous two rules:
 * <ol>
 * <li>The min area side is always included.</li>
 * <li>The preferred side is always included.</li>
 * </ol>
 * If {@code forceMinArea} is ON, then there will ALWAYS be EXACTLY ONE allowed
 * side. If there is a mandatory hint, then that will be the side. If there is
 * not a mandatory hint (so this case includes non-mandatory hints) then the
 * minimum possible area side is used. So in this mode, min area overrides both
 * (non-mandatory) hints and preferred side.
 *
 * For {@code singletonForMandatory = ON}, {@code removeEquivalentSides = ON},
 * {@code minAreaOnly = ON}, and {@code forceMinArea = OFF},
 * filtering follows these rules:
 * <ul>
 * <li>If the part has a mandatory hint, then the side used in the hint is
 * the only side that is allowed.</li>
 * <li>If the part has a non-mandatory hint that side is added to the list of allowed sides.</li>
 * <li>If the part has a preferred side, that side is added to the list of allowed sides.</li>
 * <li>Lastly, the side with the minimum area is added.</li>
 * <li>No redundancy is allowed.</li>
 * </ul>
 *
 * If {@code minAreaOnly} is OFF, then all allowed sides are added, except that
 * redundancies are removed.
 *
 * Note that removing redundancies always takes hint and preferred side into account.
 * In other words, if both left and right are allowed, right would normally be removed.
 * But if the hint specifies right (or right is preferred), then left is removed
 * instead.
 *
 * Preferred is not added if it is <em>equivalent</em> to hint, since hints
 * should take precedence.
 *
 * <ul>
 * <li>FIXME: THIS ONLY WORKS IF SURFACES HAVE THE SAME DIMENSIONS!
 * If we only look at the max dimensions, then we might claim that the min area side
 * is possible, exclude the other sides, but then it turns out that that side was not
 * actually possible, and now we cannot find a feasible solution.
 * <li>TODO: change the default so that the default operation is like minAreaOnly, and
 * setting minAreaOnly has the much more aggressive meaning that anything other than
 * the min area side is included ONLY IF THERE IS A MANDATORY HINT.
 * So include hinted and preferred only if minAreaOnly is off.
 * <li>TODO: The change above includes checking that the min area side is actually possible!
 * <li>TODO: should probably split this into two different filters.
 * </ul>
 * @author Christoffer Fink
 * @deprecated Because this class is overly complicated and needs to be heavily refactored.
 *  Its behavior may change completely in the future.
 */
@Deprecated
public class FilterSides implements CustomPhaseCommand<Layout> {

  /** Filter down to a single side if that side is mandatory? (True by default.) */
  public boolean singletonForMandatory = true;
  public boolean removeEquivalentSides = true;
  /** Off by default: Aside from hint and preferred, include only min area side. */
  @Deprecated // change to true by default
  public boolean minAreaOnly = false;
  public boolean forceMinArea = true;

  public void setSingletonForMandatory(final boolean flag) {
    singletonForMandatory = flag;
  }

  public void setRemoveEquivalentSides(final boolean flag) {
    removeEquivalentSides = flag;
  }

  @Deprecated
  public void setMinAreaOnly(final boolean flag) {
    minAreaOnly = flag;
  }

  public void setForceMinArea(final boolean flag) {
    forceMinArea = flag;
  }

  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    final Layout layout = scoreDirector.getWorkingSolution();
    int countBefore = layout.getParts().stream()
      .map(Part::getAllowedDown).mapToInt(List::size).sum();
    final Set<Dimensions> surfaceDimensions = layout
      .getWagon()
      .surfaces()
      .stream()
        .map(Surface::size)
        .collect(toSet());
    layout.getParts().forEach(part -> filterSides(surfaceDimensions, part));
    int countAfter = layout.getParts().stream()
      .map(Part::getAllowedDown).mapToInt(List::size).sum();
  }

  @Deprecated(forRemoval = true)
  public Dimensions maxSurfaceDimensions(final Layout layout) {
    final Wagon wagon = layout.getWagon();
    final int maxWidth = wagon.surfaces().stream()
      .map(Surface::size).mapToInt(Dimensions::getX).sum();
    final int maxDepth = wagon.surfaces().stream()
      .map(Surface::size).mapToInt(Dimensions::getY).sum();
    final int maxHeight = wagon.surfaces().stream()
      .map(Surface::size).mapToInt(Dimensions::getZ).sum();
    return Dimensions.of(maxWidth, maxDepth, maxHeight);
  }

  /**
   * Returns a set of all surface dimensions.
   * In practice, this will likely be a singleton set.
   */
  public static Set<Dimensions> surfaceDimensions(final Layout layout) {
    return layout
      .getWagon()
      .surfaces()
      .stream()
        .map(Surface::size)
        .collect(toSet());
  }

  /**
   * Return the allowed and possible sides for the given part.
   * If processing multiple parts, using {@link #surfaceDimensions(Layout)}
   * and {@link #possibleSides(Set, Part)} is more efficient.
   */
  public static List<Side> possibleSides(final Layout layout, final Part part) {
    return possibleSides(surfaceDimensions(layout), part);
  }

  public static List<Side> possibleSides(final Set<Dimensions> surfaces, final Part part) {
    final var partSize = part.getSize();
    return part.getAllowedDown().stream()
      .filter(side -> {
          final var onSide = dimensionsOnSide(partSize, side);
          return surfaces.stream()
            .anyMatch(surface -> couldFit(surface, onSide));
      })
      .collect(toList());
  }

  public List<Side> possibleSides(final Dimensions max, final Part part) {
    final var partSize = part.getSize();
    return part.getAllowedDown().stream()
      .filter(side -> couldFit(max, dimensionsOnSide(partSize, side)))
      .collect(toList());
  }

  public static Dimensions dimensionsOnSide(final Dimensions original, final Side side) {
    return Rotation.rotateOntoSide(side, original);
  }

  public static boolean couldFit(final Dimensions bound, final Dimensions size) {
    return size.z <= bound.z
      && ((size.x <= bound.x && size.y <= bound.y)  // 0 rotation
      || (size.x <= bound.y && size.y <= bound.x)); // 90 rotation
  }

  public void filterSides(final Dimensions bound, final Part part) {
    part.setAllowedDown(getFiltered(bound, part));
  }

  public void filterSides(final Set<Dimensions> surfaces, final Part part) {
    part.setAllowedDown(getFiltered(surfaces, part));
  }

  public List<Side> getFiltered(final Set<Dimensions> surfaces, final Part part) {
    final Set<Side> allowed = EnumSet.copyOf(possibleSides(surfaces, part));
    final List<Side> keep = new ArrayList<>();
    final var hint = part.getHint();
    if (part.hasHint() && hint.side().isPresent()) {
      final var side = hint.side().get();
      keep.add(side);
      if (singletonForMandatory && part.hasMandatoryHint()) {
        return keep;
      }
      allowed.remove(side);
      if (removeEquivalentSides) {
        allowed.remove(side.opposite());
      }
    }

    final var preferred = part.getPreferredDown();
    if (allowed.contains(preferred)) {
      assert !keep.contains(preferred);
      keep.add(preferred);
      allowed.remove(preferred);
      if (removeEquivalentSides) {
        allowed.remove(preferred.opposite());
      }
    }

    if (minAreaOnly) {
      final var side = part.minAreaSide();
      if (!keep.contains(side) && !keep.contains(side.opposite())) {
        keep.add(side);
      }
      return keep;
    }

    if (removeEquivalentSides) {
      keep.addAll(Side.normalize(allowed));  // Remove redundant sides from remaining.
    } else {
      keep.addAll(allowed); // Don't remove redundant sides.
    }
    return keep;
  }


  public List<Side> getFiltered(final Dimensions bound, final Part part) {
    final Set<Side> allowed = EnumSet.copyOf(possibleSides(bound, part));
    final List<Side> keep = new ArrayList<>();
    final var hint = part.getHint();
    if (part.hasHint() && hint.side().isPresent()) {
      final var side = hint.side().get();
      keep.add(side);
      if (singletonForMandatory && part.hasMandatoryHint()) {
        return keep;
      }
      allowed.remove(side);
      if (removeEquivalentSides) {
        allowed.remove(side.opposite());
      }
    }

    final var preferred = part.getPreferredDown();
    if (allowed.contains(preferred)) {
      assert !keep.contains(preferred);
      keep.add(preferred);
      allowed.remove(preferred);
      if (removeEquivalentSides) {
        allowed.remove(preferred.opposite());
      }
    }

    if (minAreaOnly) {
      final var side = part.minAreaSide();
      if (!keep.contains(side) && !keep.contains(side.opposite())) {
        keep.add(side);
      }
      return keep;
    }

    if (removeEquivalentSides) {
      keep.addAll(Side.normalize(allowed));  // Remove redundant sides from remaining.
    } else {
      keep.addAll(allowed); // Don't remove redundant sides.
    }
    return keep;
  }

}
