package se.ltu.kitting.model;

/**
 * A side of a part. Implicitly represents rotation by specifying which of its
 * six sides the part is resting on (which side is facing downward).
 */
public enum Side {
  left, right, back, front, bottom, top;
}
