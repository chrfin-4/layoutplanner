package se.ltu.kitting.algo;

import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

/**
 * A custom phase that initializes those parts that have hints to the values
 * given in the hints.
 * @author Christoffer Fink
 */
public class HintInit implements CustomPhaseCommand<Layout> {

  @Override
  public void changeWorkingSolution(final ScoreDirector<Layout> scoreDirector) {
    final Layout layout = scoreDirector.getWorkingSolution();
    for (Part part : layout.getParts()) {
      if (part.getHint() != null) {
        scoreDirector.beforeVariableChanged(part, "rotation");
        part.getHint().rotation().ifPresent(part::setRotation);
        scoreDirector.beforeVariableChanged(part, "rotation");

        scoreDirector.beforeVariableChanged(part, "sideDown");
        part.getHint().side().ifPresent(part::setSideDown);
        scoreDirector.beforeVariableChanged(part, "sideDown");

        Dimensions pos = part.getHint().centerPosition();
        if (part.getRotation() != null && part.getSideDown() != null) {
          pos = Part.centerToCorner(pos, part.currentDimensions());
        }
        pos = Dimensions.of(pos.x, pos.y, part.getHint().surfaceId());

        scoreDirector.beforeVariableChanged(part, "position");
        part.setPosition(pos);
        scoreDirector.beforeVariableChanged(part, "position");

        scoreDirector.triggerVariableListeners();
      }
    }
  }

}
