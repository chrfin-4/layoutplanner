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
    return LayoutBuilder.builder()
      .part(80,40,100)
      .part(120,20,100)
      .part(40,60,100)
      .surface(120,80,100)
      .build();
	}

	// Rotation needed, fits perfectly
	public static Layout demoLayout2(){
    return LayoutBuilder.builder()
      .surface(100,100,100)
      .part(25,100,100)
      .part(75,50,100)
      .part(25,75,100)
      .part(75,25,100)
      .build();
	}

	// Real example. 15 parts, covers 80% of surface if not rotated.
	public static Layout demoLayout3(){
    return LayoutBuilder.builder()
      .surface(120,80,100)
      .part(20,40,40)
      .part(40,40,40)
      .part(100,10,10)
      .part(20,20,20)
      .part(20,30,30)
      .part(10,50,50)
      .part(10,25,25)
      .part(10,25,25)
      .part(25,15,25)
      .part(35,15,35)
      .part(15,20,20)
      .part(15,20,20)
      .part(10,25,25)
      .part(20,15,20)
      .part(35,10,35)
      .build();
	}
}
