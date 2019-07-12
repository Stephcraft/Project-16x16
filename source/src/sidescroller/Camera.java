package sidescroller;

import org.gicentre.utils.move.ZoomPan;

import objects.EditableObject;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Camera class. Extends {@link org.gicentre.utils.move.ZoomPan ZoomPan}, offering some bespoke methods relating to Project-16x16.
 * At the moment, the camera uses simple lerping when following objects.
 * @author micycle1
 * @see {@link org.gicentre.utils.move.ZoomPan ZoomPan}
 */
public final class Camera extends ZoomPan {

	private float lerpSpeed = 0.1f, zoom = 1.0f;
	private PVector position, followObjectOffset = new PVector(0, 0);
	private boolean shaking = false, following = false;
	private EditableObject followObject;

	/**
	 * The most basic constructor.
	 * Initialises the camera at position (0, 0).
	 * @param applet Target applet ({@link SideScroller}).
	 */
	public Camera(PApplet applet) {
		super(applet);
		position = new PVector(0, 0); // default
	}

	/**
	 * 
	 * @param applet Target applet ({@link SideScroller}).
	 * @param startPosition Initial camera position.
	 */
	public Camera(PApplet applet, PVector startPosition) {
		super(applet);
		position = new PVector(-startPosition.x, -startPosition.y);
	}

	/**
	 * 
	 * @param applet Target applet ({@link SideScroller}).
	 * @param followObject Object the camera will follow.
	 */
	public Camera(PApplet applet, EditableObject followObject) {
		super(applet);
		this.followObject = followObject;
		following = true;
//		setPanOffset(0, 0);
	}

	/**
	 * 
	 * @param applet Target applet ({@link SideScroller}).
	 * @param followObject Object the camera will follow.
	 * @param offset Offset with which the camera will follow the given object.
	 */
	public Camera(PApplet applet, EditableObject followObject, PVector offset) {
		super(applet);
		this.followObject = followObject;
		following = true;
		followObjectOffset = offset.copy();
//		setPanOffset(0, 0);
	}

	/**
	 * This should be put in the {@link SideScroller#Draw draw()} loop.
	 */
	public void run() {
		transform();
		zoom = 0.3f; // todo not working?
		setZoomScale(zoom); // todo not working?
		if (following) {
			setPanOffset(PApplet.lerp(getPanOffset().x, -followObject.pos.x - followObjectOffset.x, lerpSpeed),
					PApplet.lerp(getPanOffset().y, -followObject.pos.y - followObjectOffset.y, lerpSpeed));
		} else {
			// setPanOffset(PApplet.lerp(getPanOffset().x, position.x, lerpSpeed),
			// PApplet.lerp(getPanOffset().y, position.y, lerpSpeed));
			setPanOffset(position.x, position.y);
		}

		if (shaking) {
			// todo
		}
	}

	/**
	 * Tells camera which object to follow. retains previous offset.
	 * @param offset
	 */
	public void setFollowObject(EditableObject o) {
		followObject = o;
		following = true;
	}

	/**
	 * Tells camera which object to follow, and new offset.
	 * @param offset
	 */
	public void setFollowObject(EditableObject o, PVector offset) {
		followObject = o;
		followObjectOffset = offset.copy();
		following = true;
	}

	/**
	 * Modify existing follow offset.
	 * @param followObjectOffset
	 */
	public void setFollowObjectOffset(PVector followObjectOffset) {
		this.followObjectOffset = followObjectOffset;
	}

	/**
	 * Shake camera around current position
	 * @param force
	 */
	public void shake(float force) { // todo
		shaking = true;
		// will need to record frame # when it began
		// and initial/return position
	}

	public void setCameraPosition(PVector position) {
		following = false;
		this.position = position.copy();
	}

	@Override
	public void setZoomScale(double zoomScale) {
		zoom = (float) zoomScale;
	}

	public void zoomIn(float amount) {
		zoom -= amount;
	}

	public void zoomOut(float amount) {
		zoom += amount;
	}

	/**
	 * @return Formatted string representation of camera position.
	 */
	public String getCameraPosition() {
		return PApplet.round(getPanOffset().x) + ", " + PApplet.round(getPanOffset().y);
	}

}
