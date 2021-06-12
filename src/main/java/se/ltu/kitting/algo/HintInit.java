package se.ltu.kitting.algo;

import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

/**
 * A custom phase that initializes those parts that have hints to the values
 * given in the hints. Initialization of side/rotation/position can optionally
 * be turned on/off. (All are on by default.)
 * <p>
 * Setting {@code initPosition} on
 * but {@code initSide} and {@code initRotation} off
 * is illegal. Initializing the position is impossible unless the side and
 * rotation are also initialized. (At least as long as hints use center
 * position while parts use corner position.)
 * <p>
 * By default also initializes side using the preferred side if no hint exists.
 * @author Christoffer Fink
 */
public class HintInit implements CustomPhaseCommand<Layout> {

  /** Use hinted position for initialization. */
  public boolean initPosition = true;
  /** Use hinted side for initialization. */
  public boolean initSide = true;
  /** Use hinted rotation for initialization. */
  public boolean initRotation = true;
  /** Use preferred side for initialization if no side is hinted. */
  public boolean initPreferred = true;

  public void setInitPosition(final boolean flag) {
    this.initPosition = flag;
  }

  public void setInitSide(final boolean flag) {
    this.initSide = flag;
  }

  public void setInitRotation(final boolean flag) {
    this.initRotation = flag;
  }

  public void setInitPreferred(final boolean flag) {
    this.initPreferred = flag;
  }

  public void assertValid() {
    if (initPosition && !(initSide && initRotation)) {
      throw new IllegalStateException("Cannot initialize position without side and rotation");
    }
  }

  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    assertValid();
    final Layout layout = scoreDirector.getWorkingSolution();
    for (Part part : layout.getParts()) {
      if (part.getHint() != null) {
        if (initRotation) {
          scoreDirector.beforeVariableChanged(part, "rotation");
          part.getHint().rotation().ifPresent(part::setRotation);
          scoreDirector.afterVariableChanged(part, "rotation");
        }

        if (initSide) {
          scoreDirector.beforeVariableChanged(part, "sideDown");
          part.getHint().side().ifPresent(part::setSideDown);
          scoreDirector.afterVariableChanged(part, "sideDown");
        }

        if (initPosition) {
          Dimensions pos = part.getHint().centerPosition();
          if (part.getRotation() != null && part.getSideDown() != null) {
            pos = Part.centerToCorner(pos, part.currentDimensions());
          }
          pos = Dimensions.of(pos.x, pos.y, part.getHint().surfaceId());

          scoreDirector.beforeVariableChanged(part, "position");
          part.setPosition(pos);
          scoreDirector.afterVariableChanged(part, "position");
        }
      } else if (initPreferred) {
        final var preferred = part.getPreferredDown();
        if (preferred != null) {
          scoreDirector.beforeVariableChanged(part, "sideDown");
          part.setSideDown(preferred);
          scoreDirector.afterVariableChanged(part, "sideDown");
        }
      }
      scoreDirector.triggerVariableListeners();
    }
  }

}
