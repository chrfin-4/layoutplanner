package se.ltu.kitting.test;

import java.util.*;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;

public class LayoutExamples {
	
	/* ---- Possible soltions --- */
	
	/* NOTE: Only for Z90 rotation */
	
	// Rotation needed, covers surface completely
	public static Layout layout1(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(5,10,1)));
		parts.add(new Part(2,1,new Dimensions(10,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Need to be placed in other order than given, 100 points empty, should not rotate
	public static Layout layout2(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(20,10,1)));
		parts.add(new Part(2,1,new Dimensions(30,5,1)));
		parts.add(new Part(3,1,new Dimensions(10,15,1)));
		Surface surface = Surface.of(30,20,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Easy to place, 250 points empty, small surface
	public static Layout layout3(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(15,10,1)));
		parts.add(new Part(2,1,new Dimensions(10,10,1)));
		parts.add(new Part(3,1,new Dimensions(20,5,1)));
		Surface surface = Surface.of(30,20,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// 100 squares fit perfectly
	public static Layout layout4(){
		List<Part> parts = new ArrayList<>();
		for(int i = 0; i < 100; i++){
			parts.add(new Part(i+1,1,new Dimensions(10,10,1)));
		}
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Odd shapes that just barely fit perfectly
	public static Layout layout5(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(35,30,1)));
		parts.add(new Part(2,1,new Dimensions(20,45,1)));
		parts.add(new Part(3,1,new Dimensions(45,45,1)));
		parts.add(new Part(4,1,new Dimensions(35,60,1)));
		parts.add(new Part(5,1,new Dimensions(50,45,1)));
		parts.add(new Part(6,1,new Dimensions(15,55,1)));
		parts.add(new Part(7,1,new Dimensions(85,10,1)));
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Variation of layout5, one piece removed, 2025 points empty
	public static Layout layout6(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(35,30,1)));
		parts.add(new Part(2,1,new Dimensions(20,45,1)));
		//parts.add(new Part(1,1,new Dimensions(45,45,1)));
		parts.add(new Part(3,1,new Dimensions(35,60,1)));
		parts.add(new Part(4,1,new Dimensions(50,45,1)));
		parts.add(new Part(5,1,new Dimensions(15,55,1)));
		parts.add(new Part(6,1,new Dimensions(85,10,1)));
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Rotation needed, fit perfectly 
	public static Layout layout7(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(25,100,1)));
		parts.add(new Part(2,1,new Dimensions(75,50,1)));
		parts.add(new Part(3,1,new Dimensions(25,75,1)));
		parts.add(new Part(4,1,new Dimensions(75,25,1)));
		Surface surface = Surface.of(100,100,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Small surface, no rotation needed, 15 points empty
	public static Layout layout8(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(5,5,1)));
		parts.add(new Part(2,1,new Dimensions(2,10,1)));
		parts.add(new Part(3,1,new Dimensions(8,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	/* For all rotations */
	
	// Need rotation in both X90 and Y90
	public static Layout layout9(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(10,5,10)));
		parts.add(new Part(2,1,new Dimensions(10,10,3)));
		parts.add(new Part(3,1,new Dimensions(2,10,10)));
		Surface surface = Surface.of(10,10,10);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Need rotation in double directions Z90Y90 and Z90X90
	public static Layout layout10(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(10,5,20)));
		parts.add(new Part(2,1,new Dimensions(5,30,3)));
		//parts.add(new Part(1,1,new Dimensions(2,10,10)));
		Surface surface = Surface.of(30,13,5);
		Layout layout = new Layout(surface, parts);
		return layout;
	}

	// "Real" example
	public static Layout layout11(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(200,400,100)));
		parts.add(new Part(2,1,new Dimensions(400,400,400)));
		parts.add(new Part(3,1,new Dimensions(1000,100,100)));
		parts.add(new Part(4,1,new Dimensions(200,200,200)));
		parts.add(new Part(5,1,new Dimensions(200,300,200)));
		parts.add(new Part(6,1,new Dimensions(100,500,300)));
		parts.add(new Part(7,1,new Dimensions(100,250,100)));
		parts.add(new Part(8,1,new Dimensions(100,250,100)));
		parts.add(new Part(9,1,new Dimensions(250,150,200)));
		parts.add(new Part(10,1,new Dimensions(350,150,200)));
		parts.add(new Part(11,1,new Dimensions(150,200,200)));
		parts.add(new Part(12,1,new Dimensions(150,200,200)));
		parts.add(new Part(13,1,new Dimensions(100,250,200)));
		parts.add(new Part(14,1,new Dimensions(200,150,100)));
		parts.add(new Part(15,1,new Dimensions(350,100,100)));
		Surface surface = Surface.of(1200,800,1000);
		Layout layout = new Layout(surface, parts);
		return layout;
	}

	// "Real" example
	public static Layout layout12(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(200,400,400)));
		parts.add(new Part(2,1,new Dimensions(400,400,400)));
		parts.add(new Part(3,1,new Dimensions(1000,100,1000)));
		parts.add(new Part(4,1,new Dimensions(200,200,200)));
		parts.add(new Part(5,1,new Dimensions(200,300,300)));
		parts.add(new Part(6,1,new Dimensions(100,500,500)));
		parts.add(new Part(7,1,new Dimensions(100,250,250)));
		parts.add(new Part(8,1,new Dimensions(100,250,250)));
		parts.add(new Part(9,1,new Dimensions(250,150,250)));
		parts.add(new Part(10,1,new Dimensions(350,150,350)));
		parts.add(new Part(11,1,new Dimensions(150,200,200)));
		parts.add(new Part(12,1,new Dimensions(150,200,200)));
		parts.add(new Part(13,1,new Dimensions(100,250,250)));
		parts.add(new Part(14,1,new Dimensions(200,150,200)));
		parts.add(new Part(15,1,new Dimensions(350,100,350)));
		Surface surface = Surface.of(1200,800,1000);
		Layout layout = new Layout(surface, parts);
		return layout;
	}

  public static Layout apiExample1() {
    Surface surface = Surface.of(1000,600,500);
    List<Part> parts = new ArrayList<>();
    Part part;
    // "TOWING BRACE", func = "8922-01", weight = 5.0, req.cap = [cap1, cap2], hint = {(300,0,0), (45,0,0), 10}
    part = new Part(1, "21313876", Dimensions.of(200,500,700));
    part.setAllowedDown(Set.of(Side.top, Side.bottom, Side.left, Side.right));
    part.setPreferredDown(Side.top);
    parts.add(part);
    // "WARNING TRIANGLE", func = "8962-01", weight = 0.5, req.cap = [cap1], quantity = 2
    part = new Part(2, "23374865", Dimensions.of(200,500,700));
    part.setAllowedDown(Set.of(Side.left, Side.right));
    part.setPreferredDown(Side.left);
    parts.add(part);
    // XXX: id=2 ???
    part = new Part(2, "23374865", Dimensions.of(200,500,700));
    part.setAllowedDown(Set.of(Side.left, Side.right));
    part.setPreferredDown(Side.left);
    parts.add(part);
    // "INTERIOR LAMP", func = "354-01", weight = 0.3
    part = new Part(3, "84814790", Dimensions.of(100,300,20));
    part.setAllowedDown(Set.of(Side.top));
    part.setPreferredDown(Side.top);
    parts.add(part);
    return new Layout(surface, parts);
  }

	/* ----  Impossible solutions ---- */ 
	
	// Overlap completely
	public static Layout unsolvableLayout1(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(10,10,1)));
		parts.add(new Part(2,1,new Dimensions(10,10,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
	// Fits when looking at area but one part is to wide
	public static Layout unsolvableLayout2(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(15,5,1)));
		parts.add(new Part(2,1,new Dimensions(5,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
}
