package se.ltu.kitting.test;

import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import java.util.Set;

/**
 * Recommended to run layout through this class before solving it
 * Can declare layouts unsolvable (by throwing exceptions)
 * Can edit out impossible layouthints to optimize the solving process
 */
public class Preprocess {

  public static void preprocess(Layout layout) throws Exception {
	/* --- Unsolvable checks --- */
    minAreaCheck(layout);

	/* --- Modifying layout --- */
	removeImpossibleSides(layout);
  }

  // Check if total area of part is greater than total area of surfaces
  public static void minAreaCheck(Layout layout) throws Exception {
	int totalPartArea = 0;
	int totalSurfaceArea = 0;
    for(Part part : layout.getParts()){
	  totalPartArea += part.minAllowedArea();
	}
	for(Surface surface : layout.getWagon().surfaces()){
	  totalSurfaceArea += surface.width() * surface.depth();
    }
	if(totalPartArea > totalSurfaceArea){
	  throw new Exception("Unsolvable - area of parts greater than area of surfaces");
	}
  }
  
  // Check if several parts have mandatory layout hints with same position
  public static void mandatoryCollision(Layout layout) throws Exception {
	Set<Dimensions> positions = null;
	for(Part part : layout.getParts()){
	  if(part.getHint().isMandatory()){
		
		if(positions.contains(part.getHint().centerPosition())){
		  throw new Exception("Unsolvable - multiple madatory hints require same position");
		}
        positions.add(part.getHint().centerPosition());
      }
    }	  
  }
  
  // Removes impossible sides from allowedDown if the height is greater than surface height
  // Only removes side if it can not be placed down on any surface.
  public static void removeImpossibleSides(Layout layout) throws Exception{
	for(Part part : layout.getParts()){
	  Set<Side> allowedSides = part.getAllowedDown();
	  int partX = part.getSize().getX();
	  int partY = part.getSize().getY();
	  int partZ = part.getSize().getZ();
	  int surfaceX = 0;
	  int surfaceY = 0;
	  int surfaceZ = 0;
	  for(Surface surface : layout.getWagon().surfaces()){
	    surfaceX = Math.max(surfaceX, surface.width());
		surfaceY = Math.max(surfaceY, surface.depth());
		surfaceZ = Math.max(surfaceZ, surface.height());
	  }
      if(partZ > surfaceZ && (allowedSides.contains(Side.bottom) || allowedSides.contains(Side.top))){
		allowedSides.remove(Side.bottom);
		allowedSides.remove(Side.top);
	  }
	  if(partY > surfaceZ && (allowedSides.contains(Side.back) || allowedSides.contains(Side.front))){
        allowedSides.remove(Side.bottom);
		allowedSides.remove(Side.top);
      }
	  if(partX > surfaceZ && (allowedSides.contains(Side.left) || allowedSides.contains(Side.right))){
        allowedSides.remove(Side.left);
		allowedSides.remove(Side.right);
      }
	  // Removes preferred if removed from allowedSides.
	  if(!allowedSides.contains(part.getPreferredDown())){
	    part.setPreferredDown(null);
	  }
	  if(allowedSides.isEmpty()){
	    throw new Exception("Unsolvable - no sides are allowed down");
	  }
	  part.setAllowedDown(allowedSides);
    }
  }
}
