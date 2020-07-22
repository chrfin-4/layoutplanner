package se.ltu.kitting.test;

import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.api.Message;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import ch.rfin.util.Pair;

/**
 * Recommended to run layout through this class before solving it
 * Can declare layouts unsolvable 
 * Can edit out impossible layouthints to optimize the solving process
 */
public class Preprocess {

  public static Pair<Optional<List<Message>>, Optional<Map<Integer,List<Message>>>> preprocess(Layout layout) {
	List<Message> generalMessages = new ArrayList<>();
	List<Map<Integer,List<Message>>> maps = new ArrayList<>();
	Map<Integer, List<Message>> partMessages = new HashMap<>();
	
	/* --- Unsolvable checks --- */
    minAreaCheck(layout).ifPresent(generalMessages::add);
	volumeCheck(layout).ifPresent(generalMessages::add);
	
	partTooBig(layout).ifPresent(maps::add);
	mandatoryHintCollision(layout).ifPresent(maps::add);
	
	/* --- Modifying layout --- */
	removeImpossibleSides(layout).ifPresent(maps::add);

    for(Part part : layout.getParts()){
	  partMessages.put(part.getId(), new ArrayList());
	  for(Map<Integer,List<Message>> map : maps){
	    partMessages.get(part.getId()).addAll(map.getOrDefault(part.getId(), List.of()));
	  }
	}
    return Pair.of(Optional.of(generalMessages), Optional.of(partMessages));
  }

  // Simple check so volume of parts are less than volume of surfaces
  // Alternativ: use wagon but dimensions not required
  public static Optional<Message> volumeCheck(Layout layout) {
	Dimensions wagonDimensions = layout.getWagon().dimensions();
	int totalSurfaceVolume = 0;
	int totalPartVolume = 0;
	for(Surface surface : layout.getWagon().surfaces()){
	  totalSurfaceVolume += surface.width() * surface.depth() * surface.height();
    }
	for(Part part : layout.getParts()){
	  totalPartVolume += part.volume();
	}  
	if(totalPartVolume > totalSurfaceVolume){
      return Optional.of(Message.error("Unsolvable - volume of parts greater than volume of surfaces"));
	}
	return Optional.empty();
  }

  // Check if total area of part is greater than total area of surfaces
  // Margin not considered
  public static Optional<Message> minAreaCheck(Layout layout) {
	int totalPartArea = 0;
	int totalSurfaceArea = 0;
    for(Part part : layout.getParts()){
	  totalPartArea += part.minAllowedArea();
	}
	for(Surface surface : layout.getWagon().surfaces()){
	  totalSurfaceArea += surface.width() * surface.depth();
    }
	if(totalPartArea > totalSurfaceArea){
      return Optional.of(Message.error("Unsolvable - area of parts greater than area of surfaces"));
	}
	return Optional.empty();
  }
  
  // Check if a part is too big to fit on wagon
  // Not considering allowed sides
  public static Optional<Map<Integer,List<Message>>> partTooBig(Layout layout) {
	Map<Integer,List<Message>> messages = new HashMap<>();
	int surfaceX = 0;
	int surfaceY = 0;
	int surfaceZ = 0;
	for(Surface surface : layout.getWagon().surfaces()){
	  surfaceX = Math.max(surfaceX, surface.width());
	  surfaceY = Math.max(surfaceY, surface.depth());
	  surfaceZ = Math.max(surfaceZ, surface.height());
	}
	for(Part part : layout.getParts()){
	  int width = part.width();
	  int height = part.height();
	  int depth = part.depth();
	  if(Math.max(width, Math.max(height, depth)) > Math.max(surfaceX, Math.max(surfaceY, surfaceZ))){
		messages.put(part.getId(), List.of(Message.error("Part does not fit on any surface")));
	  } 
    }
	if(messages.isEmpty()){
	  return Optional.empty();
	}
    return Optional.of(messages);
  }
  
  // Check if several parts have mandatory layout hints that collide considering position
  // No check for allowed sides down nor rotation
  public static Optional<Map<Integer,List<Message>>> mandatoryHintCollision(Layout layout) {
	Map<Integer,List<Message>> messages = new HashMap<>();
	Set<Dimensions> positions = new HashSet<>();
	for(Part part : layout.getParts()){
	  if(part.getHint() != null){
	    if(part.getHint().isMandatory()){
		  int minLength = Math.min(part.width(), Math.min(part.height(), part.depth()));
		  Dimensions centerPosition = part.getHint().centerPosition();
		  int z = centerPosition.getZ();
		  // Add all positions around center that part definitely covers
		  for(int x = centerPosition.getX() - minLength/2; x <= centerPosition.getX() + minLength/2; x++){
		    for(int y = centerPosition.getY() - minLength/2; y <= centerPosition.getY() + minLength/2; y++){
			  if(positions.contains(Dimensions.of(x,y,z))){
		        messages.put(part.getId(), List.of(Message.error("multiple mandatory hints require same positions")));
		      }		    
			  positions.add(Dimensions.of(x,y,z));
	        }
		  }
        }
	  }
    }
	if(messages.isEmpty()){
	  return Optional.empty();
	}
    return Optional.of(messages);	
  }
  
  // Removes sides from allowedDown if a length is greater than the side lengths of surface
  // Only removes side if it can not be placed down on any surface.
  // Only works if surfaces has a height
  // partTooBig() not needed together with this method
  public static Optional<Map<Integer,List<Message>>> removeImpossibleSides(Layout layout) {
	Map<Integer,List<Message>> messages = new HashMap<>();
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
      if((partZ > surfaceZ || partX > Math.max(surfaceX, surfaceY) || partY > Math.max(surfaceX, surfaceY))
	     && (allowedSides.contains(Side.bottom) || allowedSides.contains(Side.top))){
		allowedSides.remove(Side.bottom);
		allowedSides.remove(Side.top);
	  }
	  if((partY > surfaceZ || partX > Math.max(surfaceX, surfaceY) || partZ > Math.max(surfaceX, surfaceY))
	     && (allowedSides.contains(Side.back) || allowedSides.contains(Side.front))){
        allowedSides.remove(Side.back);
		allowedSides.remove(Side.front);
      }
	  if((partX > surfaceZ || partY > Math.max(surfaceX, surfaceY) || partZ > Math.max(surfaceX, surfaceY)) 
		 && (allowedSides.contains(Side.left) || allowedSides.contains(Side.right))){
        allowedSides.remove(Side.left);
		allowedSides.remove(Side.right);
      }
	  // Removes preferred if removed from allowedSides.
	  if(!allowedSides.contains(part.getPreferredDown())){
	    part.setPreferredDown(null);
	  }
	  if(allowedSides.isEmpty()){
		messages.put(part.getId(), List.of(Message.error("No side can be placed down")));
	  }
	  part.setAllowedDown(allowedSides);
    }
	if(messages.isEmpty()){
	  return Optional.empty();
	}
    return Optional.of(messages);
  }
}
