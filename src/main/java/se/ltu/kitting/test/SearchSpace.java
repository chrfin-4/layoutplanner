package se.ltu.kitting.test;

import se.ltu.kitting.model.*;
import java.util.stream.Stream;
import java.math.BigInteger;

/**
 * Performs an intentionally naive calculation of the search space.
 * This is the total number of possible combinations that would have to
 * be explored by an exhaustive search - it takes no constraints into
 * account.
 * Note that this version knows that only 2 rotations are possible.
 * @author Christoffer Fink
 */
public class SearchSpace {
  public static BigInteger sides(final Part p) {
    return BigInteger.valueOf(p.getAllowedDown().size());
  }

  public static BigInteger rotations(final Part p) {
    return BigInteger.valueOf(2);
  }

  public static BigInteger positions(final Layout layout, final Part p) {
    return BigInteger.valueOf(layout.getPositions().size());
  }

  public static BigInteger compute(final Layout layout) {
    BigInteger product = BigInteger.ONE;
    for (final Part p : layout.getParts()) {
      product = product.multiply(sides(p));
      product = product.multiply(rotations(p));
      product = product.multiply(positions(layout, p));
    }
    return product;
  }
}
