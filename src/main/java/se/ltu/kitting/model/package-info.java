/**
 * Definitions used in the model.
 * <p>
 * The sides of a part/cuboid are:
 * </p>
 * <ul>
 *   <li>Left = smallest x coordinate</li>
 *   <li>Right = largest x coordinate</li>
 *   <li>Back = smallest y coordinate</li>
 *   <li>Front = largest y coordinate</li>
 *   <li>Bot = smallest z coordinate</li>
 *   <li>Top = largest z coordinate</li>
 * </ul>
 *
 * <p>
 * When representing the cuboid bounding a part as two points, they are always
 * ((left,back,bot), (right,front,left)). In other words, the point with the
 * smallest coordinates first and the point with the largest coordinates second.
 * </p>
 */
package se.ltu.kitting.model;
