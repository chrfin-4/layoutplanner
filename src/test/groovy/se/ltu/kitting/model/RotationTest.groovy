package se.ltu.kitting.model;

import spock.lang.*;
import static se.ltu.kitting.model.Rotation.*;
import static se.ltu.kitting.model.Side.*;
import static se.ltu.kitting.model.Dimensions.dimensions as dim;

class RotationTest extends Specification {

  @Unroll
  def "placing (1,2,3) onto #side should result in #endDim"() {
    expect:
      endDim == Rotation.rotateOntoSide(side, dim(1,2,3))
    where:
      side <<   [left,       right,      back,       front,      bottom,     top]
      endDim << [dim(3,2,1), dim(3,2,1), dim(1,3,2), dim(1,3,2), dim(1,2,3), dim(1,2,3)]
  }

  @Unroll
  def "rotating (1,2,3) by #rot should result in #endDim"() {
    expect:
      endDim == Rotation.rotateZeroOr90Z(rot, dim(1,2,3))
    where:
      rot <<    [ZERO,       Z90]
      endDim << [dim(1,2,3), dim(2,1,3)]
  }

  @Unroll
  def "placing (1,2,3) onto #side should result in #endDim"() {
    given:
      def op = Rotation.rotation(side, rot)
    expect:
      endDim == op.apply(dim(1,2,3))
    where:
      side    | rot  | endDim
      left    | ZERO | dim(3,2,1)
      right   | ZERO | dim(3,2,1)
      back    | ZERO | dim(1,3,2)
      front   | ZERO | dim(1,3,2)
      bottom  | ZERO | dim(1,2,3)
      top     | ZERO | dim(1,2,3)
      left    |  Z90 | dim(2,3,1)
      right   |  Z90 | dim(2,3,1)
      back    |  Z90 | dim(3,1,2)
      front   |  Z90 | dim(3,1,2)
      bottom  |  Z90 | dim(2,1,3)
      top     |  Z90 | dim(2,1,3)
  }

}
