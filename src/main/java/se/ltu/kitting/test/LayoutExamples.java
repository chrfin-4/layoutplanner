package se.ltu.kitting.test;

import java.util.*;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.model.Wagon;
import se.ltu.kitting.model.Rotation;
import ch.rfin.util.Pair;

import static se.ltu.kitting.model.Side.*;
import static se.ltu.kitting.model.Dimensions.dimensions;
import static java.util.stream.Collectors.toList;

public class LayoutExamples {

	private static List<Pair<String,Layout>> layouts = List.of(Pair.of("Layout 0: Trivial", layout0()),
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
					
	private static List<Pair<String,Layout>> realisticLayouts = List.of(Pair.of("Layout 1: 8 items, lots of space", realisticLayout1()),
					Pair.of("Layout 2: 8 items, half space", realisticLayout2()),
					Pair.of("Layout 3: 8 items, low height", realisticLayout3()),
					Pair.of("Layout 4: 16 items, full space", realisticLayout4()));				

	public static List<Pair<String,Layout>> getAll() {
		return layouts;
	}
	
	public static List<Pair<String,Layout>> realisticLayouts() {
		return realisticLayouts;
	}

	public static String layoutDescription(int index) {
		if(index >= layouts.size()){
			throw new IndexOutOfBoundsException("Layout " + index + " does not exist");
		}
		return layouts.get(index)._1;
	}

  public static List<Pair<String,Layout>> layouts(int ... indices) {
    return Arrays.stream(indices).mapToObj(layouts::get).collect(toList());
  }

	public static Layout layout(int index){
		if(index >= layouts.size()){
			throw new IndexOutOfBoundsException("Layout " + index + " does not exist");
		}
		return layouts.get(index)._2;
	}

	/* ---- Possible solutions ---- */

  public static Layout layout0() {
    return LayoutBuilder.builder()
      .surface(1,1,1)
      .part(1,1,1)
      .build();
  }

	/* NOTE: Only for Z90 rotation */

	// Rotation needed, covers surface completely
  public static Layout layout1() {
    return LayoutBuilder.builder()
      .surface(10,10,1)
      .part(5,10,1)
      .part(10,5,1)
      .build();
  }

	// Need to be placed in other order than given, 100 points empty, should not rotate
	public static Layout layout2() {
    return LayoutBuilder.builder()
      .surface(30,20,1)
      .part(20,10,1)
      .part(30,5,1)
      .part(10,15,1)
      .build();
	}

	// Easy to place, 250 points empty, small surface
	public static Layout layout3() {
    return LayoutBuilder.builder()
      .surface(30,20,1)
      .part(15,10,1)
      .part(10,10,1)
      .part(20,5,1)
      .build();
	}

	// 100 squares fit perfectly
	public static Layout layout4() {
    return LayoutBuilder.builder()
      .surface(100,100,1)
      .part().dimensions(10,10,1).add(100)
      .build();
	}

	// Odd shapes that just barely fit perfectly
	public static Layout layout5() {
    return LayoutBuilder.builder()
      .surface(100,100,1)
      .part(35,30,1)
      .part(20,45,1)
      .part(45,45,1)
      .part(35,60,1)
      .part(50,45,1)
      .part(15,55,1)
      .part(85,10,1)
      .build();
	}

	// Variation of layout5, one piece removed, 2025 points empty
	public static Layout layout6() {
    return LayoutBuilder.builder()
      .surface(100,100,1)
      .part(35,30,1)
      .part(20,45,1)
      .part(35,60,1)
      .part(50,45,1)
      .part(15,55,1)
      .part(85,10,1)
      .build();
	}

	// Rotation needed, fits perfectly.
	public static Layout layout7() {
    return LayoutBuilder.builder()
      .surface(100,100,1)
      .part(25,100,1)
      .part(75,50,1)
      .part(25,75,1)
      .part(75,25,1)
      .build();
	}

	// Small surface, no rotation needed, 15 points empty
	public static Layout layout8() {
    return LayoutBuilder.builder()
      .surface(10,10,1)
      .part(5,5,1)
      .part(2,10,1)
      .part(8,5,1)
      .build();
	}

	/* For all rotations */

	// Need rotation in both X90 and Y90
	public static Layout layout9() {
    return LayoutBuilder.builder()
      .surface(10,10,10)
      .part(10,5,10)
      .part(10,10,3)
      .part(2,10,10)
      .build();
	}

	// Need rotation in double directions Z90Y90 and Z90X90
	public static Layout layout10() {
    return LayoutBuilder.builder()
      .surface(30,13,5)
      .part(10,5,20)
      .part(5,30,3)
      .build();
	}

	// "Real" example
	public static Layout layout11() {
    return LayoutBuilder.builder()
      .surface(1200,800,1000)
      .part(200,400,100)
      .part(400,400,400)
      .part(1000,100,100)
      .part(200,200,200)
      .part(200,300,200)
      .part(100,500,300)
      .part(100,250,100)
      .part(100,250,100)
      .part(250,150,200)
      .part(350,150,200)
      .part(150,200,200)
      .part(150,200,200)
      .part(100,250,200)
      .part(200,150,100)
      .part(350,100,100)
      .build();
	}

	// "Real" example, less free space than layout 11
	public static Layout layout12() {
    return LayoutBuilder.builder()
      .surface(1200,800,1000)
      .part(200,400,400)
      .part(400,400,400)
      .part(1000,100,1000)
      .part(200,200,200)
      .part(200,300,300)
      .part(100,500,500)
      .part(100,250,250)
      .part(100,250,250)
      .part(250,150,250)
      .part(350,150,350)
      .part(150,200,200)
      .part(150,200,200)
      .part(100,250,250)
      .part(200,150,200)
      .part(350,100,350)
      .build();
	}

  /* --- Realistic examples based on objects placed on a table --- */
  public static Layout realisticLayout1() {
    return LayoutBuilder.builder()
      .surface(1500,780,1000)
      .part().dimensions(480,200,330).allowSides(bottom, back).preferredSide(bottom).partNumber("Monitor").add()
      .part().dimensions(290,190,70).partNumber("Cornflakes").add() // All
      .part().dimensions(220,320,150).allowSides(bottom).partNumber("Plastic box").margin(5).add()
      .part().dimensions(150,60,110).partNumber("Screws").mandatory(Dimensions.of(1425,705,0), Rotation.Z90).add() // All
      .part().dimensions(160,460,30).allowSides(bottom).partNumber("Keyboard").margin(10).add()
      .part().dimensions(200,240,5).allowSides(bottom, top).partNumber("Mousepad").mandatory(Dimensions.of(120,120,0)).add() 
      .part().dimensions(190,120,60).allowSides(bottom, top).preferredSide(bottom).partNumber("Lunchbox").add() 
      .part().dimensions(110,60,30).allowSides(bottom).partNumber("Mouse").margin(20).add()
      .build();
  }
  
  public static Layout realisticLayout2() {
    return LayoutBuilder.builder()
      .surface(750,780,1000)
      .part().dimensions(480,200,330).allowSides(bottom, back).preferredSide(bottom).partNumber("Monitor").add()
      .part().dimensions(290,190,70).partNumber("Cornflakes").add() // All
      .part().dimensions(220,320,150).allowSides(bottom).partNumber("Plastic box").margin(5).add()
      .part().dimensions(150,60,110).partNumber("Screws").mandatory(Dimensions.of(675,705,0), Rotation.Z90).add() // All
      .part().dimensions(160,460,30).allowSides(bottom).partNumber("Keyboard").margin(10).add()
      .part().dimensions(200,240,5).allowSides(bottom, top).partNumber("Mousepad").mandatory(Dimensions.of(120,120,0)).add() 
      .part().dimensions(190,120,60).allowSides(bottom, top).preferredSide(bottom).partNumber("Lunchbox").add() 
      .part().dimensions(110,60,30).allowSides(bottom).partNumber("Mouse").margin(20).add()
      .build();
  }
  
  public static Layout realisticLayout3() {
    return LayoutBuilder.builder()
      .surface(1500,780,200)
      .part().dimensions(480,200,330).allowSides(bottom, back).preferredSide(bottom).partNumber("Monitor").add()
      .part().dimensions(290,190,70).partNumber("Cornflakes").add() // All
      .part().dimensions(220,320,150).allowSides(bottom).partNumber("Plastic box").margin(5).add()
      .part().dimensions(150,60,110).partNumber("Screws").mandatory(Dimensions.of(1425,705,0), Rotation.Z90).add() // All
      .part().dimensions(160,460,30).allowSides(bottom).partNumber("Keyboard").margin(10).add()
      .part().dimensions(200,240,5).allowSides(bottom, top).partNumber("Mousepad").mandatory(Dimensions.of(120,120,0)).add() 
      .part().dimensions(190,120,60).allowSides(bottom, top).preferredSide(bottom).partNumber("Lunchbox").add() 
      .part().dimensions(110,60,30).allowSides(bottom).partNumber("Mouse").margin(20).add()
      .build();
  }
  
  public static Layout realisticLayout4() {
    return LayoutBuilder.builder()
      .surface(1500,780,1000)
      .part().dimensions(480,200,330).allowSides(bottom, back).preferredSide(bottom).partNumber("Monitor").add()
      .part().dimensions(290,190,70).partNumber("Cornflakes").add() // All
      .part().dimensions(220,320,150).allowSides(bottom).partNumber("Plastic box").margin(5).add()
      .part().dimensions(150,60,110).partNumber("Screws").mandatory(Dimensions.of(1425,705,0), Rotation.Z90).add() // All
      .part().dimensions(160,460,30).allowSides(bottom).partNumber("Keyboard").margin(10).add()
      .part().dimensions(200,240,5).allowSides(bottom, top).partNumber("Mousepad").mandatory(Dimensions.of(120,120,0)).add() 
      .part().dimensions(190,120,60).allowSides(bottom, top).preferredSide(bottom).partNumber("Lunchbox").add() 
      .part().dimensions(110,60,30).allowSides(bottom).partNumber("Mouse").margin(20).add()
      .part().dimensions(480,200,330).allowSides(bottom, back).preferredSide(bottom).partNumber("Monitor").add()
      .part().dimensions(290,190,70).partNumber("Cornflakes").add() // All
      .part().dimensions(220,320,150).allowSides(bottom).partNumber("Plastic box").margin(5).add()
      .part().dimensions(150,60,110).partNumber("Screws").add() // All
      .part().dimensions(160,460,30).allowSides(bottom).partNumber("Keyboard").margin(10).add()
      .part().dimensions(200,240,5).allowSides(bottom, top).partNumber("Mousepad").add() 
      .part().dimensions(190,120,60).allowSides(bottom, top).preferredSide(bottom).partNumber("Lunchbox").add() 
      .part().dimensions(110,60,30).allowSides(bottom).partNumber("Mouse").margin(20).add()
      .build();
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
	public static Layout unsolvableLayout1() {
    return LayoutBuilder.builder()
      .surface(10,10,1)
      .part(10,10,1)
      .part(10,10,1)
      .build();
	}

	// Fits when looking at area but one part is to wide
	public static Layout unsolvableLayout2() {
    return LayoutBuilder.builder()
      .surface(10,10,1)
      .part(15,5,1)
      .part(5,5,1)
      .build();
	}
}
