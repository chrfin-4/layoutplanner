package se.ltu.kitting.api.json;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import com.google.gson.*;
import se.ltu.kitting.api.json.LayoutPlanningResponse; // JSON data
import se.ltu.kitting.api.json.LayoutPlanningRequest;  // JSON data
import se.ltu.kitting.api.PlanningResponse;     // domain model
import se.ltu.kitting.api.PlanningRequest;      // domain model
import se.ltu.kitting.api.Message;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.LayoutHint;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Rotation;
import se.ltu.kitting.model.WagonHint;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.Side;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    var result = new PlanningRequest(toModel(req.kit), parts, wagonHint);
    return validateRequest(req, result);
  }

  private static PlanningRequest validateRequest(LayoutPlanningRequest req, PlanningRequest result) {
    for (var p : req.parts) {
      if (p.layoutHint == null) {
        continue;
      }
      int z = (int) p.layoutHint.rotation;
      if (z != 0 && z != 90) {
        result.messages().addMessage((int) p.id, Message.warn("Only 0 and 90 degrees currently supported."));
      }
    }
    return result;
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
      result.layout = partLayout(part, res.solution());
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

  public static LayoutPlanningRequest.Part fromModel(final Part partModel) {
    final var part = new LayoutPlanningRequest.Part();
    part.dimensions = partModel.getSize();
    part.id = partModel.getId();
    part.minMargin = partModel.getMargin();
    part.partNumber = partModel.getPartNumber();
    //part.functionGroup = ...
    //part.partDesc = ...
    //part.requiredCapabilities = ...
    //part.weight = ...

    final var layoutHint = new LayoutPlanningRequest.Part.LayoutHint();
    if (partModel.hasHint()) {
      final var hint = partModel.getHint();
      layoutHint.orientation = hint.side().orElse(null);
      layoutHint.origin = hint.centerPosition();
      layoutHint.rotation = hint.rotation().map(r -> r.z).orElse(0);
      layoutHint.surfaceId = hint.surfaceId();
      layoutHint.weightFactor = hint.weight();
    }
    final var orientation = new LayoutPlanningRequest.Part.Orientation();
    orientation.allowedDown = new java.util.HashSet<>(partModel.getAllowedSidesDown());
    orientation.preferredDown = partModel.getPreferredDown();
    part.layoutHint = layoutHint;
    part.orientation = orientation;
    return part;
  }

  public static String toJson(final PlanningRequest req) {
    final var layout = req.getLayout();
    final var kit = fromModel(req.kit());
    final var wagon = fromModel(req.wagonHint().get().wagon());
    final var request = new LayoutPlanningRequest();
    final var wagonHint = new LayoutPlanningRequest.WagonHint();
    wagonHint.wagon = wagon;
    wagonHint.weightFactor = req.wagonHint().get().weightFactor();
    request.wagonHint = wagonHint;
    request.parts = layout.getParts().stream()
      .map(JsonIO::fromModel)
      .collect(toList());
    return gson.toJson(request);
  }

  // TODO: include messages
  /**
   * Construct a planning response from JSON. Needs the JSON representation of
   * both the response and the request that the response is a reponse to,
   * because the response itself does not carry enough information.
   */
  public static PlanningResponse response(String jsonRequest, String jsonResponse) {
    final var request = request(jsonRequest);
    final var partMap = request.parts().stream()
      .collect(toMap(Part::getId, p -> p));
    // Convert string to intermediate representation holding all the JSON data.
    LayoutPlanningResponse response = gson.fromJson(jsonResponse, LayoutPlanningResponse.class);
    // Then use intermediate representation to create domain model object.
    final var wagon = toModel(response.wagon);
    final var parts = new ArrayList<Part>();
    for (final var part : response.parts) {
      final Part tmp = partMap.get(part.id);
      final var layout = part.layout;
      tmp.setPosition(layout.origin.withZ(layout.surfaceId)); // FIXME: center to corner!
      tmp.setRotation(Rotation.of(layout.rotation));
      tmp.setSideDown(layout.orientation);
      parts.add(tmp);
    }
    return PlanningResponse.response_(request, new Layout(wagon, parts));
  }

  public static LayoutPlanningResponse.Part.Layout partLayout(Part part, Optional<Layout> layout) {
    if (layout.isEmpty()) {
      return null;
    }
    Dimensions position = part.getPosition();
    Side side = part.getSideDown();
    Rotation rotation = part.getRotation();
    if (position == null || side == null || rotation == null) {
      return null;
    }
    Surface surface = layout.get().surfaceOf(part);
    Dimensions center = part.currentCenter();
    var result = new LayoutPlanningResponse.Part.Layout();
    result.origin = center;
    result.orientation = side;
    result.rotation = rotation.z;
    result.surfaceId = surface.id;
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
    result.dimensions = wagon.dimensions();
    return result;
  }

  public static Wagon.Surface fromModel(Surface surface) {
    Wagon.Surface result = new Wagon.Surface();
    result.origin = surface.origin;
    result.dimensions = surface.dimensions;
    result.id = surface.id;
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
    Part part = new Part((int) p.id, p.partNumber, p.dimensions());
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
    Dimensions dimensions = hint.origin();
    var rotation = Rotation.of((int) hint.rotation());
    return new LayoutHint(dimensions, hint.surfaceId)
      .withRotation(rotation)
      .withWeight(hint.weightFactor())
      .withSide(hint.orientation);
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
    return se.ltu.kitting.model.Wagon.of(wagon.wagonId, surfaces)
      .withCapabilities(wagon.capabilities())
      .withDimensions(wagon.dimensions());
  }

  public static Surface toModel(Wagon.Surface surface) {
    Dimensions origin = surface.origin();
    Dimensions size = surface.dimensions();
    int id = surface.id;
    return Surface.surface(id, size, origin);
  }

}
