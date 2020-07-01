package se.ltu.kitting.test;

import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

import se.ltu.kitting.model.*;

@SuppressWarnings("serial")
public class Vis extends JPanel {

  private final Layout layout;
  private static final List<Color> colors = Arrays.asList(
    Color.RED, Color.ORANGE, Color.PINK, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN
  );
  private static final int margin = 100;
  private static final int offset = margin/3;
  private static int scale;
  private final int frameWidth;
  private final int frameHeight;


  private Vis(Layout layout, int width, int height) {
    this.layout = layout;
    this.frameWidth = width;
    this.frameHeight = height;
  }

  public static void draw(Layout layout) {
    draw(layout, "Layout");
  }
  
  public static void draw(Layout layout, String layoutDescription){
	JFrame frame = new JFrame(layoutDescription);
	scale = 300/layout.getSurface().depth();
    int width = (scale * layout.getSurface().width() + margin) + (scale * layout.getSurface().depth() + margin);
    int height = scale * layout.getSurface().depth() + margin;
    Vis v = new Vis(layout, width, height);
    frame.add(v);
    frame.setSize(width, height);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);	
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponents(g);
	paintBackground(g);
	paintSurface(g);
	paintParts(g);
  }

  public void paintBackground(Graphics g) {
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(0, 0, frameWidth, frameHeight);
  }
  
  public void paintSurface(Graphics g) {
	int sw = scale * layout.getSurface().width();
    int sh = scale * layout.getSurface().depth();
	//int offset = margin/3;
	g.setColor(Color.GRAY);
	g.fillRect(offset - 1,offset - 1, sw + 2, sh + 2);
	g.drawString("Solved Layout", offset + sw/3, 2* offset/3);
	g.drawString("Uninitialized parts", offset + sw + margin + sh/3, 2 * offset/3);
	g.setColor(Color.WHITE);
    g.fillRect(offset, offset, sw, sh);
    g.fillRect(offset + sw + margin, offset, sh, sh);
  }
  
  public void paintParts(Graphics g) {
    int count = 0;
    int lastUnplacedY = 0;
	//int offset = margin/3;
    for (Part part : layout.getParts()) {
      Dimensions pos = part.getPosition();
	  int originalPartWidth = scale * part.getSize().getX();
	  int originalPartDepth = scale * part.getSize().getY();
      if (pos == null) {
        g.setColor(Color.GRAY);
        g.fillRect(offset + scale * layout.getSurface().width() + margin + 10, offset + lastUnplacedY, originalPartWidth, originalPartDepth);
        lastUnplacedY += part.depth() * scale + 10;
      } else {
		int startPosX = offset + scale * pos.getX();
	    int startPosY = offset + scale * pos.getY();
	    int partWidth = scale * part.width();
	    int partDepth = scale * part.depth();
        g.setColor(colors.get(count % colors.size()));
        g.fillRect(startPosX, startPosY, partWidth, partDepth);
		g.setColor(Color.black);
		g.drawString(String.valueOf(part.getId()), startPosX + partWidth/2 - 5, startPosY + partDepth/2 + 12);
        if (part.getRotation() != null && part.getRotation() != Rotation.ZERO) {
          g.setColor(Color.BLACK);
          //g.drawRect(startPosX, startPosY, partWidth - 1, partDepth - 1);
		  g.drawString(rotationName(part.getRotation()), startPosX + partWidth/2 - 10, startPosY + partDepth/2);
        }
        count++;
      }
    }
  }

  private String rotationName(Rotation r) {
    if (r == null) { return "null"; }
    if (r == Rotation.ZERO) { return "ZERO"; }
    if (r == Rotation.X90) { return "X90"; }
    if (r == Rotation.Y90) { return "Y90"; }
    if (r == Rotation.Z90) { return "Z90"; }
    if (r == Rotation.Z90X90) { return "Z90X90"; }
    if (r == Rotation.Z90Y90) { return "Z90Y90"; }
    return String.format("(%.1f,%.1f,%.1f)", r.x, r.y, r.z);
  }

}
