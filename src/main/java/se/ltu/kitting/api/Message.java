package se.ltu.kitting.api;

/**
 * An error/warning/info message.
 * @author Christoffer Fink
 */
public class Message {

  public enum Severity {
    error, warning, info;
  }

  public String message;
  public Severity severity;
  public String code;

  private Message(String message, Severity severity, String code) {
    this.message = message;
    this.severity = severity;
    this.code = code;
  }

  public static Message info(String msg) {
    return new Message(msg, Severity.info, "Info");
  }

  public static Message warn(String msg) {
    return new Message(msg, Severity.warning, "Warning");
  }

  public static Message error(String msg) {
    return new Message(msg, Severity.error, "Error");
  }

  public static Message fromError(Throwable t) {
    return new Message(t.getMessage(), Severity.error, t.getClass().getSimpleName());
  }

  public Message code(String code) {
    return new Message(message, severity, code);
  }

  public String message() {
    return message;
  }

  public Severity severity() {
    return severity;
  }

  public String code() {
    return code;
  }

  public boolean isError() {
    return severity == Severity.error;
  }

  public boolean isWarning() {
    return severity == Severity.warning;
  }

  public boolean isInfo() {
    return severity == Severity.info;
  }

}
