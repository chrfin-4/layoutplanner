package se.ltu.kitting.algo;

import se.ltu.kitting.model.Layout;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

// TODO: Consider renaming? "Resolution" may be misleading since a larger
// value implies a smaller step size.
/**
 * A custom phase command that adjusts the position resolution (step size).
 * @author Christoffer Fink
 */
public class ResolutionChange implements CustomPhaseCommand<Layout> {

  private int resolution = 1;

  public void setResolution(final int resolution) {
    this.resolution = resolution;
  }

  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    final Layout layout = scoreDirector.getWorkingSolution();
    layout.setPositionStepSize(resolution);
  }

}
