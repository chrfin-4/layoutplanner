package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;

/**
 * Returns the combined hard and soft score of a layout.
 * Used to define how good a solution is.
 * Hard score have a higher priority than soft score.
 */
public class ScoreCalculator implements EasyScoreCalculator<Layout, HardSoftLongScore> {

  public HardSoftLongScore calculateScore(Layout layout) {
    int hardScore = HardScore.getHardScore(layout);
    int softScore = SoftScore.getSoftScore(layout);
    return HardSoftLongScore.of(hardScore, softScore);
  }

}
