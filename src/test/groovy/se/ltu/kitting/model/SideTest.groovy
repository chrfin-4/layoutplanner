package se.ltu.kitting.model

import spock.lang.*;
import static se.ltu.kitting.model.Side.*;

class SideTest extends Specification {

  @Unroll
  def "#side should be opposite of #opposite"() {
    expect:
      side.opposite() == opposite
    where:
      side <<     [left, right, back, front, bottom, top]
      opposite << [right, left, front, back, top, bottom]
  }

  @Unroll
  def "#side1 and #side2 should be equivalent"() {
    expect:
      side1.equivalentTo(side2)
    where:
      side1 << [left, right, back, front, bottom, top]
      side2 << [right, left, front, back, top, bottom]
  }

  @Unroll
  def "#side should be equivalent to itself"() {
    expect:
      side.equivalentTo(side)
    where:
      side << [left, right, back, front, bottom, top]
  }

  @Unroll
  def "#side1 and #side2 should not be equivalent"() {
    expect:
      !side1.equivalentTo(side2)
    where:
      side1 << [left, right,  back, front, bottom, top]
      side2 << [back, bottom, top,  left,  left,   right]
  }

  def "removing redundancies from an empty set should yield an empty set"() {
    expect:
      Side.withoutRedundancy([] as Set).isEmpty()
  }

  @Unroll
  def "removing redundancies from #set should yield the same set"() {
    expect:
      Side.withoutRedundancy(set as Set) == set as Set
    where:
      set << [[right], [front, bottom], [left, back, bottom], [right, front, top]]
  }

  def "removing redundancies from a redundant pair should yield a singleton set"() {
    expect:
      Side.withoutRedundancy(set as Set).size() == 1
    where:
      set << [[left, right], [back, front], [bottom, top]]
  }

  def "removing redundancies from a set of all sides should yield a set with 3 sides"() {
    given:
      def allSides = Side.all()
      def reduced = Side.withoutRedundancy(allSides)
    expect:
      reduced.size() == 3
      !reduced.any({ reduced.contains(it.opposite()) })
  }

  @Unroll
  def "#sides should be normalized to #norm"() {
    expect:
      Side.normalize(sides as Set) == norm as Set
    where:
      sides << [[], [top], [left, back], [right, left], all()]
      norm  << [[], [top], [left, back], [left],  canonical()]
  }

  @Unroll
  def "getting opposites of #sides should yield #opposites"() {
    expect:
      Side.opposites(sides as Set) == opposites as Set
    where:
      sides <<     [[], [right], [back, top],   [left, back, bottom], [left, right], all()]
      opposites << [[], [left], [front, bottom], [right, front, top], [left, right], all()]
  }

}
