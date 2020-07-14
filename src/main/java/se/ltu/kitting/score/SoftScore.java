package se.ltu.kitting.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import java.util.List;
import ch.rfin.util.Pair;

public class SoftScore {

  // Return negative score for parts not on preferred side
  public static int getSoftScore(Layout layout) {
    int notPreferred = countNotPreferredSides(layout);
	int totalDistance = totalDistancePreferredPosition(layout);
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
  public static boolean preferredSideDown(Part part){
	if(part.getPreferredDown() == null){
	  return true;
	}
	return part.getPreferredDown() == part.getSideDown();
  }		
 
  public static int totalDistancePreferredPosition(Layout layout){
    List<Part> parts = layout.getParts();
	int totalDistance = 0;
	for(Part part : parts) {
      if(part.getPosition() != null && part.getHint() != null){
		totalDistance += distancePreferredPosition(part);
	  }
	}	
	return totalDistance;
  }  
  
  public static int distancePreferredPosition(Part part){ 
	int x = Math.abs(part.currentCenter().getX() - part.getHint().centerPosition().getX());	 
    int y = Math.abs(part.currentCenter().getY() - part.getHint().centerPosition().getY());	 
    return x + y; 	
  }	
}
