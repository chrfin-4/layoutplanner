package se.ltu.kitting.score;

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

  // TODO: first filter on fully initialized?
  // Counts the number of parts that overlap eachother
  // NOTE: Counts same overlap multiple times
  public static int countOverlappingParts(Layout layout) {
    List<Part> parts = layout.getParts();
    int count = 0;
    for (int i = 0; i < parts.size() - 1; i++) {
      final Part p1 = parts.get(i);
      if (!p1.fullyInitialized()) {
        continue;
      }
      for(int j = i + 1; j < parts.size(); j++) {
        final Part p2 = parts.get(j);
        if (p2.fullyInitialized()) {
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
    // Parts do not overlap if they are on different surfaces
    if (p1.getPosition().z != p2.getPosition().z) {
      return false;
    }
    Pair<Dimensions,Dimensions> currentRegionP1 = p1.currentRegion();
    Pair<Dimensions,Dimensions> currentRegionP2 = p2.currentRegion();
    int margin = Math.max(p1.getMargin(), p2.getMargin());
    Dimensions startPositionP1 = currentRegionP1._1;
    Dimensions endPositionP1 = currentRegionP1._2;
    Dimensions startPositionP2 = currentRegionP2._1;
    Dimensions endPositionP2 = currentRegionP2._2;
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
  public static int countPartsOutside(Layout layout) {
    List<Part> parts = layout.getParts();
    int count = 0;
    for (Part part : parts) {
      if (part.fullyInitialized()) {
        if (partOutside(part, layout.surfaceOf(part))) {
          count++;
        }
      }
    }
    return count;
  }

  // Checks if part is outside of surface.
  public static boolean partOutside(Part part, Surface surface) {
    var tmp = part.getPosition();
    Dimensions partEnd = Dimensions.of(tmp.x, tmp.y, 0).plus(part.currentDimensions()); // Part position is relative to surface. So z = 0.
    Dimensions surfaceEnd = surface.dimensions;
    return partOutside(partEnd, surfaceEnd);
  }

  // Note: given that positions are only generated from (0,0) UP TO surface
  // dimensions, there is no point in checking the opposite case (where the
  // part is outside top/left).
  public static boolean partOutside(Dimensions partEnd, Dimensions surfaceEnd) {
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

  // Count number of parts that don't match their layout hint.
  // (All violations count equally, whether mandatory or not.)
  public static int countMisplacedParts(Layout layout) {
    List<Part> parts = layout.getParts();
    int count = 0;
    for (Part part : parts) {
      if (part.hasMandatoryHint() && !partMatchesHint(layout, part)) {
        count++;
      }
    }
    return count;
  }

  // Check whether part matches the layout hint.
  public static boolean partMatchesHint(Layout layout, Part part) {
    return positionMatchesHint(part) &&
      surfaceMatchesHint(layout, part) &&
      sideMatchesHint(part) &&
      rotationMatchesHint(part);
  }

  // Check whether part has the center position specified in hint.
  public static boolean positionMatchesHint(Part part) {
    assert part.getHint().centerPosition() != null;
    if (part.getPosition() == null || part.getRotation() == null || part.getSideDown() == null) {
      return false;
    }
    boolean sameX = part.currentCenter().getX() == part.getHint().centerPosition().getX();
    boolean sameY = part.currentCenter().getY() == part.getHint().centerPosition().getY();
    return sameX && sameY;
  }

  // Check whether part is placed on the surface specified in hint.
  public static boolean surfaceMatchesHint(Layout layout, Part part) {
    return layout.surfaceOf(part).id() == part.getHint().surfaceId();
  }

  // Check whether part is placed on side specified in hint.
  public static boolean sideMatchesHint(Part part) {
    return part.getHint().side().map(s -> part.getSideDown() == s).orElse(true);
  }

  // Check whether part has the rotation specified in hint.
  public static boolean rotationMatchesHint(Part part) {
    return part.getHint().rotation().map(r -> part.getRotation() == r).orElse(true);
  }

  /**
   * Compute the area of overlap between two parts.
   * Takes margin into account.
   */
  public static int overlappingPartArea(Part p1, Part p2) {
    // Parts do not overlap if they are on different surfaces
    if (p1.getPosition().z != p2.getPosition().z) {
      return 0;
    }
    Pair<Dimensions,Dimensions> currentRegionP1 = p1.currentRegion();
    Pair<Dimensions,Dimensions> currentRegionP2 = p2.currentRegion();
    int margin = Math.max(p1.getMargin(), p2.getMargin());
    Dimensions startPositionP1 = currentRegionP1._1;
    Dimensions endPositionP1 = currentRegionP1._2;
    Dimensions startPositionP2 = currentRegionP2._1;
    Dimensions endPositionP2 = currentRegionP2._2;
    int rect1xLeft = startPositionP1.getX() - margin;
    int rect1xRight = endPositionP1.getX() + margin;
    int rect1yBack = startPositionP1.getY() - margin;
    int rect1yFront = endPositionP1.getY() + margin;
    int rect2xLeft = startPositionP2.getX();
    int rect2xRight = endPositionP2.getX();
    int rect2yBack = startPositionP2.getY();
    int rect2yFront = endPositionP2.getY();

    int left = Math.max(rect1xLeft, rect2xLeft);
    int right = Math.min(rect1xRight, rect2xRight);
    int back = Math.max(rect1yBack, rect2yBack);
    int front = Math.min(rect1yFront, rect2yFront);
    int width = right - left;
    int depth = front - back;
    return width*depth;
  }

  public static int partOutsideArea(Part part, Surface surface) {
    var tmp = part.getPosition();
    // Part position is relative to surface. So z = 0.
    Dimensions partEnd = Dimensions.of(tmp.x, tmp.y, 0)
      .plus(part.currentDimensions());
    Dimensions surfaceEnd = surface.dimensions;
    return areaOutside(partEnd, surfaceEnd);
  }

  // Note: given that positions are only generated from (0,0) UP TO surface
  // dimensions, there is no point in checking the opposite case (where the
  // part is outside top/left).
  public static int areaOutside(Dimensions partEnd, Dimensions surfaceEnd) {
    int depth = Math.min(0, partEnd.y - surfaceEnd.y);
    int width = Math.min(0, partEnd.x - surfaceEnd.x);
    return depth * width;
  }

}
