package se.ltu.kitting.api;

import java.util.List;
import java.util.Optional;
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
    .registerTypeAdapter(Rotation.class, new RotationDeserializer())
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

  // XXX: include more than just the parts.
  public static LayoutPlanningResponse fromLayout(LayoutPlanningRequest request, Layout layout) {
    List<PartOut> parts = layout.getParts().stream().map(PartOut::new).collect(toList());
    LayoutPlanningResponse response = new LayoutPlanningResponse(parts);
    response.wagon = layout.getWagon();
    return response;
  }

  public static LayoutPlanningResponse fromError(LayoutPlanningRequest request, Exception e) {
    List<PartOut> parts = request.getLayout().getParts().stream().map(PartOut::nullLayout).collect(toList());
    LayoutPlanningResponse response = new LayoutPlanningResponse(parts);
    response.messagesToDisplay = List.of(Message.error(e.getMessage()));
    response.version = request.version;
    response.kit = request.kit;
    return response;
  }

  public static LayoutPlanningResponse fromJson(String json) {
    return gson.fromJson(json, LayoutPlanningResponse.class);
  }

  public String toJson() {
    return gson.toJson(this);
  }

  public Optional<Dimensions> getCenterPosition(int partId) {
    return getPartLayout(partId).map(l -> l.origin);
  }

  public Optional<Rotation> getRotation(int partId) {
    return getPartLayout(partId).map(l -> l.rotation);
  }

  public Optional<Side> getSide(int partId) {
    return getPartLayout(partId).map(l -> l.orientation);
  }

  private Optional<Layout_> getPartLayout(int partId) {
    return parts.stream().filter(p -> p.id == partId).findAny().map(p -> p.layout);
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

    PartOut() {
    }

    PartOut(Part part) {
      id = part.getId();
      partNumber = part.getPartNumber();
      layout = new Layout_(part.currentCenter(), part.getSideDown(), part.getRotation());
    }

    public static PartOut nullLayout(Part part) {
      PartOut p = new PartOut();
      p.id = part.getId();
      p.partNumber = part.getPartNumber();
      p.layout = null;
      return p;
    }

  }

}

enum Severity {
  error, warning, info;
}

class Message {
  private String message;
  private Severity severity;
  private String code;

  private Message(String message, Severity severity, String code) {
    this.message = message;
    this.severity = severity;
    this.code = code;
  }

  public static Message info(String msg) {
    return new Message(msg, Severity.info, "?");
  }

  public static Message warn(String msg) {
    return new Message(msg, Severity.warning, "?");
  }

  public static Message error(String msg) {
    return new Message(msg, Severity.error, "?");
  }

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
