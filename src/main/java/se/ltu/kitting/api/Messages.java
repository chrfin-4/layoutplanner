package se.ltu.kitting.api;

import java.util.*;

// Deprecated.
import se.ltu.kitting.model.Part;

/**
 * Holds all info/warning/error messages that are accumulated while processing
 * a request to produce a response.
 * Note, instances of this class are mutable!
 * However, they can only be mutated through the {@code add(...)} methods.
 * The methods that return messages do not leak references.
 * @author Christoffer Fink
 */
public class Messages {

  private final List<Message> globalMessages = new ArrayList<>();
  private final Map<Integer,List<Message>> partMessages = new HashMap<>();
  private boolean hasErrors = false;
  private boolean hasWarnings = false;

  public static Messages empty() {
      return new Messages();
  }

  @Deprecated(forRemoval = true)
  public Messages addMessage(Part part, Message msg) {
    return addMessage(part.getId(), msg);
  }

  @Deprecated(forRemoval = true)
  public Messages addMessage(int partId, Message msg) {
    return add(partId, msg);
  }

  @Deprecated(forRemoval = true)
  public Messages addMessage(Message msg) {
    return add(msg);
  }

  /** Add a global message. */
  public Messages add(Message msg) {
    globalMessages.add(msg);
    updateSeverityFlags(msg);
    return this;
  }

  /** Associate a message with the part ID. */
  public Messages add(int partId, Message msg) {
    if (!partMessages.containsKey(partId)) {
      partMessages.put(partId, new ArrayList<>());
    }
    partMessages.get(partId).add(msg);
    updateSeverityFlags(msg);
    return this;
  }

  // FIXME: should return an empty list rather than an optional
  @Deprecated
  public Optional<List<Message>> globalMessages() {
    if (globalMessages.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(List.copyOf(globalMessages));
  }

  /**
   * A map mapping part IDs to a list of messages.
   * Note that there is no guarantee that a certain part ID has any messages
   * associated with it. In that case, the ID doesn't map to anything.
   * (So {@code get(id)} will return null.)
   */
  public Map<Integer,List<Message>> partMessages() {
    return Map.copyOf(partMessages);
  }

  // FIXME: should return an empty list rather than an optional
  @Deprecated
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

  public Collection<Message> allMessages() {
    final var messages = new ArrayList<Message>();
    messages.addAll(globalMessages);
    partMessages.values().forEach(messages::addAll);
    return messages;
  }

  /**
   * Has ANY error message been added? Includes part-specific as well as gobal
   * messages.
   */
  public boolean hasErrors() {
    return hasErrors;
  }

  /**
   * Has ANY warning message been added? Includes part-specific as well as gobal
   * messages.
   */
  public boolean hasWarnings() {
    return hasWarnings;
  }

  /** Are there any global messages? */
  public boolean hasGlobalMessages() {
    return !globalMessages.isEmpty();
  }

  /** Do any parts have messages? */
  public boolean hasPartMessages() {
    return !partMessages.isEmpty();
  }

  /** Add all messages from the {@code other} Messages object. */
  public Messages merge(Messages other) {
    if (other == null) {
      return this;
    }
    other.globalMessages.forEach(this::add);
    other.partMessages.forEach((i, list) -> list.forEach(msg -> add(i, msg)));
    return this;
  }

  private void updateSeverityFlags(Message msg) {
    hasErrors = hasErrors || msg.isError();
    hasWarnings = hasWarnings || msg.isWarning();
  }

}
