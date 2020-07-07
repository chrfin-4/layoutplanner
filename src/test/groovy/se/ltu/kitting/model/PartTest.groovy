package se.ltu.kitting.model

import spock.lang.*;
import static se.ltu.kitting.model.Side.*;
import static ch.rfin.util.Pair.pair;

class PartTest extends Specification {

  @Unroll
  def "min area side of 100x200x300 is #side when #allowed allowed"() {
    given:
      def part = getPart([100,200,300], allowed)
    expect:
      part.minAreaSide() == side
    where:
      side   | allowed
      bottom | [left, right, back, front, bottom, top]
      top    | [left, right, back, front, top]
      back   | [left, right, back, front]
      front  | [left, right, front]
      left   | [left, right]
      right  | [right]
  }

  @Unroll
  def "min area side of 200x100x300 is #side when #allowed allowed"() {
    given:
      def part = getPart([200,100,300], allowed)
    expect:
      part.minAreaSide() == side
    where:
      side   | allowed
      bottom | [left, right, back, front, bottom, top]
      top    | [left, right, back, front, top]
      left   | [left, right, back, front]
      right  | [right, back, front]
      back   | [back, front]
      front  | [front]
  }

  @Unroll
  def "min area side of 100x300x200 is #side when #allowed allowed"() {
    given:
      def part = getPart([100,300,200], allowed)
    expect:
      part.minAreaSide() == side
    where:
      side   | allowed
      back   | [left, right, back, front, bottom, top]
      front  | [left, right, front, bottom, top]
      bottom | [left, right, bottom, top]
      top    | [left, right, top]
      left   | [left, right]
      right  | [right]
  }

  @Unroll
  def "min area side of 300x100x200 is #side when #allowed allowed"() {
    given:
      def part = getPart([300,100,200], allowed)
    expect:
      part.minAreaSide() == side
    where:
      side   | allowed
      left   | [left, right, back, front, bottom, top]
      right  | [right, back, front, bottom, top]
      bottom | [back, front, bottom, top]
      top    | [back, front, top]
      back   | [back, front]
      front  | [front]
  }

  @Unroll
  def "min area side of 300x200x100 is #side when #allowed allowed"() {
    given:
      def part = getPart([300,200,100], allowed)
    expect:
      part.minAreaSide() == side
    where:
      side   | allowed
      left   | [left, right, back, front, bottom, top]
      right  | [right, back, front, bottom, top]
      back   | [back, front, bottom, top]
      front  | [front, bottom, top]
      bottom | [bottom, top]
      top    | [top]
  }

  @Unroll
  def "min area side of 200x300x100 is #side when #allowed allowed"() {
    given:
      def part = getPart([200,300,100], allowed)
    expect:
      part.minAreaSide() == side
    where:
      side   | allowed
      back   | [left, right, back, front, bottom, top]
      front  | [left, right, front, bottom, top]
      left   | [left, right, bottom, top]
      right  | [right, bottom, top]
      bottom | [bottom, top]
      top    | [top]
  }

  @Unroll
  def "min area side of 100x200x300 is #side when #allowed allowed and #preferred is preferred"() {
    given:
      def part = getPart([100,200,300], allowed, preferred)
    expect:
      part.minAreaSide() == side
    where:
      side   | preferred | allowed
      bottom | bottom    | [left, right, back, front, bottom, top]  // preferred is redundant here
      top    | top       | [left, right, back, front, bottom, top]  // preferred overrides bottom
      bottom | left      | [left, right, back, front, bottom, top]  // left cannot override
      top    | front     | [left, right, back, front, top]          // front cannot override
  }

  def "min area test"() {
    given:
      def part = getPart(size)
    expect:
      part.minArea() == 100*200
    where:
      size << [100,200,300].permutations()
  }

  def "max area test"() {
    given:
      def part = getPart(size)
    expect:
      part.maxArea() == 200*300
    where:
      size << [100,200,300].permutations()
  }

  def "part with no position should return null current region"() {
    given:
      def part = getPart([100,200,300])
    expect:
      part.currentRegion() == null
  }

  def "part with no position should still return null current region after setting a side"() {
    given:
      def part = getPart([100,200,300])
    when:
      part.setSideDown(left)
    then:
      part.currentRegion() == null
  }

  def "part with no position should still return null current region after setting a rotation"() {
    given:
      def part = getPart([100,200,300])
    when:
      part.setRotation(Rotation.Z90)
    then:
      part.currentRegion() == null
  }

  def "part with no position should still return null current region after setting a rotation"() {
    given:
      def part = getPart([100,200,300])
    when:
      part.setRotation(Rotation.Z90)
    then:
      part.currentRegion() == null
  }

  def "part with position should use default side and rotation"() {
    given:
      def part = getPart([100,200,300])
    when:
      part.setPosition(Dimensions.ZERO)
    then:
      part.currentRegion() == pair(Dimensions.ZERO, Dimensions.of(99,199,299))
  }

  def "computing region with position, side, and rotation"() {
    given:
      def part = getPart([100,200,300])
    when:
      part.setPosition(Dimensions.of(200,300,400))  // becomes 300,400,200
      part.setRotation(Rotation.Z90)
      part.setSideDown(left)
    then:
      part.currentRegion() == pair(Dimensions.of(200,300,400), Dimensions.of(399,599,499))
  }

  def "computing region with position, side, and rotation, undo"() {
    given:
      def part = getPart([100,200,300])
      part.setRotation(Rotation.Z90)
      part.setSideDown(left)
    when:
      part.setPosition(Dimensions.of(200,300,400))
      part.setRotation(Rotation.ZERO)
      part.setSideDown(bottom)
    then:
      part.currentRegion() == pair(Dimensions.of(200,300,400), Dimensions.of(299,499,699))
  }


  def getPart(List size) {
    return new Part(1, "1", Dimensions.of(size[0], size[1], size[2]))
  }

  def getPart(List size, Collection allowed) {
    def part = new Part(1, "1", Dimensions.of(size[0], size[1], size[2]))
    part.setAllowedDown(allowed as Set)
    return part
  }

  def getPart(List size, Collection allowed, Side preferred) {
    def part = new Part(1, "1", Dimensions.of(size[0], size[1], size[2]))
    part.setAllowedDown(allowed as Set)
    part.setPreferredDown(preferred)
    return part
  }

}
