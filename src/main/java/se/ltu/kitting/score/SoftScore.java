package se.ltu.kitting.score;

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
	  int totalDistance = totalDistance(layout);
    return -(notPreferred + totalDistance);
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
  public static boolean preferredSideDown(Part part) {
    if (part.getPreferredDown() == null){
      return true;  // Don't have a preference to violate.
    }
    // If a side is given as a hint, ignore preferred side.
    if (part.getHint() != null && part.getHint().side().isPresent()) {
      return true;
    }
    return part.getPreferredDown() == part.getSideDown();
  }


  public static int totalDistance(Layout layout){
    List<Part> parts = layout.getParts();
		int totalDistance = 0;
		for(Part part : parts) {
			if(part.getPosition() != null && part.getHint() != null){
        // TODO: only count this if mandatory?
				if(part.getHint().isMandatory()){
					totalDistance += distanceToPosition(part);
				}
			}
		}
		return totalDistance;
  }

  // Distance between part;s current postion and mandatory position.
  public static int distanceToPosition(Part part) {
    int x = Math.abs(part.currentCenter().getX() - part.getHint().centerPosition().getX());
    int y = Math.abs(part.currentCenter().getY() - part.getHint().centerPosition().getY());
    return x + y;
  }

}
