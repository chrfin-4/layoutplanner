package se.ltu.kitting.api;

import java.util.*;
import se.ltu.kitting.model.Part;

/**
 * Holds all info/warning/error messages that are accumulated while processing
 * a request to produce a response.
 * Note, instances of this class are mutable!
 * @author Christoffer Fink
 */
public class Messages {

  private final List<Message> globalMessages = new ArrayList<>();
  private final Map<Integer,List<Message>> partMessages = new HashMap<>();
  private boolean hasErrors = false;

  /** Associate a message with the ID of this part. */
  public Messages addMessage(Part part, Message msg) {
    return addMessage(part.getId(), msg);
  }

  /** Associate a message with the part ID. */
  public Messages addMessage(int partId, Message msg) {
    if (!partMessages.containsKey(partId)) {
      partMessages.put(partId, new ArrayList<>());
    }
    partMessages.get(partId).add(msg);
    updateHasErrors(msg);
    return this;
  }

  /** Add a global message. */
  public Messages addMessage(Message msg) {
    globalMessages.add(msg);
    updateHasErrors(msg);
    return this;
  }

  public Optional<List<Message>> globalMessages() {
    if (globalMessages.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(List.copyOf(globalMessages));
  }

  public Map<Integer,List<Message>> partMessages() {
    return Map.copyOf(partMessages);
  }

  public Optional<List<Message>> partMessages(int partId) {
    if (partMessages.containsKey(partId)) {
      return Optional.of(List.copyOf(partMessages.get(partId)));
    }
    return Optional.empty();
  }

  /** Get all part IDs for which there are messages. */
  public Collection<Integer> partsWithMessages() {
    return partMessages.keySet();
  }

  /**
   * Has ANY error message been added? Includes part-specific as well as gobal
   * messages.
   */
  public boolean hasErrors() {
    return hasErrors;
  }

  /** Are there any global messages? */
  public boolean hasGlobalMessages() {
    return !globalMessages.isEmpty();
  }

  /** Do any parts have messages? */
  public boolean hasPartMessages() {
    return !partMessages.isEmpty();
  }

  private void updateHasErrors(Message msg) {
    hasErrors = hasErrors || msg.isError();
  }

}
