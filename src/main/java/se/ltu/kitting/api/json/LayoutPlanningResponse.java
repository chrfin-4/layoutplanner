package se.ltu.kitting.api.json;

import java.util.List;
import java.util.Optional;
import java.lang.reflect.Type;
import com.google.gson.*;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.model.Dimensions;

import se.ltu.kitting.api.Message;

import static java.util.stream.Collectors.toList;

/**
 * Low-level representation of an outgoing response, mirroring the JSON schema.
 * Intermediary representation for translation to JSON.
<p>
<code>
// As of commit 8fd0af41b4b48c2802b60b352d83d9ba65ce09f3 (2020-07-27)
{
  kit: {                          // required
    kitId: string,                  // required
    chassisId: string,              // required
    side: "left"/"right",           // (optional, default="left")
  },
  wagon: {                        // required
    wagonId: string,                // required
    capabilities: [string],         // required
    dimensions: {                   // required
      x: integer,                   // default 0
      y: integer,                   // default 0
      z: integer,                   // default 0
    },
    surfaces: [                   // required (minimum 1 surface)
      id: integer,                  // required
      origin: Coordinate3d,
      dimensions: Coordinate3d,
    ],
  },
  parts: [                        // required (minimum 1 part)
    id: integer,                    // required
    partNumber: string,             // required
    layout: {
      surfaceId: integer,           // required
      origin: Coordinate3d,         // required
      orientation: "left"/"right"/"bottom"/"top"/"back"/"front", // required
      rotation: number,             // (optional, default 0, values [0,359] allowed, is always 0 or 90)
    },
    messagesToDisplay: [            // (optional, default [])
      message: string,                // required
      severity: "error"/"warning"/"info",     // required (default="error")
      code: string,                   // requred
    }
  ],
  messagesToDisplay: [            // (optional, default [])
    message: string,                      // required
    severity: "error"/"warning"/"info",   // required (default="error")
    code: string,                         // required
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
      public int surfaceId;
      //public Coordinate3D origin;
      public Dimensions origin;
      public Side orientation;
      // Optional. (But leaving it out doesn't make much sense.)
      public int rotation;
    }
  }

}
