package se.ltu.kitting.api;

import java.util.Objects;

/**
 * An error/warning/info message.
 * @author Christoffer Fink
 */
public class Message {

  public enum Severity {
    error, warning, info;
  }

  public final String message;
  public final Severity severity;
  public final String code;

  private Message(final String message, final Severity severity, final String code) {
    this.message = message;
    this.severity = severity;
    this.code = code;
  }

  public static Message info(final String msg) {
    return new Message(msg, Severity.info, "Info");
  }

  public static Message warn(final String msg) {
    return new Message(msg, Severity.warning, "Warning");
  }

  public static Message error(final String msg) {
    return new Message(msg, Severity.error, "Error");
  }

  /**
   * Create an error message using the throwable.
   * The message will be the exception message, and the code is the
   * (simple) exception class name.
   */
  public static Message fromError(final Throwable t) {
    return new Message(t.getMessage(), Severity.error, t.getClass().getSimpleName());
  }

  public Message code(final String code) {
    return new Message(message, severity, code);
  }

  public Message message(final String message) {
    return new Message(message, severity, code);
  }

  public Message severity(final Severity severity) {
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

  @Override
  public String toString() {
    return String.format("[%s] (%s): %s", severity, code, message);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Message)) {
      return false;
    }
    final Message other = (Message) o;
    return this.severity == other.severity
      && Objects.equals(this.code, other.code)
      && Objects.equals(this.message, other.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, severity, code);
  }

}
