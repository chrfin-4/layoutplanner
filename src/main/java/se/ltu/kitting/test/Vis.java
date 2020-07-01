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
    Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.PINK
  );
  private static final int margin = 100;
  private static final int scale = 10;
  private final int frameWidth;
  private final int frameHeight;

  private Vis(Layout layout, int width, int height) {
    this.layout = layout;
    this.frameWidth = width;
    this.frameHeight = height;
  }

  public static void draw(Layout layout) {
    JFrame frame = new JFrame("Layout");
    int width = 2 * (scale * layout.getSurface().width() + margin);
    int height = scale * layout.getSurface().depth() + margin;
    Vis v = new Vis(layout, width, height);
    frame.add(v);
    frame.setSize(width, height);
    //frame.setSize(100, 100);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponents(g);
    int sw = layout.getSurface().width();
    int sh = layout.getSurface().depth();

    g.setColor(Color.BLACK);
    g.fillRect(0, 0, frameWidth, frameHeight);
    g.setColor(Color.WHITE);
    int offset = margin/2;
    g.fillRect(offset,offset, scale * sw, scale * sh);
    g.fillRect(offset+frameWidth/2, offset, scale * sw, scale * sh);
    int count = 0;
    int lastUnplacedY = 0;
    for (Part part : layout.getParts()) {
      Dimensions pos = part.getPosition();
      if (pos == null) {
        g.setColor(Color.GRAY);
        g.fillRect(offset+frameWidth/2 + 10, offset + lastUnplacedY, scale * part.getSize().getX(), scale * part.getSize().getY());
        lastUnplacedY += part.depth() * scale + 10;
      } else {
        g.setColor(colors.get(count % colors.size()));
        g.fillRect(offset + scale * pos.getX(), offset + scale * pos.getY(), scale * part.width(), scale * part.depth());
        if (part.getRotation() != null && part.getRotation() != Rotation.ZERO) {
          g.setColor(Color.GRAY);
          g.drawRect(offset + scale * pos.getX(), offset + scale * pos.getY(), scale * part.width(), scale * part.depth());
        }
        count++;
      }
    }
    g.setColor(Color.WHITE);
    g.drawRect(offset-1,offset-1, scale * sw+1, scale * sh+1);
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
