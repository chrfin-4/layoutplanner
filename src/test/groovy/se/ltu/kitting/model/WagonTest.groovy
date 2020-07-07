package se.ltu.kitting.model;

import spock.lang.*;
import static se.ltu.kitting.model.Side.*;
import static se.ltu.kitting.model.Dimensions.dimensions as pos;

class WagonTest extends Specification {

  def "the wagon should return all surface positions"() {
    given:
      def surface1 = Surface.of(pos(10,10,5), pos(0,0,0))
      def surface2 = Surface.of(pos(20,10,5), pos(0,0,10))
      def surface3 = Surface.of(pos(10,20,5), pos(0,0,20))
      def wagon = Wagon.of([surface1, surface2, surface3])
      def positions = wagon.allPositions()
    expect:
      positions.contains(pos(0,0,0))
      positions.contains(pos(0,0,10))
      positions.contains(pos(0,0,20))
  }

  def "surfaceOf should return the surface that matches the position"() {
    given:
      def surface1 = Surface.of(pos(10,10,5), pos(0,0,0))
      def surface2 = Surface.of(pos(20,10,5), pos(0,0,10))
      def surface3 = Surface.of(pos(10,20,5), pos(0,0,20))
      def wagon = Wagon.of([surface1, surface2, surface3])
    expect:
      wagon.surfaceOf(pos(0,0,0)) == surface1
      wagon.surfaceOf(pos(5,5,0)) == surface1
      wagon.surfaceOf(pos(0,0,10)) == surface2
      wagon.surfaceOf(pos(0,0,20)) == surface3
      wagon.surfaceOf(pos(9,19,20)) == surface3
  }

}
