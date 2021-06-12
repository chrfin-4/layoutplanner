package se.ltu.kitting.algo;

import se.ltu.kitting.model.Layout;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

// TODO: Consider renaming? "Resolution" may be misleading since a larger
// value implies a smaller step size.
// Also, should probably be responsible for (un)shuffling positions.
// So "resolution" may be too specific as well.
/**
 * A custom phase command that adjusts the position resolution (step size).
 * @author Christoffer Fink
 */
public class ResolutionChange implements CustomPhaseCommand<Layout> {

  /** The new step size to apply. */
  public int resolution = 1;
  /** Verbose mode outputs the new resolution being applied. */
  public boolean verbose = false;

  public void setResolution(final int resolution) {
    this.resolution = resolution;
  }

  public void setVerbose(final boolean flag) {
    this.verbose = flag;
  }

  /**
   * Applies the step size.
   * Note that the name of this method is slightly misleading, since it
   * doesn't actually change any planning variables of the solution. It only
   * affects the value provider for positions.
   */
  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    final Layout layout = scoreDirector.getWorkingSolution();
    // TODO: use logging framework instead?
    if (verbose) {
      System.out.println("Setting resolution to " + resolution);
    }
    layout.setPositionStepSize(resolution);
  }

}
