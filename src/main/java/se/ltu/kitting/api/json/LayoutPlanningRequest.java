package se.ltu.kitting.api.json;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.model.Dimensions;

/**
 * Low-level representation of an incoming request, mirroring the JSON schema.
 * Intermediate representation for translation from JSON.
<p>
<code>
// As of commit 65d402d4a8a50ee96a362bb8b7cd3abf87f0c698 (2020-07-27)
{
  kit: {
    kitId: string,              // required
    chassisId: string,          // required
    side: "left"/"right",       // (optional, default="left")
  },
  wagonHint: {
    wagon: {
      wagonId: string,            // required
      capabilities: [string],     // required
      dimensions: Coordinate3d,   // required
      surfaces: [                 // required, minimum 1 surface
        id: integer,              // required
        origin: Coordinate3d,     // required
        dimensions: Coordinate3d, // required
      ]
    },
    weightFactor: number,       // (optional, [1,10], default 1, will be truncated to integer)
  },
  parts: [                      // required (minimum 1 part)
    id: integer,                  // required
    partNumber: string,           // required
    dimensions: Coordinate3d      // required
    orientation: {                // required
      allowedDown: [
        "left"/"right"/"bottom"/"top"/"back"/"front"                // optional?
      ],
      preferredDown: "left"/"right"/"bottom"/"top"/"back"/"front"   // optional?
    },
    partDesc: string,
    functionGroup: string,
    minMargin: number,            // (optional, will be rounded up to the nearest int)
    weight: number,               // (optional, in kg)
    requiredCapabilities: [string],
    layoutHint: {
      origin: Coordinate3d,       // required
      surfaceId: integer,         // required
      orientation: "left"/"right"/"bottom"/"top"/"back"/"front"
      rotation: number,           // truncated to integer
      weightFactor: number,       // (optional, [1,10], default 1, will be truncated to integer)
    },
  ],
}
</code>
 * @author Christoffer Fink
 */
public class LayoutPlanningRequest {

  // Defined by schema.
  public static final int defaultCoordinates = 0;
  public static final double minRotation = 0.0;
  public static final double maxRotation = 359.0;
  public static final double defaultRotation = 0.0;
  public static final double minWeightFactor = 1.0;
  public static final double maxWeightFactor = 10.0;
  public static final double defaultWeightFactor = 1.0;
  public static final double minWeight = 0.0;
  public static final Side defaultKitSide = Side.left;

  // Defined locally (not in schema).
  public static final double defaultMargin = 0.0;
  public static final double minMargin = 0.0;

  public Kit kit;
  public List<Part> parts;
  // Currently assumes that a wagon hint is provided.
  public WagonHint wagonHint;

  public static class Part {
    // Required.
    public int id;
    public String partNumber;
    public Dimensions dimensions;
    public Orientation orientation;
    // Optional.
    public String partDesc;
    public String functionGroup;
    public double weight;  // Min 0.
    public Collection<String> requiredCapabilities;
    public LayoutHint layoutHint;
    public double minMargin;

    public Optional<LayoutHint> layoutHint() {
      return Optional.ofNullable(layoutHint);
    }

    public Dimensions dimensions() {
      return dimensions;
    }

    public Part dimensions(Dimensions dimensions) {
      this.dimensions = dimensions;
      return this;
    }

    public static class Orientation {
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
      public Dimensions origin;
      public int surfaceId;
      // Optional.
      public double rotation;
      public double weightFactor;
      public Side orientation;

      public Dimensions origin() {
        return origin;
      }

      public int surfaceId() {
        return surfaceId;
      }

      public Side orientation() {
        return orientation;
      }

      public double rotation() {
        return rotation;
      }

      public double weightFactor() {
        return weightFactor == 0.0 ? 1.0 : weightFactor;
      }

      public LayoutHint origin(Dimensions origin) {
        this.origin = origin;
        return this;
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
