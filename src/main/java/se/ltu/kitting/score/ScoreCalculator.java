package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import java.util.List;
import ch.rfin.util.Pair;

// FIXME: This is just a dummy place holder.
public class ScoreCalculator implements EasyScoreCalculator<Layout> {

  public HardSoftScore calculateScore(Layout layout) {
	int hardScore = getHardScore(layout);
    return HardSoftScore.of(hardScore, 0);
  }
  
  // Return negative number of overlapping parts
  public int getHardScore(Layout layout) {
    int outside = countPartsOutside(layout);
	int overlap = countOverlappingParts(layout);
    return -(overlap + outside);
  }
  
  // Counts the number of parts that overlap eachother
  // NOTE: Counts same overlap multiple times
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
	Pair<Dimensions,Dimensions> currentRegionP1 = p1.currentRegion();
	Pair<Dimensions,Dimensions> currentRegionP2 = p2.currentRegion();
	Dimensions startPositionP1 = currentRegionP1._1;
	Dimensions endPositionP1 = currentRegionP1._2;
	Dimensions startPositionP2 = currentRegionP2._1;
	Dimensions endPositionP2 = currentRegionP2._2;
	
	// Parts do not overlap if they are on different surfaces
	if(startPositionP1.getZ() != startPositionP2.getZ()){
	  return false;
	}
	
	int rect1xLeft = startPositionP1.getX();
    int rect1xRight = endPositionP1.getX();
    int rect1yBack = startPositionP1.getY();
    int rect1yFront = endPositionP1.getY();
    int rect2xLeft = startPositionP2.getX();
    int rect2xRight = endPositionP2.getX();
    int rect2yBack = startPositionP2.getY();
    int rect2yFront = endPositionP2.getY();  
	  
    return rect1xLeft <= rect2xRight && rect1xRight >= rect2xLeft &&
      rect1yBack <= rect2yFront && rect1yFront >= rect2yBack;
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
	Pair<Dimensions,Dimensions> currentRegion = part.currentRegion();
	boolean height = currentRegion._2.getZ() >= surface.height();
	boolean depth = currentRegion._2.getY() >= surface.depth();
	boolean width = currentRegion._2.getX() >= surface.width();
	return height || depth || width;
  }

}
