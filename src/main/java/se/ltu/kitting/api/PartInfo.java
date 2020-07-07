package se.ltu.kitting.api;

import java.util.*;

public class PartInfo {

  // Optional.
  private Optional<String> partDesc;
  private Optional<String> functionGroup;
  private Collection<String> requiredCapabilities;
  private Optional<LayoutHint> layoutHint;

  public PartInfo(String desc, String func, Collection<String> cap, LayoutHint hint) {
    this.partDesc = Optional.ofNullable(desc);
    this.functionGroup = Optional.ofNullable(func);
    this.layoutHint = Optional.ofNullable(hint);
    this.requiredCapabilities = cap == null ? Set.of() : Set.copyOf(cap);
  }

  public Optional<String> partDesc() {
    return partDesc;
  }

  public Optional<String> functionGroup() {
    return functionGroup;
  }

  public Optional<LayoutHint> layoutHint() {
    return layoutHint;
  }

  public Collection<String> requiredCapabilities() {
    return requiredCapabilities;
  }

}
