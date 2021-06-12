package se.ltu.kitting.test;

import se.ltu.kitting.model.*;
import java.math.BigInteger;

/**
 * Compute the size of the search space.
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
