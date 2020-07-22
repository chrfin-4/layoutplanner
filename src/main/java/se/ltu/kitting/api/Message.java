package se.ltu.kitting.api;

/**
 * An error/warning/info message.
 * @author Christoffer Fink
 */
public class Message {

  public enum Severity {
    error, warning, info;
  }

  private String message;
  private Severity severity;
  private String code;

  private Message(String message, Severity severity, String code) {
    this.message = message;
    this.severity = severity;
    this.code = code;
  }

  public static Message info(String msg) {
    return new Message(msg, Severity.info, null);
  }

  public static Message warn(String msg) {
    return new Message(msg, Severity.warning, null);
  }

  public static Message error(String msg) {
    return new Message(msg, Severity.error, null);
  }

  public static Message fromError(Throwable t) {
    return new Message(t.getMessage(), Severity.error, t.getClass().getSimpleName());
  }

  public Message code(String code) {
    return new Message(message, severity, code);
  }
  
  public Severity severity() {
    return severity;
  }

}
