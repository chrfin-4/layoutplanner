package se.ltu.kitting.api;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.*;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Kit;
import se.ltu.kitting.model.Wagon;
import se.ltu.kitting.model.Rotation;
import java.lang.reflect.Type;

import static java.util.stream.Collectors.toList;

// TODO: needs lots of cleanup!
/**
 * Represents an incoming request and holds all parameters.
 * Mainly as an intermediary representation between the JSON data and the
 * "real" model.
 * @author Christoffer Fink
 */
public class LayoutPlanningRequest {

  // XXX: required or not? Is nothing required?
  private String version;
  private Kit kit;
  // The request must have a non-empty list of parts.
  private List<Part_> parts;

  // Currently assumes that a wagon hint is provided.
  private WagonHint wagonHint;

  public static LayoutPlanningRequest fromJson(String json) {
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(se.ltu.kitting.model.Rotation.class, new RotationDeserializer())
      .create();
    return gson.fromJson(json, LayoutPlanningRequest.class);
  }

  public Optional<WagonHint> wagonHint() {
    return Optional.ofNullable(wagonHint);
  }

  public Layout getLayout() {
    // Just use a single surface for now.
    se.ltu.kitting.model.Surface surface = wagonHint()
      .flatMap(WagonHint::wagon)
      .map(Wagon::surfaces)
      .map(l -> l.get(0))
      .get();
    return new Layout(surface, parts.stream().map(Part_::toPart).collect(toList()));
  }

  public static class WagonHint {
    // If I understand the JSON schema correctly, neither of these is required.
    // But surely an optional wagon hint that fails to mention a wagon isn't very useful?
    private Wagon wagon;
    private int weightFactor; // [1,10], 10 = mandatory

    public Optional<Wagon> wagon() {
      return Optional.ofNullable(wagon);
    }

    public int weightFactor() {
      return getWeightFactorOrDefault(weightFactor);
    }

  }

  // DIFFERENT in request and response.
  public static class Part_ {

    // Required.
    private int id;
    private String partNumber;
    private Dimensions dimensions;
    private Orientation orientation;

    // Optional.
    private String partDesc;
    private String functionGroup;
    private int quantity;   // Min 1.
    private double weight;  // Min 0.
    private Collection<String> requiredCapabilities;
    private LayoutHint layoutHint;

    public se.ltu.kitting.model.Part toPart() {
      se.ltu.kitting.model.Part part = new se.ltu.kitting.model.Part(id, partNumber, dimensions);
      part.setAllowedDown(orientation.allowedDown);
      part.setPreferredDown(orientation.preferredDown);
      PartInfo info = new PartInfo(partDesc, functionGroup, requiredCapabilities, layoutHint);
      info.layoutHint().map(LayoutHint::origin).ifPresent(part::setPosition);
      info.layoutHint().flatMap(LayoutHint::rotation).ifPresent(part::setRotation);
      // TODO: add part info to Part;
      return part;
    }

    // DIFFERENT in request and response.
    // (In response, this is just Side.)
    private class Orientation {
      private Set<Side> allowedDown;
      private Side preferredDown;
    }

  }

  public static int getWeightFactorOrDefault(int weightFactor) {
    return weightFactor >= 1 ? weightFactor : 1;
  }
}

class RotationDeserializer implements JsonDeserializer<Rotation> {
  public Rotation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();
    int x = get(obj, "rotation_x");
    int y = get(obj, "rotation_y");
    int z = get(obj, "rotation_z");
    return Rotation.of(x, y, z);
  }

  private static int get(JsonObject obj, String member) {
    if (obj.has(member)) {
      return obj.getAsJsonPrimitive(member).getAsInt();
    } else {
      return 0;
    }
  }
}

// Note: Does not have an orientation.
class LayoutHint {
  // Required.
  private Dimensions origin;

  // Optional.
  private Rotation rotation;
  private int weightFactor; // [1,10], 10 = mandatory

  public Dimensions origin() {
    return origin;
  }

  public Optional<Rotation> rotation() {
    return Optional.ofNullable(rotation);
  }

  // TODO: Use OptionalInt or default value?
  public int weightFactor() {
    return LayoutPlanningRequest.getWeightFactorOrDefault(weightFactor);
  }

}
