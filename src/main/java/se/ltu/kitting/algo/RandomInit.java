package se.ltu.kitting.algo;

import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Side;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import java.util.Random;
import java.util.List;

/**
 * Custom phase that randomly initializes parts.
 * Currently ignores parts that have layout hints.
 * <p>
 * Initializes positions randomly. Rotations are initialized to zero.
 * Side down defaults to preferred, if available, and then min area, unless
 * {@link #usePreferredSide} and/or {@link #useMinAreaSide} are set to
 * {@code false}.
 * @author Christoffer Fink
 */
public class RandomInit implements CustomPhaseCommand<Layout> {

  // TODO: add a flag for controlling whether to randomize rotation?

  /** Defaults to current time. */
  public long seed = System.currentTimeMillis();
  /** By default, use preferred side instead of purely random. */
  public boolean usePreferredSide = true;
  /** By default, use min area side instead of purely random. */
  public boolean useMinAreaSide = true;

  public void setSeed(final long seed) {
    this.seed = seed;
  }

  public void setUseMinAreaSide(final boolean flag) {
    this.useMinAreaSide = flag;
  }

  public void setUsePreferredSide(final boolean flag) {
    this.usePreferredSide = flag;
  }

  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    final Layout layout = scoreDirector.getWorkingSolution();
    final var positions = List.copyOf(layout.getPositions());
    final var rng = new Random(seed);
    final int max = positions.size();
    for (final var part : layout.getParts()) {
      if (part.hasHint()) {
        // TODO: Remove this check? In practice, this phase will run after
        // HintInit. So the tests below will fail anyway for parts with hints.
        continue;
      }
      if (part.getPosition() == null) {
        scoreDirector.beforeVariableChanged(part, "position");
        part.setPosition(positions.get(rng.nextInt(max)));
        scoreDirector.afterVariableChanged(part, "position");
      }
      if (part.getRotation() == null) {
        scoreDirector.beforeVariableChanged(part, "rotation");
        part.setRotation(se.ltu.kitting.model.Rotation.ZERO);
        scoreDirector.afterVariableChanged(part, "rotation");
      }
      if (part.getSideDown() == null) {
        final var side = getSide(part, rng);
        scoreDirector.beforeVariableChanged(part, "sideDown");
        part.setSideDown(side);
        scoreDirector.afterVariableChanged(part, "sideDown");
      }
      scoreDirector.triggerVariableListeners();
    }
  }

  private Side getSide(final Part part, final Random rng) {
    final var hasPreferred = part.getPreferredDown() != null;
    if (hasPreferred && usePreferredSide) {
      return part.getPreferredDown();
    }
    if (useMinAreaSide) {
      return part.minAreaSide();
    }
    return randomSide(part, rng);
  }

  private Side randomSide(final Part part, final Random rng) {
    final var allowed = part.getAllowedSidesDown();
    return allowed.get(rng.nextInt(allowed.size()));
  }

}
