package se.ltu.kitting.api;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;
import java.lang.reflect.Type;
import se.ltu.kitting.model.*;
import se.ltu.kitting.api.Messages;

import static java.util.stream.Collectors.toList;

/**
 * Represents the result of trying to solve a layout request.
 * @author Christoffer Fink
 */
public class PlanningResponse {

  private final PlanningRequest request;
  private final Layout solution;
  private final Messages messages;

  private PlanningResponse(PlanningRequest request, Layout solution) {
    this.request = request;
    this.solution = solution;
    this.messages = request.messages();
  }

  public Messages messages() {
    return messages;
  }

  public PlanningRequest request() {
    return request;
  }

  public List<Part> parts() {
    if (solution != null) {
      return solution.getParts();
    }
    return request.parts();
  }

  public Part part(int id) {
    return parts().stream().filter(p -> p.getId() == id).findAny().get();
  }

  public Optional<Layout> solution() {
    return Optional.ofNullable(solution);
  }

  public static PlanningResponse response(PlanningRequest request, Throwable e) {
    return response(request).addMessage(Message.fromError(e));
  }

  public static PlanningResponse response(PlanningRequest request) {
    if (!request.messages().hasErrors()) {
      request.messages().add(Message.error("Could not find a feasible solution.").code("Unsolved"));
    }
    return new PlanningResponse(request, null);
  }

  public static PlanningResponse response(PlanningRequest request, Layout solution) {
    if (!solution.isFeasibleSolution()) {
      return response(request); // Strip out all layouts. Completely fail.
    }
    return new PlanningResponse(request, solution)
      .addMessage(Message.info(String.valueOf(solution.getScore())).code("Score"));
  }

  // FIXME: Needed to allow JsonIO to create a PlanningResponse from JSON. Find a cleaner solution!
  // TODO: include messages
  @Deprecated(forRemoval = true)
  public static PlanningResponse response_(PlanningRequest request, Layout solution) {
    return new PlanningResponse(request, solution);
  }

  /** A solution exists, and it is feasible. */
  public boolean hasFeasibleSolution() {
    return solution != null && solution.isFeasibleSolution();
  }

  // TODO: deprecate?
  public PlanningResponse addMessage(Message msg) {
    messages.add(msg);
    return this;
  }

  @Deprecated(forRemoval = true)
  public Optional<List<Message>> messagesForPart(int id) {
    return messages.partMessages(id);
  }

  @Deprecated(forRemoval = true)
  public Optional<List<Message>> globalMessages() {
    return messages.globalMessages();
  }

}
