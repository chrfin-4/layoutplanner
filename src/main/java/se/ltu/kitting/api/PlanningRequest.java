package se.ltu.kitting.api;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import se.ltu.kitting.model.Dimensions;
import se.ltu.kitting.model.Side;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Kit;
import se.ltu.kitting.model.Wagon;
import se.ltu.kitting.model.Rotation;
import se.ltu.kitting.model.Surface;
import se.ltu.kitting.model.LayoutHint;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.WagonHint;
import java.lang.reflect.Type;

import static java.util.stream.Collectors.toList;

/**
 * Represents an incoming request and holds all parameters.
 * Mainly as an intermediary representation between the JSON data and the
 * "real" model.
 * @author Christoffer Fink
 */
public class PlanningRequest {

  private Kit kit;
  // The request must have a non-empty list of parts.
  private List<Part> parts;
  // Currently assumes that a wagon hint is provided.
  private Optional<WagonHint> wagonHint;

  public PlanningRequest(Kit kit, List<Part> parts) {
    this(kit, parts, Optional.empty());
  }

  public PlanningRequest(Kit kit, List<Part> parts, WagonHint wagonHint) {
    this(kit, parts, Optional.ofNullable(wagonHint));
  }

  public PlanningRequest(Kit kit, List<Part> parts, Optional<WagonHint> wagonHint) {
    this.kit = kit;
    this.parts = parts;
    this.wagonHint = wagonHint;
  }

  public Optional<WagonHint> wagonHint() {
    return wagonHint;
  }

  public Kit kit() {
    return kit;
  }

  public List<Part> parts() {
    return parts; // TODO: defensive copy
  }

  public Layout getLayout() {
    // XXX: cannot rely on there being a hint in the future.
    Wagon wagon = wagonHint.get().wagon();
    return new Layout(wagon, parts);
  }
}
