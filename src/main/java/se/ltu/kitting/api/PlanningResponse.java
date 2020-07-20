package se.ltu.kitting.api;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;
import java.lang.reflect.Type;
import se.ltu.kitting.model.*;

import static java.util.stream.Collectors.toList;

/**
 * Represents the result of trying to solve a layout request.
 * @author Christoffer Fink
 */
public class PlanningResponse {

  private PlanningRequest request;
  private Layout solution;
  private List<Message> globalMessages;
  private Map<Integer,List<Message>> partMessages = new HashMap<>();

  private PlanningResponse(PlanningRequest request, Layout solution) {
    this.request = request;
    this.solution = solution;
  }

  public PlanningResponse addMessage(Part part, Message msg) {
    return addMessage(part.getId(), msg);
  }

  public PlanningResponse addMessage(int partId, Message msg) {
    if (!partMessages.containsKey(partId)) {
      partMessages.put(partId, new ArrayList<>());
    }
    partMessages.get(partId).add(msg);
    return this;
  }

  public PlanningResponse addMessage(Message msg) {
    if (globalMessages == null) {
      globalMessages = new ArrayList<>();
    }
    globalMessages.add(msg);
    return this;
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

  public Optional<List<Message>> globalMessages() {
    return Optional.ofNullable(globalMessages);
  }

  public Optional<List<Message>> messagesForPart(int id) {
    if (partMessages.containsKey(id)) {
      return Optional.of(List.copyOf(partMessages.get(id)));
    }
    return Optional.empty();
  }

  public Optional<Layout> solution() {
    return Optional.ofNullable(solution);
  }

  public static PlanningResponse fromLayout(PlanningRequest request, se.ltu.kitting.model.Layout layout) {
    return fromLayout(request, layout, Optional.empty(), Optional.empty());
  }

  public static PlanningResponse fromLayout(PlanningRequest request,
      se.ltu.kitting.model.Layout layout,
      Optional<List<Message>> globalMessages,
      Optional<Map<Integer,List<Message>>> partMessages) {
    var response = new PlanningResponse(request, layout);
    if (globalMessages.isPresent()) {
      globalMessages.get().forEach(response::addMessage);
    }
    if (partMessages.isPresent()) {
      Map<Integer,List<Message>> pm = partMessages.get();
      pm.forEach((id,list) -> list.forEach(msg -> response.addMessage(id, msg)));
    }
    response.addMessage(Message.info(String.valueOf(layout.getScore())).code("Score"));
    return response;
  }

  public static PlanningResponse fromError(PlanningRequest request, Throwable e) {
    return new PlanningResponse(request, null).addMessage(Message.fromError(e));
  }
  
  public static PlanningResponse fromError(PlanningRequest request,
      Throwable e,
      Optional<List<Message>> globalMessages,
      Optional<Map<Integer,List<Message>>> partMessages) {
    var response = new PlanningResponse(request, null);
    if (globalMessages.isPresent()) {
      globalMessages.get().forEach(response::addMessage);
    }
    if (partMessages.isPresent()) {
      Map<Integer,List<Message>> pm = partMessages.get();
      pm.forEach((id,list) -> list.forEach(msg -> response.addMessage(id, msg)));
    }
    return response.addMessage(Message.fromError(e));
  }
}
