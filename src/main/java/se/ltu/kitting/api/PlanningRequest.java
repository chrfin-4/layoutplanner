package se.ltu.kitting.api;

import java.util.*;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Kit;
import se.ltu.kitting.model.Wagon;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.WagonHint;

/**
 * Represents an incoming request and holds all parameters.
 * Mainly as an intermediary representation between the JSON data and the
 * "real" model.
 * @author Christoffer Fink
 */
public class PlanningRequest {

  private final Kit kit;
  // The request must have a non-empty list of parts.
  private final List<Part> parts;
  // Currently assumes that a wagon hint is provided.
  private final Optional<WagonHint> wagonHint;

  private final Messages messages;

  public PlanningRequest(Kit kit, List<Part> parts) {
    this(kit, parts, Optional.empty());
  }

  public PlanningRequest(Kit kit, List<Part> parts, WagonHint wagonHint) {
    this(kit, parts, Optional.ofNullable(wagonHint));
  }

  public PlanningRequest(Kit kit, List<Part> parts, Optional<WagonHint> wagonHint) {
    this(kit, parts, wagonHint, new Messages());
  }

  public PlanningRequest(Kit kit, List<Part> parts, Optional<WagonHint> wagonHint, Messages messages) {
    this.kit = kit;
    this.parts = parts;
    this.wagonHint = wagonHint;
    this.messages = messages;
  }

  public PlanningRequest withMessages(Messages messages) {
    return new PlanningRequest(kit, parts, wagonHint, messages);
  }

  public Optional<WagonHint> wagonHint() {
    return wagonHint;
  }

  public Kit kit() {
    return kit;
  }

  public List<Part> parts() {
    return List.copyOf(parts); // TODO: defensive copy
  }

  public Layout getLayout() {
    // XXX: cannot rely on there being a hint in the future.
    Wagon wagon = wagonHint.get().wagon();
    return new Layout(wagon, parts);
  }

  public Messages messages() {
    return messages;
  }

}
