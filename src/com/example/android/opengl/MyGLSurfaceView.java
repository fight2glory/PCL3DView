package com.example.android.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

class MyGLSurfaceView extends GLSurfaceView {

    public  MyGLRenderer mRenderer;
    
    public MyGLSurfaceView(Context context,AttributeSet attrs) {
        super(context,attrs);
      
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);      
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); 

    }
    public void loadmodel( PCLMesh mmodel)
    {
       mRenderer.mModel = mmodel;
    }


   
}
