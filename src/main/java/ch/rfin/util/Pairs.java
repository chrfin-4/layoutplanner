package ch.rfin.util;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilities for working with pairs.
 *
 * @author Christoffer Fink
 * @version 0.2
 */
public final class Pairs {

  /**
   * Converts a Map.Entry to a pair.
   * @return {@code (key, value)}.
   */
  public static <T,S> Pair<T,S> pairFrom(Map.Entry<T,S> entry) {
    return Pair.of(entry.getKey(), entry.getValue());
  }

  /**
   * Converts a list to a pair.
   * @return {@code (x0, x1)}.
   */
  public static <T> Pair<T,T> pairFrom(List<T> items) {
    return Pair.of(items.get(0), items.get(1));
  }

  // TODO: Decide whether an uneven number of items is really unacceptable.
  /**
   * Converts a list to a list of pairs.
   * @return {@code [(x0, x1), (x2, x3), ...]}.
   * @throws IllegalArgumentException if uneven number of items
   */
  public static <T> List<Pair<T,T>> pairs(List<T> items) {
    final int len = items.size();
    if (len == 0) {
      return Collections.emptyList();
    }
    if (len % 2 != 0) {
      throw new IllegalArgumentException("Illegal argument: " + len + " is not an even number of items");
    }
    List<Pair<T,T>> result = new ArrayList<>(len/2);
    for (int i = 1; i < len; i+=2) {
      result.add(Pair.of(items.get(i-1), items.get(i)));
    }
    return result;
  }

  /**
   * Converts a Map to a list of pairs.
   * @return {@code [(k0, v0), (k1, v1), ...]}.
   */
  public static <K,V> List<Pair<K,V>> pairs(Map<K,V> items) {
    final int len = items.size();
    if (len == 0) {
      return Collections.emptyList();
    }
    return items.entrySet().stream().map(Pairs::pairFrom).collect(Collectors.toList());
  }

}
