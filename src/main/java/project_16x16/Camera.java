package project_16x16;

import org.gicentre.utils.move.ZoomPan;

import project_16x16.objects.EditableObject;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;

/**
 * Camera class. Extends {@link org.gicentre.utils.move.ZoomPan ZoomPan},
 * offering some bespoke methods relating to Project-16x16. At the moment, the
 * camera uses {@link PApplet#lerp(float, float, float) lerp()} to follow
 * objects or go to target position.
 * 
 * @todo deadzone mode can be choppy when tracking; zoom-to-fit (multiple
 *       entities); setting position to mouse when camera is rotated [bugged]
 * @author micycle1
 * @see {@link org.gicentre.utils.move.ZoomPan ZoomPan}
 * @see {@link #update() run()} - the main method
 */
public final class Camera extends ZoomPan {

	private SideScroller applet;
	/**
	 * Lerp constant for motion. Used for all motion easing (zoom, position and
	 * rotation).
	 */
	private float lerpSpeed = 0.05f;
	private float zoom = 1.0f;
	/**
	 * Target variables are used as the target for lerping.
	 */
	private float zoomTarget = 1.0f, rotationTarget = 0;
	private float rotation = 0, shakeRotationOffset = 0;
	/**
	 * Used as the target position for camera position lerping.
	 */
	private PVector targetPosition;
	/**
	 * Logical camera position: takes into account offset and negative coordinates.
	 * This is the world coordinate which the camera is centered on. Differs from
	 * {@link #getPanOffset()}.
	 */
	private PVector logicalPosition;
	/**
	 * Camera position offset. Used to centre the world coordinate (0,0) in the
	 * middle of the screen (since Processing (0,0) is top-right corner. Used
	 * internally.
	 */
	private PVector offset;
	private PVector followObjectOffset = new PVector(0, 0);
	/**
	 * Specifies a camera translation offset when shaking (where it is randomised
	 * each frame). Used internally.
	 */
	private PVector shakeOffset = new PVector(0, 0);
	/**
	 * Deadzone coordinate points. Can correspond to either screen or world
	 * coordinates, depending on which method was used to set them
	 * ({@link #setWorldDeadZone()} vs {@link #setScreenDeadZone()}.
	 */
	private PVector deadZoneP1, deadZoneP2;
	private boolean following = false, deadZoneScreen = false, deadZoneWorld = false;
	/**
	 * Internal variable. Used when deadZone is toggled from off to on to restore
	 * the correct type.
	 */
	private int deadZoneTypeLast = 0;
	/**
	 * Object that the camera is tracking (if {@link #following} is True).
	 */
	private EditableObject followObject;
	private float zoomMax = 100, zoomMin = 0;
	/**
	 * Trauma is used internally to inform the magnitude of camera shake.
	 */
	private float trauma = 0, traumaDecay = 0.015f;

	/**
	 * The most basic constructor. Initialises the camera at position (0, 0).
	 * 
	 * @param applet Target applet ({@link SideScroller}).
	 */
	public Camera(SideScroller applet) {
		super(applet);
		this.applet = applet;
		targetPosition = new PVector(0, 0); // default position
	}

	/**
	 * Constructor. Specify camera's initial fixed position.
	 * 
	 * @param applet        Target applet ({@link SideScroller}).
	 * @param startPosition Initial camera position.
	 */
	public Camera(SideScroller applet, PVector startPosition) {
		super(applet);
		this.applet = applet;
		targetPosition = new PVector(-startPosition.x, -startPosition.y);
	}

	/**
	 * Constructor. Specify the object to track from initialisation.
	 * 
	 * @param applet       Target applet ({@link SideScroller}).
	 * @param followObject Object the camera will follow.
	 */
	public Camera(SideScroller applet, EditableObject followObject) {
		super(applet);
		this.applet = applet;
		this.followObject = followObject;
		following = true;
	}

	/**
	 * Constructor. Specify both the object to track and the translation offset with
	 * which to track it from initialisation.
	 * 
	 * @param applet       Target applet ({@link SideScroller}).
	 * @param followObject Object the camera will follow.
	 * @param offset       Offset with which the camera will follow the given
	 *                     object.
	 */
	public Camera(SideScroller applet, EditableObject followObject, PVector followOffset) {
		super(applet);
		this.applet = applet;
		this.followObject = followObject;
		following = true;
		followObjectOffset = followOffset.copy();
	}

	/**
	 * Draws camera debug info.
	 */
	public void post() {

		applet.noFill();
		applet.stroke(0, 150, 255);
		applet.strokeWeight(2);
		final int length = 20;
		applet.line(applet.width / 2 - length, applet.height / 2, applet.width / 2 + length, applet.height / 2);
		applet.line(applet.width / 2, applet.height / 2 - length, applet.width / 2, applet.height / 2 + length);
		applet.pushMatrix();
		applet.translate(offset.x, offset.y);
		applet.rotate(rotation);
		applet.translate(-offset.x, -offset.y);
		applet.line(applet.width / 2 - length * 2, applet.height / 2, applet.width / 2 + length * 2, applet.height / 2);
		applet.popMatrix();
		if (following) {
			applet.rectMode(PApplet.CENTER);
			applet.rect(getCoordToDisp(followObject.pos).x, getCoordToDisp(followObject.pos).y, length * 2, length * 2);
			applet.strokeWeight(PApplet.map(PApplet.dist(applet.width / 2, applet.height / 2,
					getCoordToDisp(followObject.pos).x, getCoordToDisp(followObject.pos).y), 0, 100, 2, 10));
			applet.line(applet.width / 2, applet.height / 2, getCoordToDisp(followObject.pos).x,
					getCoordToDisp(followObject.pos).y);
			if (deadZoneScreen) {
				applet.rectMode(PApplet.CORNER);
				applet.rect(deadZoneP1.x, deadZoneP1.y, deadZoneP2.x - deadZoneP1.x, deadZoneP2.y - deadZoneP1.y);
				applet.rectMode(PApplet.CENTER);
			}
		}
	}

	/**
	 * Hooks the camera to the PApplet; anything drawn in Processing after hook() is
	 * called will be affected by the camera.
	 * <p>
	 * If you wish for the entire sketch to be affected by the camera, you can call
	 * this method in the first line of the {@link SideScroller#Draw draw()} method.
	 * This results in all subsequent drawing being affected by the camera.
	 * Occasionally though there may be a need to have some display activity that is
	 * independent of the camera -- see {@link #release()}.
	 * 
	 * @see #release()
	 */
	public void hook() {
		applet.pushMatrix();
		update();
	}

	/**
	 * Releases the {@link #hook()}; anything drawn in Processing after release() is
	 * called will not be affected by the camera. Call this to display legends,
	 * annotations and 'heads-up displays', or any other graphics that need to be
	 * overlaid on top of the zoomed graphics.
	 * 
	 * @see #hook()
	 */
	public void release() {
		applet.popMatrix();
	}

	/**
	 * Updates the camera - this method is the heart of the {@link Camera} class.
	 * 
	 * @see {@linkplain ZoomPan#transform() transform()}
	 */
	private void update() {
		offset = new PVector(applet.width / 2, applet.height / 2);

		if (zoom != zoomTarget) {
			zoom = PApplet.lerp(zoom, zoomTarget, lerpSpeed);
			if (PApplet.abs(zoom - zoomTarget) < 0.0025) { //
				zoom = zoomTarget;
			}
		}

		rotation = PApplet.lerp(rotation, rotationTarget, lerpSpeed);
		applet.translate(offset.x, offset.y);
		applet.rotate(rotation + shakeRotationOffset);
		applet.translate(-offset.x, -offset.y);
		transform();

		float scale = PApplet.lerp((float) getZoomScaleX(), zoom, lerpSpeed);
		setZoomScaleX(scale);
		setZoomScaleY(scale);

		if (following && ((deadZoneScreen && !withinScreenDeadZone()) || ((deadZoneWorld && !withinWorldDeadZone()))
				|| (!deadZoneScreen && !deadZoneWorld))) {
			setPanOffset(
					PApplet.lerp(getPanOffset().x, ((-followObject.pos.x - followObjectOffset.x + offset.x) * zoom),
							lerpSpeed) - shakeOffset.x,
					PApplet.lerp(getPanOffset().y, ((-followObject.pos.y - followObjectOffset.y + offset.y) * zoom),
							lerpSpeed) - shakeOffset.y);
		} else if (!following) {
			setPanOffset(
					PApplet.lerp(getPanOffset().x, ((targetPosition.x + offset.x) * zoom), lerpSpeed) - shakeOffset.x,
					PApplet.lerp(getPanOffset().y, ((targetPosition.y + offset.y) * zoom), lerpSpeed) - shakeOffset.y);
		}
		logicalPosition = PVector.mult(PVector.sub(getPanOffset(), offset), -1);

		if (trauma > 0) { // 50 and 0.35 seem suitable values
			trauma -= traumaDecay;

			float x = (trauma * trauma) * applet.random(-1, 1) * 50;
			float y = (trauma * trauma) * applet.random(-1, 1) * 50;
			shakeOffset = new PVector(x, y);
			shakeRotationOffset = (trauma * trauma) * applet.random(-1, 1) * 0.35f;
			if (trauma == 0) {
				shakeOffset = new PVector(0, 0);
				shakeRotationOffset = 0;
			}
		}
	}

	/**
	 * Debug info for worldDeadZone area (since it is part of the world, it cannot
	 * be drawn above the camera and must be called after).
	 */
	public void postDebug() {
		if (deadZoneWorld) {
			applet.noFill();
			applet.stroke(0, 150, 255);
			applet.strokeWeight(2);
			applet.rectMode(PApplet.CORNER);
			applet.rect(deadZoneP1.x, deadZoneP1.y, deadZoneP2.x - deadZoneP1.x, deadZoneP2.y - deadZoneP1.y);
			applet.rectMode(PApplet.CENTER);
		}
	}

	/**
	 * Tells the camera which object to track/follow. Retains the previous offset
	 * (if any).
	 * 
	 * @param o Object to track.
	 */
	public void setFollowObject(EditableObject o) {
		followObject = o;
		following = true;
		rotationTarget = 0;
	}

	/**
	 * Tells the camera which object to track/follow, given a new offset.
	 * 
	 * @param o      Object to track.
	 * @param offset Offset with which the camera will follow the given object.
	 */
	public void setFollowObject(EditableObject o, PVector offset) {
		followObject = o;
		followObjectOffset = offset.copy();
		following = true;
		rotationTarget = 0;
	}

	/**
	 * Modify the existing object tracking offset.
	 * 
	 * @param followObjectOffset new offset
	 */
	public void setFollowObjectOffset(PVector followObjectOffset) {
		this.followObjectOffset = followObjectOffset.copy();
	}

	/**
	 * Shakes the camera around current position. Force is additive, so successive
	 * shakes produce more camera shaking.
	 * 
	 * @param force shaking force (should be at most 1).
	 */
	public void shake(float force) { // todo
		trauma = PApplet.min(1, trauma + force);
	}

	/**
	 * Defines the screen region in which the tracked object can move without the
	 * camera following. When the tracked object exits the region, the camera will
	 * track the object until it returns within the region.
	 * 
	 * @param point1 Coordinate 1 (Screen coordinate)
	 * @param point2 Coordinate 2 (Screen coordinate - the point opposite point1)
	 * @see {@link #setWorldDeadZone(PVector, PVector) setWorldDeadZone()}
	 */
	public void setScreenDeadZone(PVector point1, PVector point2) {
		deadZoneScreen = true;
		deadZoneWorld = false;
		deadZoneTypeLast = 0;
		deadZoneP1 = point1.copy();
		deadZoneP2 = point2.copy();
	}

	/**
	 * Determines whether the following-object is within the screen deadzone.
	 * 
	 * @return
	 */
	private boolean withinScreenDeadZone() {
		PVector coord = getCoordToDisp(followObject.pos); // object screen coord
		return withinRegion(coord, deadZoneP1, deadZoneP2);
	}

	/**
	 * Defines the game world region in which the tracked object can move without
	 * the camera following. When the tracked object exits the region, the camera
	 * will track the object until it returns within the region.
	 * 
	 * @param point1 Coordinate 1 (Screen coordinate)
	 * @param point2 Coordinate 2 (Screen coordinate - the point opposite point1)
	 * @see {@link #setScreenDeadZone(PVector, PVector) setScreenDeadZone()}
	 */
	public void setWorldDeadZone(PVector point1, PVector point2) {
		deadZoneWorld = true;
		deadZoneScreen = false;
		deadZoneTypeLast = 1;
		deadZoneP1 = point1.copy();
		deadZoneP2 = point2.copy();
	}

	/**
	 * Determines whether the following-object is within the world deadzone.
	 * 
	 * @return
	 */
	private boolean withinWorldDeadZone() {
		PVector coord = followObject.pos;
		return withinRegion(coord, deadZoneP1, deadZoneP2);
	}

	/**
	 * Toggles the most recently assigned deadzone inactive/active.
	 */
	public void toggleDeadZone() {
		if (deadZoneP1 != null && deadZoneP2 != null) {
			if (deadZoneTypeLast == 0) { // 0 is screen
				deadZoneScreen = !deadZoneScreen;
			}
			if (deadZoneTypeLast == 1) { // 1 is world
				deadZoneWorld = !deadZoneWorld;
			}

		} else {
			System.err.print("Specify a deadzone first");
		}
	}

	/**
	 * Sets camera position. Takes precedence over the follow object (if any). Could
	 * be used to reveal a boss, then snap back to the player.
	 * 
	 * @param position World position camera will center on.
	 * @see {@link ZoomPan#getDispToCoord(PVector) getDispToCoord()}
	 */
	public void setCameraPosition(PVector position) {
		following = false;
		this.targetPosition = new PVector(-position.x, -position.y);
	}
	
	/**
	 * Sets camera position immediately (no lerping towards target position).
	 * 
	 * @param position World position camera will center on.
	 * @see #setCameraPosition(PVector)
	 */
	public void setCameraPositionNoLerp(PVector position) {
		following = false;
		PVector temp = PVector.sub(position, offset);
		this.setPanOffset(-temp.x, -temp.y);
		this.targetPosition = new PVector(-position.x, -position.y);
	}

	/**
	 * Set camera rotation (the camera rotates around the camera position - not a
	 * world position).
	 * 
	 * @param angle Rotation angle (in radians)
	 */
	public void setRotation(float angle) {
		rotationTarget = angle;
	}

	/**
	 * Modify the existing rotation.
	 * 
	 * @param angle Rotation angle (in radians)
	 */
	public void rotate(float angle) {
		rotationTarget += angle;
	}

	@Override
	public void setZoomScale(double zoomScale) {
		zoomTarget = (float) zoomScale;
	}

	@Override
	public void setMinZoomScale(double minZoomScale) {
		zoomMin = (float) minZoomScale;
	}

	@Override
	public void setMaxZoomScale(double maxZoomScale) {
		zoomMax = (float) maxZoomScale;
	}

	public void zoomIn(float amount) {
		zoomTarget = PApplet.min(zoomTarget + amount, zoomMax);
	}

	public void zoomOut(float amount) {
		zoomTarget = PApplet.max(zoomTarget - amount, zoomMin);
	}

	/**
	 * Specify lerp (linear interpolation) speed for camera motion. Default = 0.05.
	 * Since the lerp is calculated per-frame (after prior motion), the camera
	 * motion is effectively non-linear. Smaller values provide a smoother, less
	 * snappy, slower camera.
	 * 
	 * @param lerpSpeed Range = [0-1.0]
	 * @see {@link PApplet#lerp(float, float, float) lerp()}
	 */
	public void setLerpSpeed(float lerpSpeed) {
		this.lerpSpeed = lerpSpeed;
	}

	/**
	 * Determine if a point is within rectangular region.
	 * 
	 * @param point PVector position to test.
	 * @param UL    Corner one of region.
	 * @param BR    Corner two of region (different X & Y).
	 * @return True if point contained in region.
	 */
	private static boolean withinRegion(PVector point, PVector UL, PVector BR) {
		return (point.x >= UL.x && point.y >= UL.y) && (point.x <= BR.x && point.y <= BR.y) // SE
				|| (point.x >= BR.x && point.y >= BR.y) && (point.x <= UL.x && point.y <= UL.y) // NW
				|| (point.x <= UL.x && point.x >= BR.x) && (point.y >= UL.y && point.y <= BR.y) // SW
				|| (point.x <= BR.x && point.x >= UL.x) && (point.y >= BR.y && point.y <= UL.y); // NE
	}

	/**
	 * Where is the camera in the world? Accounts for centering the camera (ie. an
	 * object located at (0, 0) which the camera is following gives a camera
	 * position of (0, 0) too).
	 * 
	 * @return Representation of camera position (the point the camera is centered
	 *         on).
	 * @see {@link ZoomPan#getPanOffset() getPanOffset()} (not adjusted for
	 *      centering and translation)
	 */
	public PVector getPosition() {
		return logicalPosition;
	}

	/**
	 * Returns clockwise rotation of the camera.
	 * 
	 * @return rotation (radians).
	 */
	public float getCameraRotation() {
		return PApplet.abs(rotation) % PApplet.TWO_PI;
	}

	/**
	 * Returns the world position the mouse is over, accounting for camera rotation
	 * (uses polar coordinates). Overrides the {@link ZoomPan#getMouseCoord() parent
	 * method}, since this does not account for camera rotation.
	 * 
	 * @return World position the mouse is over.
	 */
	@Override
	public PVector getMouseCoord() {
		final float theta = Util.angleBetween(super.getMouseCoord(), logicalPosition) - rotation;
		final float eDist = PVector.dist(super.getMouseCoord(), logicalPosition);
		final float xReal = logicalPosition.x + eDist * (cos(theta));
		final float yReal = logicalPosition.y + eDist * (sin(theta));

		return new PVector(xReal, yReal);
	}

}
