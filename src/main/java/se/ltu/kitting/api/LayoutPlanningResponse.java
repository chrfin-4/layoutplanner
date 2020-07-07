package se.ltu.kitting.api;

import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.*;
import se.ltu.kitting.model.*;

import static java.util.stream.Collectors.toList;

// TODO: needs cleanup.
/**
 * Represents the result of trying to solve a layout request.
 * Intermediary representation for translation to JSON.
 * @author Christoffer Fink
 */
public class LayoutPlanningResponse {

  private static final Gson gson = new GsonBuilder()
    .registerTypeAdapter(Rotation.class, new RotationSerializer())
    .create();

  // Required.
  private String version;
  private Kit kit;
  private List<PartOut> parts;
  private Wagon wagon;

  // Optional.
  private List<Message> messagesToDisplay;

  private LayoutPlanningResponse(List<PartOut> parts) {
    this.parts = parts;
  }

  public static LayoutPlanningResponse fromLayout(Layout layout) {
    List<PartOut> parts = layout.getParts().stream().map(PartOut::new).collect(toList());
    return new LayoutPlanningResponse(parts);
  }

  public String toJson() {
    return gson.toJson(this);
  }

  private static class Layout_ {
    // Required.
    private Dimensions origin;
    private Side orientation;

    // Optional.
    private Rotation rotation;

    Layout_(Dimensions origin, Side orientation, Rotation rotation) {
      this.origin = origin;
      this.orientation = orientation;
      this.rotation = rotation;
    }
  }

  private static class PartOut {
    // Required.
    private int id;
    private String partNumber;

    // Optional.
    private Layout_ layout;

    PartOut(Part part) {
      id = part.getId();
      partNumber = part.getPartNumber();
      layout = new Layout_(part.currentCenter(), part.getSideDown(), part.getRotation());
    }
  }

}

enum Severity {
  error, warning, info;
}

class Message {
  private String message;
  private String code;
  private Severity severity;
}

// TODO: Make response more compact by omitting zeros?
class RotationSerializer implements JsonSerializer<Rotation> {
  public JsonElement serialize(Rotation src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject obj = new JsonObject();
    obj.addProperty("rotation_x", src.x);
    obj.addProperty("rotation_y", src.y);
    obj.addProperty("rotation_z", src.z);
    return obj;
  }
}
