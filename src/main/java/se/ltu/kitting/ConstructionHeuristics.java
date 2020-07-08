package se.ltu.kitting;

import se.ltu.kitting.model.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import ch.rfin.util.Pair;

public class ConstructionHeuristics {

  public static Layout FFDH(Layout layout){
	List<Part> parts = layout.getParts();
    Collections.sort(parts, new Part.CompareHeight());
	List<Part> partsLeft = new ArrayList<Part>();
	int rowHeight = 0; 
	int nextRowHeight = rowHeight;
	int x2 = 0;
    while(parts.size() > 0) {
	  int x = 0;
	  for(Part part : parts) {
        x2 = x + part.getSize().getX(); 
		if(rowHeight + part.getSize().getY() > rowHeight){
		  nextRowHeight = rowHeight + part.getSize().getY();
		}
		if (x2 >= layout.getSurface().width()) {
		  partsLeft.add(part);	
		} else {
		  part.setPositionAndRotation(Pair.of(Dimensions.of(x,rowHeight,0),Rotation.ZERO));
		  part.setSideDown(Side.bottom);
		  x = x2 + 1;
		}
	  }
	  parts = new ArrayList<Part>(partsLeft);
	  partsLeft.clear();
	  rowHeight = nextRowHeight + 1;
	  x2 = 0;
	}
	return layout;
  }
  
  
  public static Layout random(Layout layout) {
    List<Part> parts = layout.getParts();
	Collections.sort(parts, new Part.CompareHeight());
	List<Part> initializedParts = new ArrayList<Part>();
    for(Part part : parts) {
	  Random rn = new Random();
	  int x = rn.nextInt(layout.getSurface().width() - part.getSize().getX());
	  int y = rn.nextInt(layout.getSurface().depth() - part.getSize().getY());
	  int x2 = x + part.getSize().getX();
	  int y2 = y + part.getSize().getY();
	  for(int i = 0; i < initializedParts.size(); i++) {
	    if(x <= xmax.get(i) && 
		   x2 >= xmin.get(i) && 
		   y <= ymax.get(i) && 
		   y2 >= ymin.get(i) || 
		   x2 >= layout.getSurface().width() || 
		   y2 >= layout.getSurface().depth()){
		  x = rn.nextInt(layout.getSurface().width() - part.getSize().getX());
		  y = rn.nextInt(layout.getSurface().depth() - part.getSize().getY());
		  x2 = x + part.getSize().getX();
		  y2 = y + part.getSize().getY();
		  i = -1;
		}
	  }
	  initializedParts.add(part);
	  part.setPositionAndRotation(Pair.of(Dimensions.of(x,y,0),Rotation.ZERO));
	  part.setSideDown(Side.bottom);
	}
	return layout;
  }
}
