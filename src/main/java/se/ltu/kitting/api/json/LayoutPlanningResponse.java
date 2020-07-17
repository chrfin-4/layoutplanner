package se.ltu.kitting.api.json;

import java.util.List;
import java.util.Optional;
import java.lang.reflect.Type;
import com.google.gson.*;
import se.ltu.kitting.model.Side;

import se.ltu.kitting.api.Message;

import static java.util.stream.Collectors.toList;

/**
 * Low-level representation of an outgoing response, mirroring the JSON schema.
 * Intermediary representation for translation to JSON.
<p>
<code>
{
  kit: {                    // required
    kitId: string,            // required
    chassisId: string,        // required
    side: "left"/"right",     // (optional, default="left")
  },
  wagon: {                        // required
    wagonId: string,                // required
    capabilities: [string],         // required
    dimensions: {                   // required (NOTE: numbers truncated to int)
      x: number,
      y: number,
      z: number,
    },
    surfaces: [                   // required (minimum 1 surface)
      origin: Coordinate3d,         // !! not required ???
      dimensions: Coordinate3d,     // !! not required ???
    ],
  },
  parts: [                        // required (minimum 1 part)
    id: int,                        // required (NOTE: SCHEMA SAYS "number", but will always be an int.)
    partNumber: string,             // required
    layout: {
      origin: Coordinate3d,         // required
      orientation: "left"/"right"/"bottom"/"top"/"back"/"front", // required
      rotation: {
        rotation_z: number,         // 0 or 90
        rotation_x: number,         // always 0
        rotation_y: number,         // always 0
      },
    },
    messagesToDisplay: [
      message: string,          // required
      severity: "error"/"warning"/"info",     // required (default="error")
      code: string,
    }
  ],
  messagesToDisplay: [
    message: string,                      // required
    severity: "error"/"warning"/"info",   // required (default="error")
    code: string,
  ],
}
</code>
 * @author Christoffer Fink
 */
public class LayoutPlanningResponse {

  // Required.
  public Kit kit;
  public List<Part> parts;
  public Wagon wagon;
  // Optional.
  public List<Message> messagesToDisplay;

  public static class Part {
    // Required.
    public int id;
    public String partNumber;
    // Optional.
    public Layout layout;
    public List<Message> messagesToDisplay;

    public static class Layout {
      // Required.
      public Coordinate3D origin;
      public Side orientation;
      // Optional. (But leaving it out doesn't make much sense.)
      public Rotation rotation;
    }
  }

}
