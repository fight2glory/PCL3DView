package com.example.android.opengl;

public class RayCast {

	/**
	 * @param args
	 */
	
	public float FOV;
	public float height;
	public float width;
	public float whratio;
	
	public void updateFOV(float Width, float Height)
	{
		width = Width;
		height = Height;
		whratio = Width/Height; 
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}

}
