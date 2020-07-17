package se.ltu.kitting.api.json;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import com.google.gson.*;
import se.ltu.kitting.api.json.LayoutPlanningResponse; // JSON data
import se.ltu.kitting.api.json.LayoutPlanningRequest;  // JSON data
import se.ltu.kitting.api.PlanningResponse;     // domain model
import se.ltu.kitting.api.PlanningRequest;      // domain model
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.LayoutHint;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Rotation;
import se.ltu.kitting.model.WagonHint;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Side;

import static java.util.stream.Collectors.toList;

public class JsonIO {

  private static final Gson gson = new Gson();

  public static PlanningRequest request(String json) {
    // Convert string to intermediate representation holding all the JSON data.
    LayoutPlanningRequest req = gson.fromJson(json, LayoutPlanningRequest.class);
    // Then use intermediate representation to create domain model object.
    return request(req);
  }

  public static PlanningRequest request(LayoutPlanningRequest req) {
    List<Part> parts = req.parts.stream()
      .map(JsonIO::toModel)
      .collect(toList());
    Optional<WagonHint> wagonHint = req.wagonHint().map(JsonIO::toModel);
    return new PlanningRequest(toModel(req.kit), parts, wagonHint);
  }

  public static String toJson(PlanningResponse res) {
    return toJson(response(res));
  }

  public static LayoutPlanningResponse response(PlanningResponse res) {
    LayoutPlanningResponse response = new LayoutPlanningResponse();
    response.kit = fromModel(res.request().kit());
    response.wagon = fromModel(res.request().wagonHint().get().wagon());
    List<LayoutPlanningResponse.Part> parts = new ArrayList<>();
    for (var part : res.parts()) {
      var result = new LayoutPlanningResponse.Part();
      result.id = part.getId();
      result.partNumber = part.getPartNumber();
      result.layout = partLayout(part);
      res.messagesForPart(part.getId())
        .ifPresent(m -> result.messagesToDisplay = m);
      parts.add(result);
    }
    response.parts = parts;
    res.globalMessages()
        .ifPresent(m -> response.messagesToDisplay = m);
    return response;
  }

  public static String toJson(LayoutPlanningResponse res) {
    return gson.toJson(res);
  }

  public static PlanningResponse response(String json) {
    // Convert string to intermediate representation holding all the JSON data.
    LayoutPlanningRequest req = gson.fromJson(json, LayoutPlanningRequest.class);
    // Then use intermediate representation to create domain model object.
    throw new UnsupportedOperationException("Not implemented.");
  }

  public static LayoutPlanningResponse.Part.Layout partLayout(Part part) {
    Dimensions position = part.getPosition();
    Side side = part.getSideDown();
    Rotation rotation = part.getRotation();
    if (position == null || side == null || rotation == null) {
      return null;
    }
    Dimensions center = Part.cornerToCenter(position, part.currentDimensions());
    var result = new LayoutPlanningResponse.Part.Layout();
    result.origin = Coordinate3D.from(center);
    result.orientation = side;
    result.rotation = se.ltu.kitting.api.json.Rotation.from(rotation);
    return result;
  }

  public static Wagon fromModel(se.ltu.kitting.model.Wagon wagon) {
    List<Wagon.Surface> surfaces = wagon.surfaces().stream()
      .map(JsonIO::fromModel)
      .collect(toList());
    var result = new Wagon();
    result.surfaces = surfaces;
    result.wagonId = wagon.wagonId();
    result.capabilities = wagon.capabilities();
    result.dimensions = Coordinate3D.from(wagon.dimensions());
    return result;
  }

  public static Wagon.Surface fromModel(Surface surface) {
    Coordinate3D origin = Coordinate3D.from(surface.origin);
    Coordinate3D dimensions = Coordinate3D.from(surface.dimensions);
    Wagon.Surface result = new Wagon.Surface();
    result.origin = origin;
    result.dimensions = dimensions;
    return result;
  }

  public static Kit fromModel(se.ltu.kitting.model.Kit kit) {
    var result = new Kit();
    result.kitId = kit.kitId();
    result.chassisId = kit.chassisId();
    result.side = kit.side();
    return result;
  }

  public static Part toModel(LayoutPlanningRequest.Part p) {
    Part part = new Part((int) p.id, p.partNumber, p.dimensions.toDimensions());
    p.orientation.allowedDown().ifPresent(part::setAllowedDown);
    p.orientation.preferredDown().ifPresent(part::setPreferredDown);
    p.layoutHint().map(JsonIO::toModel).ifPresent(part::setHint);
    // XXX: Ignoring:
    //  - description
    //  - function group
    //  - weight
    //  - required capabilities
    return part;
  }

  public static LayoutHint toModel(LayoutPlanningRequest.Part.LayoutHint hint) {
    Dimensions dimensions = hint.origin().toDimensions();
    Optional<Rotation> rotation = hint.rotation().map(r -> r.toRotation());
    return new LayoutHint(dimensions, rotation, hint.weightFactor());
  }

  public static se.ltu.kitting.model.Kit toModel(Kit kit) {
    return new se.ltu.kitting.model.Kit(kit.kitId, kit.chassisId, kit.side);
  }

  public static WagonHint toModel(LayoutPlanningRequest.WagonHint hint) {
    se.ltu.kitting.model.Wagon wagon = toModel(hint.wagon);
    return new WagonHint(wagon, (int) hint.weightFactor());
  }

  public static se.ltu.kitting.model.Wagon toModel(Wagon wagon) {
    List<Surface> surfaces = wagon.surfaces().stream()
      .map(JsonIO::toModel)
      .collect(toList());
    var result = se.ltu.kitting.model.Wagon.of(wagon.wagonId, surfaces)
      .withCapabilities(wagon.capabilities());
    return wagon.dimensions()
      .map(Coordinate3D::toDimensions)
      .map(result::withDimensions)
      .orElse(result);
  }

  public static Surface toModel(Wagon.Surface surface) {
    Dimensions origin = surface.origin().map(Coordinate3D::toDimensions).get();
    Dimensions size = surface.dimensions().map(Coordinate3D::toDimensions).get();
    return Surface.of(size, origin);
  }

}
