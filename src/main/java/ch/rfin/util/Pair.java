package ch.rfin.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * A Pair of values. This class is immutable (and therefore thread safe) if
 * T1 and T2 are immutable.
 *
 * @author Christoffer Fink
 * @version 0.2
 */
public final class Pair<T1,T2> {

  public final T1 _1;
  public final T2 _2;

  // TODO: Consider making public for better interoperability with Groovy.
  private Pair(T1 first, T2 second) {
    this._1 = first;
    this._2 = second;
  }

  /** Factory method. */
  public static <T1,T2> Pair<T1,T2> of(T1 first, T2 second) {
    return new Pair<>(first, second);
  }

  /**
   * More descriptive alias for {@link #of(Object, Object)}; useful for
   * static imports.
   */
  public static <T1,T2> Pair<T1,T2> pair(T1 first, T2 second) {
    return new Pair<>(first, second);
  }

  /** {@code (a,b).get_1() = a}. **/
  public T1 get_1() {
    return _1;
  }

  /** {@code (a,b).get_2() = b}. **/
  public T2 get_2() {
    return _2;
  }

  /** {@code (a,b).with_1(c) = (c,b)} */
  public <U> Pair<U,T2> with_1(U newFirst) {
    return of(newFirst, _2);
  }

  /** {@code (a,b).with_2(c) = (a,c)} */
  public <U> Pair<T1,U> with_2(U newSecond) {
    return of(_1, newSecond);
  }

  /** {@code (a,b).swap() = (b,a)} */
  public Pair<T2,T1> swap() {
    return of(_2, _1);
  }

  /**
   * Allows {@code q = p.map_1(f);} instead of
   * {@code q = Pair.of(f.apply(p._1), p._2)}.
   */
  public <U> Pair<U,T2> map_1(Function<T1,U> f) {
    return of(f.apply(_1), _2);
  }

  /**
   * Allows {@code q = p.map_2(f);} instead of
   * {@code q = Pair.of(p._1, f.apply(p._2))}.
   */
  public <U> Pair<T1,U> map_2(Function<T2,U> f) {
    return of(_1, f.apply(_2));
  }

  /**
   * Allows {@code q = p.map(f, g);} instead of
   * {@code q = Pair.of(f.apply(p._1), g.apply(p._2))}.
   */
  public <U,R> Pair<U,R> map(Function<T1,U> f, Function<T2,R> g) {
    return of(f.apply(_1), g.apply(_2));
  }

  @Override
  public String toString() {
    return "(" + _1 + ", " + _2 + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    Pair<?,?> that = (Pair)o;
    return Objects.deepEquals(_1, that._1) && Objects.deepEquals(_2, that._2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_1, _2);
  }

}
