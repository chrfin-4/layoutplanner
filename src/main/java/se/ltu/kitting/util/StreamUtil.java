package se.ltu.kitting.util;

import java.util.stream.Stream;
import java.util.Iterator;
import ch.rfin.util.Pair;
import ch.rfin.java.util.function.Fun;

/**
 * Some tools for working with Streams.
 * @author Christoffer Fink
 */
public class StreamUtil {

  /** Make a stream out of an Iterable. */
  public static <T> Stream<T> stream(Iterable<T> it) {
    Iterator<T> iter = it.iterator();
    if (!iter.hasNext()) {
      return Stream.empty();
    }
    return Stream.iterate(iter.next(), t -> iter.hasNext(), t -> iter.next());
  }

  /**
   * Takes an item and returns a function that will take a second item and
   * return a pair with the parameter to this function first and the parameter
   * to the returned function second: f → (s → (f,s)).
   * This is essentially just the Pair factory method curried with the first
   * item.
   */
  public static <T,S> Fun<S,Pair<T,S>> pairWithFirst(T first) {
    return s -> Pair.of(first, s);
  }

  /**
   * Takes an item and returns a function that will take a second item and
   * return a pair with the parameter to this function second and the parameter
   * to the returned function first: s → (f → (f,s)).
   */
  public static <T,S> Fun<T,Pair<T,S>> pairWithSecond(S second) {
    return t -> Pair.of(t, second);
  }

  /**
   * Takes an item s and several items [f1, f2, ...] and returns a stream of
   * [(f1,s), (f2,s), ...] pairs.
   * Pair up a single item with multiple other items, putting the single item
   * as the second in the pair.
   */
  public static <T,S> Stream<Pair<T,S>> pairsWithSecond(S second, Iterable<T> firsts) {
    return pairsWithFirst(second, firsts).map(Pair::swap);
  }

  /**
   * Takes an item f and several items [s1, s2, ...] and returns a stream of
   * [(f,s1), (f,s2), ...] pairs.
   * Pair up a single item with multiple other items, putting the single item
   * as the first in the pair.
   */
  public static <T,S> Stream<Pair<T,S>> pairsWithFirst(T first, Iterable<S> seconds) {
    return stream(seconds).map(s -> Pair.of(first, s));
  }

}
