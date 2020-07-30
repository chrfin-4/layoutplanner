package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import java.util.List;
import ch.rfin.util.Pair;

/**
 * Calculates the hard score for a solution. 
 * Desirable score is 0 which guarantees that:
 * No overlap, no parts outside of surface, no parts placed on a disallowed side and
 * parts with a mandatory hint are on the desired position.
 */
public class HardScore {

  // Return negative score for parts overlapping, outside or placed on disallowed side
  public static int getHardScore(Layout layout) {
    int outside = countPartsOutside(layout);
		int overlap = countOverlappingParts(layout);
		int disallowedSide = countDisallowedSidesDown(layout);
		int misplacedParts = countMisplacedParts(layout);
    return -(overlap + outside + disallowedSide + misplacedParts);
  }

  // Counts the number of parts that overlap eachother
  // NOTE: Counts same overlap multiple times
  public static int countOverlappingParts(Layout layout) {
    List<Part> parts = layout.getParts();
    int count = 0;
    for (int i = 0; i < parts.size() - 1; i++) {
			for(int j = i + 1; j < parts.size(); j++) {
			Part p1 = parts.get(i);
			Part p2 = parts.get(j);
			if (p1.getPosition() != null && p2.getPosition() != null){
				if(partsOverlap(p1, p2)) {
					count++;
				}
			}
			}
    }
    return count;
  }

  // Check if two parts overlap - considering maximal margin between two parts
  public static boolean partsOverlap(Part p1, Part p2) {
		Pair<Dimensions,Dimensions> currentRegionP1 = p1.currentRegion();
		Pair<Dimensions,Dimensions> currentRegionP2 = p2.currentRegion();
		int margin = Math.max(p1.getMargin(), p2.getMargin());
		Dimensions startPositionP1 = currentRegionP1._1;
		Dimensions endPositionP1 = currentRegionP1._2;
		Dimensions startPositionP2 = currentRegionP2._1;
		Dimensions endPositionP2 = currentRegionP2._2;
		// Parts do not overlap if they are on different surfaces
		if(startPositionP1.getZ() != startPositionP2.getZ()){
			return false;
		}
		int rect1xLeft = startPositionP1.getX() - margin;
    int rect1xRight = endPositionP1.getX() + margin;
    int rect1yBack = startPositionP1.getY() - margin;
    int rect1yFront = endPositionP1.getY() + margin;
    int rect2xLeft = startPositionP2.getX();
    int rect2xRight = endPositionP2.getX();
    int rect2yBack = startPositionP2.getY();
    int rect2yFront = endPositionP2.getY();
    return rect1xLeft <= rect2xRight && rect1xRight >= rect2xLeft &&
      rect1yBack <= rect2yFront && rect1yFront >= rect2yBack;
  }

  // Counts the number of parts outside surface
  public static int countPartsOutside(Layout layout){
    List<Part> parts = layout.getParts();
    int count = 0;
    for(Part part : parts) {
      if(part.getPosition() != null){
        if(partOutside(part, layout.surfaceOf(part))){
          count++;
        }
      }
    }
    return count;
  }

  // Checks if part is outside of surface
  public static boolean partOutside(Part part, Surface surface){
    Dimensions partEnd = part.getPosition().plus(part.currentDimensions());
    // Dimensions surfaceEnd = surface.origin.plus(surface.dimensions);
		Dimensions surfaceEnd = surface.dimensions;
    boolean height = partEnd.z > surfaceEnd.z;
    boolean depth = partEnd.y > surfaceEnd.y;
    boolean width = partEnd.x > surfaceEnd.x;
    return height || depth || width;
  }

  // Count parts placed on a disallowed side
  public static int countDisallowedSidesDown(Layout layout){
    List<Part> parts = layout.getParts();
		int count = 0;
		for(Part part : parts) {
			if(part.getPosition() != null && part.getSideDown() != null){
				if(!allowedSideDown(part)){
					count++;
				}
			}
		}	
		return count;
  }  

  // Check if placed side down is allowed
  public static boolean allowedSideDown(Part part){
		return part.getAllowedDown().contains(part.getSideDown());
  }		
  
  // Count number of locked parts not at their position
  public static int countMisplacedParts(Layout layout){
    List<Part> parts = layout.getParts();
		int count = 0;
		for(Part part : parts) {
			if(part.getPosition() != null && part.getHint() != null){
				if(part.getHint().isMandatory()){
					count += lockedPosition(part) + lockedSurface(layout, part) + lockedSide(part) + lockedRotation(part);
				}
			}
		}	
		return count;
  }  
  
  // Check if part is places as specified in hint
  // public static boolean lockedPart(Part part){ 
	  // return lockedPosition(part) && lockedRotation(part) && lockedSide(part); 
  // }	  
	  
  // Check if part has the center position specified in hint
  public static int lockedPosition(Part part){
	  boolean sameX = part.currentCenter().getX() == part.getHint().centerPosition().getX();
	  boolean sameY = part.currentCenter().getY() == part.getHint().centerPosition().getY();
    if(sameX && sameY){
			return 0;
		}
		return 1;
  }
  
	public static int lockedSurface(Layout layout, Part part){
		if(layout.surfaceOf(part).id() == part.getHint().surfaceId()){
			return 0;
		} 
		return 1;
	}
	
	// Check if part is placed on side specified in hint
	public static int lockedSide(Part part){
	  if(part.getHint().side().isPresent() && part.getSideDown().equals(part.getHint().side().get())){
			return 0;
		}
		return 1;
	}
	
  // Check if part has the rotation specified in hint
  public static int lockedRotation(Part part){
		if(part.getHint().rotation().isPresent() && part.getRotation().equals(part.getHint().rotation().get())){
			return 0;
		}
    return 1;
  }
}
