package sidescroller;

import org.gicentre.utils.move.ZoomPan;

import objects.EditableObject;
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
 * @todo deadzone mode can be choppy when tracking; shaking; worlddeadzone;
 *       zoom-to-fit (multiple entities)
 * @author micycle1
 * @see {@link org.gicentre.utils.move.ZoomPan ZoomPan}
 */
public final class Camera extends ZoomPan {

	private SideScroller applet;
	private float lerpSpeed = 0.05f, zoom = 1.0f;
	private float rotation = 0, shakeRotationOffset = 0;
	private PVector targetPosition, logicalPosition, offset;
	private PVector followObjectOffset = new PVector(0, 0), shakeOffset = new PVector(0, 0);
	private PVector deadZoneP1, deadZoneP2;
	private boolean following = false, deadZoneScreen = false, deadZoneWorld = false;
	private int deadZoneTypeLast = 0;
	private EditableObject followObject;
	private float zoomMax = 100, zoomMin = 0;
	private float trauma = 0, traumaDecay = 0.02f;

	/**
	 * The most basic constructor. Initialises the camera at position (0, 0).
	 * 
	 * @param applet Target applet ({@link SideScroller}).
	 */
	public Camera(SideScroller applet) {
		super(applet);
		this.applet = applet;
		targetPosition = new PVector(0, 0); // default position
		if (SideScroller.DEBUG) {
			applet.registerMethod("post", this);
		}
	}

	/**
	 * Constructor. Can specify camera's initial fixed position.
	 * 
	 * @param applet        Target applet ({@link SideScroller}).
	 * @param startPosition Initial camera position.
	 */
	public Camera(SideScroller applet, PVector startPosition) {
		super(applet);
		this.applet = applet;
		targetPosition = new PVector(-startPosition.x, -startPosition.y);
		if (SideScroller.DEBUG) {
			applet.registerMethod("post", this);
		}
	}

	/**
	 * 
	 * @param applet       Target applet ({@link SideScroller}).
	 * @param followObject Object the camera will follow.
	 */
	public Camera(SideScroller applet, EditableObject followObject) {
		super(applet);
		this.applet = applet;
		this.followObject = followObject;
		following = true;
		if (SideScroller.DEBUG) {
			applet.registerMethod("post", this);
		}
	}

	/**
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
		if (SideScroller.DEBUG) {
			applet.registerMethod("post", this);
		}
	}

	/**
	 * Draws camera debug info if {@link SideScroller#DEBUG DEBUG} is true. Bound to
	 * the end of draw() loop using {@link PApplet#registerMethod(String, Object)
	 * registerMethod()} - don't call this method manually!
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
			applet.rect(getCoordToDisp(followObject.pos).x, getCoordToDisp(followObject.pos).y, length * 2, length * 2);
			if (deadZoneScreen) {
				applet.rectMode(PApplet.CORNER);
				applet.rect(deadZoneP1.x, deadZoneP1.y, deadZoneP2.x - deadZoneP1.x, deadZoneP2.y - deadZoneP1.y);
				applet.rectMode(PApplet.CENTER);
			}
		} else {
			applet.rect(getCoordToDisp(PVector.mult(targetPosition, -1)).x,
					getCoordToDisp(PVector.mult(targetPosition, -1)).y, length * 2, length * 2);
		}
	}

	/**
	 * Updates the camera. Should be put in the {@link SideScroller#Draw draw()}
	 * loop of the PApplet.
	 */
	public void run() {
		offset = new PVector(applet.width / 2, applet.height / 2);

		applet.translate(offset.x, offset.y);
		applet.rotate(rotation + shakeRotationOffset);
		applet.translate(-offset.x, -offset.y);

		transform();

		float scale = PApplet.lerp((float) getZoomScaleX(), zoom, lerpSpeed);
		setZoomScaleX(scale);
		setZoomScaleY(scale);

		if (following && (((deadZoneScreen && !withinScreenDeadZone()) || ((deadZoneWorld && !withinWorldDeadZone()))
				|| (!deadZoneScreen && !deadZoneWorld)))) {
			setPanOffset(
					PApplet.lerp(getPanOffset().x,
							(-followObject.pos.x - followObjectOffset.x - shakeOffset.x + offset.x) * zoom, lerpSpeed),
					PApplet.lerp(getPanOffset().y,
							(-followObject.pos.y - followObjectOffset.y - shakeOffset.y + offset.y) * zoom, lerpSpeed));
		} else if (!following) {
			setPanOffset(
					PApplet.lerp(getPanOffset().x, (targetPosition.x - shakeOffset.x + offset.x) * zoom, lerpSpeed),
					PApplet.lerp(getPanOffset().y, (targetPosition.y - shakeOffset.y + offset.y) * zoom, lerpSpeed));
		}

		if (trauma > 0) { // 400 and 0.35 seem suitable values
			trauma -= traumaDecay;

			float x = (trauma * trauma) * applet.random(-1, 1) * 400;
			float y = (trauma * trauma) * applet.random(-1, 1) * 400;
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
	 * be drawn above the camera and msut be called after).
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
			System.err.print("Define a deadzone first");
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
	 * Set camera rotation (rotates around camera position).
	 * 
	 * @param angle radians
	 */
	public void setRotation(float angle) {
		rotation = angle;
	}

	/**
	 * Modify existing rotation.
	 * 
	 * @param angle radians
	 */
	public void rotate(float angle) {
		rotation += angle;
	}

	@Override
	public void setZoomScale(double zoomScale) {
		zoom = (float) zoomScale;
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
		zoom += amount;
		zoom = PApplet.min(zoom, zoomMax);
	}

	public void zoomOut(float amount) {
		zoom -= amount;
		zoom = PApplet.max(zoom, zoomMin);
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
	 * @return Formatted string representation of camera position (point the camera
	 *         is centered on).
	 * @see {@link ZoomPan#getPanOffset() getPanOffset()} (not adjusted for
	 *      centering and translation)
	 */
	public String getCameraPosition() {
		logicalPosition = PVector.sub(getPanOffset(), offset); // offset camera to center screen
		return PApplet.round(-logicalPosition.x) + ", " + PApplet.round(-logicalPosition.y);
	}

	/**
	 * Return the world positon the mouse is over, accounting for camera rotation.
	 * NOT WORKING FULLY. todo
	 * 
	 * @return
	 */
	private PVector getRotationMouseCoord() { // return mouseCoord, acc
		logicalPosition = PVector.sub(getPanOffset(), offset); // offset camera to center screen
		PVector z = new PVector(getMouseCoord().x - logicalPosition.x, getMouseCoord().y + logicalPosition.y);
		PVector n = new PVector(
				(z.x * cos(rotation + shakeRotationOffset)) + (z.y * sin(rotation + shakeRotationOffset)),
				z.x * sin(rotation + shakeRotationOffset) - z.y * cos(rotation + shakeRotationOffset));
		n = new PVector((logicalPosition.x + n.x), -(logicalPosition.y + n.y));
		return n;
	}

}
