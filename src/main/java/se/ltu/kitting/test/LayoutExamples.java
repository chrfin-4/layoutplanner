package se.ltu.kitting.test;

import java.util.*;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.model.Wagon;
import ch.rfin.util.Pair;

import static se.ltu.kitting.model.Dimensions.dimensions;

public class LayoutExamples {

	private static List<Pair<String,Layout>> layouts = List.of(Pair.of("Layout 0: Easy", layout0()),
					Pair.of("Layout 1: Easy w. rotation", layout1()),
					Pair.of("Layout 2: Easy spec. order", layout2()),
					Pair.of("Layout 3: Easy w. free space", layout3()),
					Pair.of("Layout 4: 100 squares", layout4()),
					Pair.of("Layout 5: Hard, no free space", layout5()),
					Pair.of("Layout 6: Layout 5 w. one piece removed", layout6()),
					Pair.of("Layout 7: Medium, rotation needed", layout7()),
					Pair.of("Layout 8: Easy, small surface", layout8()),
					Pair.of("Layout 9: Easy, rotation multiple directions", layout9()),
					Pair.of("Layout 10: Easy, rotation multiple directions", layout10()),
					Pair.of("Layout 11: 'Real example'", layout11()),
					Pair.of("Layout 12: Layout 11 w. less free space", layout12()));

	public static List<Pair<String,Layout>> getAll() {
		return layouts;
	}
	
	public static String layoutDescription(int index){
		if(index >= layouts.size()){
			throw new IndexOutOfBoundsException("Layout " + index + " does not exist");
		}
		return layouts.get(index)._1;
	}
	
	public static Layout layout(int index){
		if(index >= layouts.size()){
			throw new IndexOutOfBoundsException("Layout " + index + " does not exist");
		}
		return layouts.get(index)._2;
	}
	
	// public static List<Pair<String, Layout>> getAll() {
		// return List.of(Pair.of("Layout 0: Easy", layout0()),
					// Pair.of("Layout 1: Easy w. rotation", layout1()),
					// Pair.of("Layout 2: Easy spec. order", layout2()),
					// Pair.of("Layout 3: Easy w. free space", layout3()),
					// Pair.of("Layout 4: 100 squares", layout4()),
					// Pair.of("Layout 5: Hard, no free space", layout5()),
					// Pair.of("Layout 6: Layout 5 w. one piece removed", layout6()),
					// Pair.of("Layout 7: Medium, rotation needed", layout7()),
					// Pair.of("Layout 8: Easy, small surface", layout8()),
					// Pair.of("Layout 9: Easy, rotation multiple directions", layout9()),
					// Pair.of("Layout 10: Easy, rotation multiple directions", layout10()),
					// Pair.of("Layout 11: 'Real example'", layout11()),
					// Pair.of("Layout 12: Layout 11 w. less free space", layout12()));
	// }
	
	/* ---- Possible solutions ---- */
	
	public static Layout layout0(){
		List<Part> parts = new ArrayList<>();
		parts.add(new Part(1,1,new Dimensions(5,10,1)));
		parts.add(new Part(2,1,new Dimensions(10,5,1)));
		Surface surface = Surface.of(10,10,1);
		Layout layout = new Layout(surface, parts);
		return layout;
	}
	
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

	// "Real" example, less free space than layout 11
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
    List<Surface> surfaces = List.of(
        Surface.of(dimensions(1000,600,500), dimensions(0,0,100)),
        Surface.of(dimensions(1000,600,600), dimensions(0,0,600)),
        Surface.of(dimensions(1000,600,0), dimensions(0,0,1200))    // XXX: eh, 0 thickness? or infinite?
      );
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
    return new Layout(Wagon.of(surfaces), parts);
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
