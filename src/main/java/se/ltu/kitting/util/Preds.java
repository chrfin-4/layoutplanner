package se.ltu.kitting.util;

import java.util.function.Predicate;
import java.util.function.IntPredicate;

public class Preds {

  public static Predicate<Object> isNull() {
    return Preds::isNull;
  }

  public static boolean isNull(Object o) {
    return o == null;
  }

  public static IntPredicate zero() {
    return Preds::zero;
  }

  public static boolean zero(int i) {
    return i == 0;
  }

  public static IntPredicate positive() {
    return Preds::positive;
  }

  public static boolean positive(int i) {
    return i > 0;
  }

}
