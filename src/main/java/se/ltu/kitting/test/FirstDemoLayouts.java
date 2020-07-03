package se.ltu.kitting.test;

import java.util.*;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;

public class FirstDemoLayouts {
	
	// Need to be placed in other order than given, 160 000 points empty, should not rotate.
	// NOTE: Maybe exchange this examples, takes 1 min to solve
	public static Layout demoLayout1(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(80,40,100)));
		parts.add(new Part(2,1,new Dimensions(120,20,100)));
		parts.add(new Part(3,1,new Dimensions(40,60,100)));
		Surface surface = Surface.of(120,80,100);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Rotation needed, fit perfectly 
	public static Layout demoLayout2(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(25,100,100)));
		parts.add(new Part(2,1,new Dimensions(75,50,100)));
		parts.add(new Part(3,1,new Dimensions(25,75,100)));
		parts.add(new Part(4,1,new Dimensions(75,25,100)));
		Surface surface = Surface.of(100,100,100);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Real example. 15 parts, covers 80% of surface if not rotated.
	public static Layout demoLayout3(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(20,40,40)));
		parts.add(new Part(2,1,new Dimensions(40,40,40)));
		parts.add(new Part(3,1,new Dimensions(100,10,10)));
		parts.add(new Part(4,1,new Dimensions(20,20,20)));
		parts.add(new Part(5,1,new Dimensions(20,30,30)));
		parts.add(new Part(6,1,new Dimensions(10,50,50)));
		parts.add(new Part(7,1,new Dimensions(10,25,25)));
		parts.add(new Part(8,1,new Dimensions(10,25,25)));
		parts.add(new Part(9,1,new Dimensions(25,15,25)));
		parts.add(new Part(10,1,new Dimensions(35,15,35)));
		parts.add(new Part(11,1,new Dimensions(15,20,20)));
		parts.add(new Part(12,1,new Dimensions(15,20,20)));
		parts.add(new Part(13,1,new Dimensions(10,25,25)));
		parts.add(new Part(14,1,new Dimensions(20,15,20)));
		parts.add(new Part(15,1,new Dimensions(35,10,35)));
		Surface surface = Surface.of(120,80,100);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
}