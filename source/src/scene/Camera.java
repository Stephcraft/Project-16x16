package scene;

import processing.core.*;
import sidescroller.SideScroller;
import objects.EditableObject;
import processing.awt.PGraphicsJava2D;
import java.lang.*;

/**
 * <h1>Camera Class</h1>
 * <p>
 * 	The Camera class controls a camera with a PMatrix. In conjunction with a Camera Manager class
 * 	will allow Developers to switch between multiple cameras for better screen effects. 
 * </p>
 * @author QwaddleMan
 *
 */
public class Camera{

	private PVector position;
	private PVector scale;
	public PVector size;
	private PMatrix2D CamMatrix;
	private SideScroller applet;
	private float theta;
	
//	private float x;
	
	public Camera(){
		this(0,0,800,600,1,1,null);
	}
	
	/**
	 * Constructor
	 * @param x float
	 * @param y float
	 * @param w float
	 * @param h float
	 * @param scaleX float
	 * @param scaleY float
	 * @param pApp SideScroller
	 */
	public Camera(float x, float y, float w, float h, float scaleX, float scaleY, SideScroller applet){
		position = new PVector(x, y);
		size = new PVector(w,h);
		scale = new PVector(scaleX, scaleY);
		
		CamMatrix = new PMatrix2D();
		this.applet = applet;
		theta = 0;
	}
	
	/**
	 * initialized the Camera with basic information.
	 */
	public void CameraInit(){
		CamMatrix.set(
				scale.x, 0.0f, 0,
				0.0f, scale.y, 0
				);
		
		applet.originX = (int)position.x;
		applet.originY = (int)position.y;
		CamMatrix.rotate(0.0f);
	}
	
	/**
	 * Makes the Camera follow a object of subclass EditableObject
	 * @param obj EditableObject is the object to follow.
	 */
	public void Follow(EditableObject obj){
		
		//System.out.println(obj.pos.x + ", " + obj.pos.y);
		position.x = obj.pos.x;
		position.y = obj.pos.y;
		
		CamMatrix.m02 = position.x;
		CamMatrix.m12 = position.y;
	}
	
	/**
	 * adds an amount of degrees to the cameras rotation about the origin
	 * @param degrees float
	 */
	public void addRotate(float degrees)
	{
		CamMatrix.apply(new PMatrix2D(
				(float)Math.cos(degrees), (float)Math.sin(degrees),  0,
				-1 * (float)Math.sin(degrees), (float)Math.cos(degrees), 1
				));
	}
	
	/**
	 * adds an amount of degrees to the cameras rotation about a point
	 * @param degrees float
	 */
	public void addRotateAbout(float degrees, float x, float y)
	{
		
		CamMatrix.apply(new PMatrix2D(
				(float)Math.cos(degrees), (float)Math.sin(degrees),  x-CamMatrix.m00 * x - CamMatrix.m01 * y,
				-1 * (float)Math.sin(degrees), (float)Math.cos(degrees),  y-CamMatrix.m10 * x - CamMatrix.m11 * y
				));
		
		
	}
	
	/**
	 * makes the camera zoom in or out
	 * @param amt float
	 */
	public void zoom(float amt) {
		
		scale.x += amt;
		scale.y += amt;
		
		applet.scale(scale.x, scale.y);
	}
	
	/**
	 * moves the camera in the direction specified
	 * @param x float the X axis of movement
	 * @param y float the Y axis of movement
	 */
	public void Move(float x, float y)
	{
		position.x += x;
		position.y += y;
		
		applet.originX = (int)position.x;
		applet.originY = (int)position.y;
		
//		CamMatrix.m02 = position.x;
//		CamMatrix.m12 = position.y;
//		System.out.println(CamMatrix.m02);
	}
	
	public void setPosition(float x, float y)
	{
		CamMatrix.translate(x, y);
	}

	//TODO: move this to CameraManager class
	/**
	 * sets this camera as the main camera. AKA the one being used at the time.
	 * For some reason this must be called every frame.
	 */
	public void useCamera(){
//		applet.setMatrix(CamMatrix);
		
	}
	
}
