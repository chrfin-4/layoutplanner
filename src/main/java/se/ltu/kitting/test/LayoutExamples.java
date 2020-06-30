package se.ltu.kitting.test;

import java.util.*;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;

public class LayoutExamples {
	
	/* ---- Possible soltions --- */
	
	/* NOTE: Only for Z90 rotation */
	
	// Rotation needed, covers surface completely
	public static Layout layout1(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(5,10,1)));
		parts.add(new Part(1,1,new Dimensions(10,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Need to be placed in other order than given, 100 points empty, should not rotate
	public static Layout layout2(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(20,10,1)));
		parts.add(new Part(1,1,new Dimensions(30,5,1)));
		parts.add(new Part(1,1,new Dimensions(10,15,1)));
		Surface surface = Surface.of(30,20,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Easy to place, 250 points empty, small surface
	public static Layout layout3(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(15,10,1)));
		parts.add(new Part(1,1,new Dimensions(10,10,1)));
		parts.add(new Part(1,1,new Dimensions(20,5,1)));
		Surface surface = Surface.of(30,20,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// 100 squares fit perfectly
	public static Layout layout4(){
		List<Part> parts = new ArrayList<>();
		for(int i = 0; i < 100; i++){
			parts.add(new Part(1,1,new Dimensions(10,10,1)));
		}
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Odd shapes that just barely fit perfectly
	public static Layout layout5(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(35,30,1)));
		parts.add(new Part(1,1,new Dimensions(20,45,1)));
		parts.add(new Part(1,1,new Dimensions(45,45,1)));
		parts.add(new Part(1,1,new Dimensions(35,60,1)));
		parts.add(new Part(1,1,new Dimensions(50,45,1)));
		parts.add(new Part(1,1,new Dimensions(15,55,1)));
		parts.add(new Part(1,1,new Dimensions(85,10,1)));
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Variation of layout5, one piece removed, 2025 points empty
	public static Layout layout6(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(35,30,1)));
		parts.add(new Part(1,1,new Dimensions(20,45,1)));
		//parts.add(new Part(1,1,new Dimensions(45,45,1)));
		parts.add(new Part(1,1,new Dimensions(35,60,1)));
		parts.add(new Part(1,1,new Dimensions(50,45,1)));
		parts.add(new Part(1,1,new Dimensions(15,55,1)));
		parts.add(new Part(1,1,new Dimensions(85,10,1)));
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Rotation needed, fit perfectly 
	public static Layout layout7(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(25,100,1)));
		parts.add(new Part(1,1,new Dimensions(75,50,1)));
		parts.add(new Part(1,1,new Dimensions(25,75,1)));
		parts.add(new Part(1,1,new Dimensions(75,25,1)));
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Small surface, no rotation needed, 15 points empty
	public static Layout layout8(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(5,5,1)));
		parts.add(new Part(1,1,new Dimensions(2,10,1)));
		parts.add(new Part(1,1,new Dimensions(8,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	/* For all rotations */
	
	// Need rotation in both X90 and Y90
	public static Layout layout9(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(10,5,10)));
		parts.add(new Part(1,1,new Dimensions(10,10,3)));
		parts.add(new Part(1,1,new Dimensions(2,10,10)));
		Surface surface = Surface.of(10,10,10);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Need rotation in double directions Z90Y90 and Z90X90
	public static Layout layout10(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(10,5,20)));
		parts.add(new Part(1,1,new Dimensions(5,30,3)));
		//parts.add(new Part(1,1,new Dimensions(2,10,10)));
		Surface surface = Surface.of(30,13,5);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	/* ----  Impossible solutions ---- */ 
	
	// Overlap completely
	public static Layout unsolvableLayout1(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(10,10,1)));
		parts.add(new Part(1,1,new Dimensions(10,10,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Fits when looking at area but one part is to wide
	public static Layout unsolvableLayout2(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(15,5,1)));
		parts.add(new Part(1,1,new Dimensions(5,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
}