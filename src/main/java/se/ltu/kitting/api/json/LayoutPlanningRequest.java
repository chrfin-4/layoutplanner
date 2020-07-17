package se.ltu.kitting.api.json;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import se.ltu.kitting.model.Side;

/**
 * Low-level representation of an incoming request, mirroring the JSON schema.
 * Intermediate representation for translation from JSON.
<p>
<code>
{
  kit: {                      // required
    kitId: string,              // required
    chassisId: string,          // required
    side: "left"/"right",       // (optional, default="left")
  },
  wagonHint: {
    wagon: {
      wagonId: string,            // required
      capabilities: [string],     // required
      dimensions: Coordinate3d,
      surfaces: [                 // (optional!?, minimum 1 surface)
        origin: Coordinate3d,
        dimensions: Coordinate3d,
      ]
    },
    weightFactor: number,       // (optional, [1,10], default 1, will be truncated to integer)
  },
  parts: [                      // required (minimum 1 part)
    id: number,                   // required (NOTE: will always be truncated to integer.)
    partNumber: string,           // required
    dimensions: Coordinate3d      // required
    partDesc: string,
    functionGroup: string,
    minMargin: int                // (optional, will be rounded up to the nearest int)
    orientation: {                // required
      allowedDown: [
        "left"/"right"/"bottom"/"top"/"back"/"front"
      ],
      preferredDown: "left"/"right"/"bottom"/"top"/"back"/"front"
    },
    weight: number,               // (optional, in kg)
    requiredCapabilities: [string],
    layoutHint: {
      origin: Coordinate3d,       // required
      rotation: {
        rotation_z: int,          // rounded to 0 or 90 degrees
        rotation_x: int,          // ignored
        rotation_y: int,          // ignored
      },
      weightFactor: number,       // (optional, [1,10], default 1, will be truncated to integer)
    },
  ],
}
</code>
 * @author Christoffer Fink
 */
public class LayoutPlanningRequest {

  public Kit kit;
  public List<Part> parts;
  // Currently assumes that a wagon hint is provided.
  public WagonHint wagonHint;

  public static class Part {
    // Required.
    public double id;
    public String partNumber;
    public Coordinate3D dimensions;
    public Orientation orientation;
    // Optional.
    public String partDesc;
    public String functionGroup;
    public double weight;  // Min 0.
    public Collection<String> requiredCapabilities;
    public LayoutHint layoutHint;

    public Optional<LayoutHint> layoutHint() {
      return Optional.ofNullable(layoutHint);
    }

    public class Orientation {
      public Set<Side> allowedDown; // Optional?
      public Side preferredDown;    // Optional?

      public Optional<Set<Side>> allowedDown() {
        return Optional.ofNullable(allowedDown);
      }

      public Optional<Side> preferredDown() {
        return Optional.ofNullable(preferredDown);
      }

    }

    public static class LayoutHint {
      // Required.
      public Coordinate3D origin;
      // Optional.
      public Rotation rotation;
      public double weightFactor;

      public Coordinate3D origin() {
        return origin;
      }

      public Optional<Rotation> rotation() {
        return Optional.ofNullable(rotation);
      }

      public double weightFactor() {
        return weightFactor == 0.0 ? 1.0 : weightFactor;
      }
    }
  }

  public static class WagonHint {
    public Wagon wagon;
    public double weightFactor;

    public double weightFactor() {
      return weightFactor == 0.0 ? 1.0 : weightFactor;
    }
  }

  public Kit kit() {
    return kit;
  }

  public List<Part> parts() {
    return parts;
  }

  public Optional<WagonHint> wagonHint() {
    return Optional.ofNullable(wagonHint);
  }

}
