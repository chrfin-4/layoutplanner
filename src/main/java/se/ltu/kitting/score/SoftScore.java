package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Part;
import java.util.List;

/**
 * Calculates the soft score for a solution. 
 * Desirable score is 0 which guarantees that:
 * Parts are placed on the preferred side and that
 * parts with a mandatory hint are on the desired position.
 *
 * Guides the solution toward the right position (if mandatory).
 */
public class SoftScore {

  // Return negative score for parts not on preferred side and not on mandatory postion
  public static int getSoftScore(Layout layout) {
    int notPreferred = countNotPreferredSides(layout);
	  int mandatory = mandatory(layout);
    return -(notPreferred + mandatory);
  }

  // Count parts placed on a none preferred side
  public static int countNotPreferredSides(Layout layout){
    List<Part> parts = layout.getParts();
		int count = 0;
		for(Part part : parts) {
			if(part.getPosition() == null || part.getSideDown() == null){
				continue;
			}	
			if(!preferredSideDown(part)){
				count++;
			}
		}	
		return count;
  }  

  // Check if placed side down is preferred
  public static boolean preferredSideDown(Part part){
		if(part.getPreferredDown() == null){
			return true;
		}	
		// If a side is given as a mandatory hint, ignore preferred side
		if(part.getHint() != null && part.getHint().isMandatory() && part.getHint().side().isPresent()){
			return true;
		}
		return part.getPreferredDown() == part.getSideDown();
  }		
 

  public static int mandatory(Layout layout){
    List<Part> parts = layout.getParts();
		int total = 0;
		for(Part part : parts) {
			if(part.getPosition() != null && part.getHint() != null){
				if(part.getHint().isMandatory()){
					total += distanceToPosition(part) + wrongSide(part) + wrongRotation(part);
				}
			}
		}	
		return total;
  }  
  
	// Distance between parts current postions and mandatory position
  public static int distanceToPosition(Part part){ 
		int x = Math.abs(part.currentCenter().getX() - part.getHint().centerPosition().getX());	 
    int y = Math.abs(part.currentCenter().getY() - part.getHint().centerPosition().getY());	 
    return x + y; 	
  }	
	
	public static int wrongSide(Part part){
	  if(part.getHint().side().isPresent() && part.getSideDown().equals(part.getHint().side().get())){
			return 0;
		}
		return 1;
	}
	
  public static int wrongRotation(Part part){
		if(part.getHint().rotation().isPresent() && part.getRotation().equals(part.getHint().rotation().get())){
			return 0;
		}
    return 1;
  }
	  
}
