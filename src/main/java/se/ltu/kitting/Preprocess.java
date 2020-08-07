package se.ltu.kitting;

import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.api.Message;
import se.ltu.kitting.api.Messages;
import se.ltu.kitting.api.PlanningRequest;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import ch.rfin.util.Pair;

/**
 * Recommended to run layout through this class before solving it to add error messages
 * Can declare layouts unsolvable
 * Can edit out impossible layouthints to optimize the solving process
 */
public class Preprocess {

  public static PlanningRequest preprocess(PlanningRequest request, Layout layout) {
    List<Message> globalMessages = new ArrayList<>();
    List<Map<Integer,List<Message>>> maps = new ArrayList<>();
    Map<Integer, List<Message>> partMessages = new HashMap<>();

		minAreaCheck(request,layout);
    volumeCheck(request,layout);
    /* --- Part checks --- */
    partTooBig(request,layout);
    /* --- Modifying layout --- */
    applyHints(layout);
    removeImpossibleSides(request,layout);
		return request;
  }

  private static void applyHints(Layout layout) {
    for (Part part : layout.getParts()) {
      if (part.getHint() != null) {
        part.getHint().rotation().ifPresent(part::setRotation);
        part.getHint().side().ifPresent(part::setSideDown);
        Dimensions pos = part.getHint().centerPosition();
        if (part.getRotation() != null && part.getSideDown() != null) {
          pos = Part.centerToCorner(pos, part.currentDimensions());
        }
        pos = Dimensions.of(pos.x, pos.y, part.getHint().surfaceId());
        part.setPosition(pos);
      }
    }
  }

  // Divide pair and add to right list
  private static void combineMessages(List<Message> globalMessages, List<Map<Integer,List<Message>>> maps, Pair<Optional<Message>, Optional<Map<Integer,List<Message>>>> ... pairs){
    for(Pair<Optional<Message>, Optional<Map<Integer,List<Message>>>> pair : pairs){
      pair._1.ifPresent(globalMessages::add);
      pair._2.ifPresent(maps::add);
    }
  }

  // Simple check so volume of parts are less than volume of surfaces
  // Alternativ: use wagon but dimensions are not required
  public static PlanningRequest volumeCheck(PlanningRequest request, Layout layout) {
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
			request.messages().addMessage(Message.error("Unsolvable - volume of parts greater than volume of surfaces"));
    }
    return request;
  }

  // Check if total area of part is greater than total area of surfaces
  // Margin not considered
  public static PlanningRequest minAreaCheck(PlanningRequest request, Layout layout) {
    int totalPartArea = 0;
    int totalSurfaceArea = 0;
    for(Part part : layout.getParts()){
      totalPartArea += part.minAllowedArea();
    }
    for(Surface surface : layout.getWagon().surfaces()){
      totalSurfaceArea += surface.width() * surface.depth();
    }
    if(totalPartArea > totalSurfaceArea){
			request.messages().addMessage(Message.error("Unsolvable - area of parts greater than area of surfaces"));
    }
    return request;
  }

  // Check if a part is too big to fit on wagon
  // Not considering allowed sides
  public static PlanningRequest partTooBig(PlanningRequest request, Layout layout) {
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
				request.messages().addMessage(part, Message.error("Part does not fit on any surface"));
      }
    }
    if(!messages.isEmpty()){
      request.messages().addMessage(Message.error("Unsolvable - part do not fit on surfaces"));
    }
		return request;
  }

  // Removes sides from allowedDown if a length is greater than the side lengths of surface
  // Only removes side if it can not be placed down on any surface.
  // Only works if surfaces has a height
  // partTooBig() not needed together with this method
  public static PlanningRequest removeImpossibleSides(PlanningRequest request, Layout layout) {
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
      if((partZ > surfaceZ || partX > Math.max(surfaceX, surfaceY) || partY > Math.max(surfaceX, surfaceY))){
        allowedSides.remove(Side.bottom);
        allowedSides.remove(Side.top);
      }
      if((partY > surfaceZ || partX > Math.max(surfaceX, surfaceY) || partZ > Math.max(surfaceX, surfaceY))){
        allowedSides.remove(Side.back);
        allowedSides.remove(Side.front);
      }
      if((partX > surfaceZ || partY > Math.max(surfaceX, surfaceY) || partZ > Math.max(surfaceX, surfaceY))){
        allowedSides.remove(Side.left);
        allowedSides.remove(Side.right);
      }
			// Warning if layout hint has other side down than preferred
			if(part.getHint() != null && part.getHint().side().isPresent() && !part.getHint().side().get().equals(part.getPreferredDown())){
				request.messages().addMessage(part, Message.warn("Preferred side does not match side in layout hint."));
			}
      // Removes preferred if removed from allowedSides.
      if(!allowedSides.contains(part.getPreferredDown())){
        part.setPreferredDown(null);
      }
      if(allowedSides.isEmpty()){
				request.messages().addMessage(part, Message.error("No side can be placed down."));
      }
      part.setAllowedDown(allowedSides);
    }
    if(!messages.isEmpty()){
			request.messages().addMessage(Message.error("Unsolvable - no allowed side to place down for part."));
    }
		return request;
  }
}
