package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;

/**
 * Returns the combined hard and soft score of a layout.
 * Used to define how good a solution is.
 * Hard score have a higher priority than soft score.
 */
public class ScoreCalculator implements EasyScoreCalculator<Layout> {

  public HardSoftScore calculateScore(Layout layout) {
	int hardScore = HardScore.getHardScore(layout);
	int softScore = SoftScore.getSoftScore(layout);
    return HardSoftScore.of(hardScore, softScore);
  }
  
}
