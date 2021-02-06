package se.ltu.kitting.algo;

import se.ltu.kitting.model.Layout;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import java.util.Random;
import java.util.List;

/**
 * Custom phase that randomly initializes parts.
 * Initialization is almost completely random (and hence unintelligent),
 * except that the preferred side is preferred, if available.
 * @author Christoffer Fink
 */
public class RandomInit implements CustomPhaseCommand<Layout> {

  /** Defaults to current time. */
  public long seed = System.currentTimeMillis();

  public void setSeed(final long seed) {
    this.seed = seed;
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
        scoreDirector.beforeVariableChanged(part, "position");
      }
      if (part.getRotation() == null) {
        scoreDirector.beforeVariableChanged(part, "rotation");
        part.setRotation(se.ltu.kitting.model.Rotation.ZERO);
        scoreDirector.beforeVariableChanged(part, "rotation");
      }
      if (part.getSideDown() == null) {
        final var side = (part.getPreferredDown() != null)
          ? part.getPreferredDown()
          : part.getAllowedSidesDown().get(0);
        scoreDirector.beforeVariableChanged(part, "sideDown");
        part.setSideDown(side);
        scoreDirector.beforeVariableChanged(part, "sideDown");
      }
      scoreDirector.triggerVariableListeners();
    }
  }

}
