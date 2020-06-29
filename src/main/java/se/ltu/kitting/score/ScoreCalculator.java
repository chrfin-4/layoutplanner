package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;

// FIXME: This is just a dummy place holder.
public class ScoreCalculator implements EasyScoreCalculator<Layout> {

  public HardSoftScore calculateScore(Layout layout) {
	int hardScore = getHardScore(layout);
    return HardSoftScore.of(hardScore, 0);
  }
  
  // Return negative number of overlapping parts
  public int getHardScore(Layout layout) {
    
	overlap = countOverlappingParts(layout);
	
    return -overlap;
  }
  
  // Counts the number of parts that overlap each other.
  public int countOverlappingParts(Layout layout) {
    List<Part> parts = layout.getParts();
    int count = 0;
    for (int i = 0; i < parts.size() - 1; i++) {
      Part p1 = parts.get(i);
      Part p2 = parts.get(i+1);
      if (p1.getPosition() == null || p2.getPosition() == null) {
        continue;
      }
      if (partsOverlap(p1, p2)) {
        count++;
      }
    }
    return count;
  }

}
