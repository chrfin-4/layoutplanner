package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import java.util.List;

// FIXME: This is just a dummy place holder.
public class ScoreCalculator implements EasyScoreCalculator<Layout> {

  public HardSoftScore calculateScore(Layout layout) {
	int hardScore = getHardScore(layout);
    return HardSoftScore.of(hardScore, 0);
  }
  
  // Return negative number of overlapping parts
  public int getHardScore(Layout layout) {
    
	int overlap = countOverlappingParts(layout);
	
    return -overlap;
  }
  
  // Counts the number of parts that overlap eachother
  public int countOverlappingParts(Layout layout) {
    List<Part> parts = layout.getParts();
    int count = 0;
    for (int i = 0; i < parts.size() - 1; i++) {
		for(int j = i + 1; j < parts.size(); j++) {
			Part p1 = parts.get(i);
			Part p2 = parts.get(j);
			if (p1.getPosition() == null || p2.getPosition() == null) {
				continue;
			}
			if (partsOverlap(p1, p2)) {
				count++;
			}
		}
    }
    return count;
  }
  
  // Check if two parts overlap
  public static boolean partsOverlap(Part p1, Part p2) {
    int rect1xLeft = p1.getPosition().getX();
    int rect1xRight = p1.getPosition().getX() + p1.getWidth();
    int rect1yLeft = p1.getPosition().getY();
    int rect1yRight = p1.getPosition().getY() + p1.getDepth();
    int rect2xLeft = p2.getPosition().getX();
    int rect2xRight = p2.getPosition().getX() + p1.getWidth();
    int rect2yLeft = p2.getPosition().getY();
    int rect2yRight = p2.getPosition().getY() + p1.getDepth();
    return rect1xLeft < rect2xRight && rect1xRight > rect2xLeft &&
        rect1yLeft < rect2yRight && rect1yRight > rect2yLeft;
  }
  
  // Counts the number of parts outside surface
  public int countPartsOutside(Layout layout){
	  List<Part> parts = layout.getParts();
	  Surface surface = layout.getSurface();
	  int count = 0;
	  for(Part part : parts) {
		  if(part.getPosition() == null){
			  continue;
		  }
		  if(partOutside(part, surface)){
			  count++;
		  }
	  }
	  return count;
  }
  
  // Checks if part is outside of surface
  public boolean partOutside(Part part, Surface surface){
    int partWidth = part.getWidth();
    int partDepth = part.getDepth();
	Dimensions position = part.getPosition();
    int xmax = surface.width() - partWidth;
    int ymax = surface.depth() - partDepth;
    return position.getX() <= xmax && position.getY() <= ymax;
  }

}
