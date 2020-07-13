package se.ltu.kitting;

import se.ltu.kitting.model.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import ch.rfin.util.Pair;
import java.util.Comparator;

public class ConstructionHeuristics {

  // Initialize all parts at left upper corner
  // XXX: Set z at first surface
  // NOTE: Better to place on bottom than minAreaSide
  public static Layout zero(Layout layout){
	List<Part> parts = layout.getParts();
	for(Part part : parts){
	  part.setPositionAndRotation(Pair.of(Dimensions.of(0,0,0),Rotation.ZERO));
      part.setSideDown(Side.bottom);
	}
	return layout;  
  }	  
  
  public static Layout minArea(Layout layout){
	List<Part> parts = layout.getParts();
    Collections.sort(parts, Comparator.comparing(Part::minAllowedArea).reversed());
	int x = 0;
	int y = 0;
	int nextRow = 0;
	for(Part part : parts) {
	  Side side = part.minAreaSide();
	  part.setSideDown(side);
	  int width = part.dimensionsOf(side)._1;
	  int height = part.dimensionsOf(side)._2;
	  if(x + Math.min(width, height) > layout.getSurface().width()){
	    y = nextRow;
		x = 0;
	  }
	  if(width <= height){
	    part.setPositionAndRotation(Pair.of(Dimensions.of(x,y,0),Rotation.ZERO));
		x += width;
		//y += height;
	  } else {
		part.setPositionAndRotation(Pair.of(Dimensions.of(x,y,0),Rotation.Z90));
		x += height;
		height = width;
		//y += width;
	  } 
	  if(y + height > y){
	    nextRow = y + height;
	  }
	}
	return layout;
  }
	  
  // XXX: Fix z for different surfaces
  public static Layout FFDH(Layout layout){
	List<Part> parts = layout.getParts();
    Collections.sort(parts, Comparator.comparing(Part::originalDepth).reversed());
	List<Part> partsLeft = new ArrayList<Part>();
	int rowHeight = 0; 
	int nextRowHeight = rowHeight;
	int x2 = 0;
    while(parts.size() > 0) {
	  int x = 0;
	  for(Part part : parts) {
        x2 = x + part.getSize().getX(); 
		if (x2 >= layout.getSurface().width()) {
		  partsLeft.add(part);	
		} else {
		  part.setPositionAndRotation(Pair.of(Dimensions.of(x,rowHeight,0),Rotation.ZERO));
		  part.setSideDown(Side.bottom);
		  x = x2 + 1;
		  if(rowHeight + part.getSize().getY() > rowHeight){
		    nextRowHeight = rowHeight + part.getSize().getY();
		  }
		}
	  }
	  parts = new ArrayList<Part>(partsLeft);
	  partsLeft.clear();
	  rowHeight = nextRowHeight + 1;
	  x2 = 0;
	  if(parts.size() != 0){
	  if(rowHeight + parts.get(0).getSize().getY() > layout.getSurface().depth()){
	    for(Part part: parts) {
		  part.setPositionAndRotation(Pair.of(Dimensions.of(0,0,0),Rotation.ZERO));
		  part.setSideDown(Side.bottom);
		}
		return layout;
	  }
	  }
	}
	return layout;
  }
  
  // XXX: Fix z for different surfaces
  public static Layout random(Layout layout) {
    List<Part> parts = layout.getParts();
	Collections.sort(parts, Comparator.comparing(Part::minAllowedArea).reversed());
	// List<Part> initializedParts = new ArrayList<Part>();
    for(Part part : parts) {
	  Random rn = new Random();
	  int x = rn.nextInt(layout.getSurface().width());
	  int y = rn.nextInt(layout.getSurface().depth());
	  // int x2 = x + part.getSize().getX();
	  // int y2 = y + part.getSize().getY();
	  // for(int i = 0; i < initializedParts.size(); i++) {
	    // if(x <= initializedParts.get(i).currentDimensions().x && 
		   // x2 >= initializedParts.get(i).getPosition().getX() && 
		   // y <= initializedParts.get(i).currentDimensions().y && 
		   // y2 >= initializedParts.get(i).getPosition().getY() || 
		   // x2 >= layout.getSurface().width() || 
		   // y2 >= layout.getSurface().depth()){
		  // x = rn.nextInt(layout.getSurface().width() - part.getSize().getX());
		  // y = rn.nextInt(layout.getSurface().depth() - part.getSize().getY());
		  // x2 = x + part.getSize().getX();
		  // y2 = y + part.getSize().getY();
		  // i = -1;
		// }
	  // }
	  // initializedParts.add(part);
	  part.setPositionAndRotation(Pair.of(Dimensions.of(x,y,0),Rotation.ZERO));
	  part.setSideDown(part.minAreaSide());
	}
    return layout;
  }
}
