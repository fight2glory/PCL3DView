/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.opengl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class OpenGLES20Complete extends Activity {
	private MyGLSurfaceView mGLView;
	public  PCLMesh MeshModel;
	public boolean load;
    private float TOUCH_SCALE_FACTOR = 0.5f;
    private float mPreviousX;
    private float mPreviousY;
    EditText editBox;
    GridView GView;
    Gallery GalView;
    private ArrayList<String> listCountry;
    private ArrayList<Integer> listFlag;
    File[]  imagelist;
    public int movePosition;
    
    public long taptime=0;
    public long tap2time;
    

    
    public float newDist; 
    public float oldDist=(float) 100.0;
	private final int NONE = 0;
	private final int DRAG = 1;
	private final int ZOOM = 2;
	private final int SELECT = 3;
	private int mode;
	
    private Uri[] mUrls;
    public String[] mFiles=null;
	 
  
	private static final int REQUEST_CODE = 1234;
	private static final String CHOOSER_TITLE = "Select a file";
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	load = false;
        super.onCreate(savedInstanceState);   
        Log.i("onStart","starting");
    //   prepareList();
   	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
     // Create a GLSurfaceView instance and set it
     // as the ContentView for this Activity
 //    float[] Cords=getIntent().getFloatArrayExtra("Cord");
   //setContentView(mGLView);
      if(load == false)
	 setContentView(R.layout.activity_load);
      Intent target = FileUtils.createGetContentIntent();
      Intent intent = Intent.createChooser(target, CHOOSER_TITLE);
      try {
          startActivityForResult(intent, REQUEST_CODE);
      } catch (ActivityNotFoundException e) {
          // The reason for the existence of aFileChooser
      }
	 
	 Log.i("end of onStart","yes end");
           //  mGLView.requestFocus();
         //    mGLView.setFocusableInTouchMode(true);

    }

    public void LoadValues(Uri uri)
    {
    	LoadDataThread Mydata = new LoadDataThread();
    	Mydata.execute(uri);
   	    Log.i("end of onStart","just made thread");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CODE:  
            if (resultCode == RESULT_OK) {  
                // The URI of the selected file 
                final Uri uri = data.getData();
                // Create a File from this Uri
                LoadValues(uri);
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        if(load == true)
        mGLView.onPause();
    }
 
 
    
    @Override public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		
		Log.i("ONTOUCHEVENT", x+" "+y);
		
		float w = mGLView.getWidth();
		float h = mGLView.getHeight();
		Log.i("Selected", imagelist[movePosition].getAbsolutePath());
		String tmp="";
		char[] SS = mFiles[movePosition].toCharArray();
		for(int i=SS.length-1;i>=0;i--)
		{
			if(SS[i]=='/')
					break;
			tmp+=SS[i];
		}
		String tmp2="";
		
		
		
		for(int i=tmp.length()-1;i>=0;i--)
			tmp2+=tmp.charAt(i);
		mGLView.mRenderer.GALimage =  tmp2;
		mGLView.mRenderer.stringMatching();
		return true;
		
	}

   
    
	// finds spacing
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	
	
    @Override
    protected void onResume() {
        super.onResume();
        
        Log.i("resuming","yes resuming");
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        if(load == true)
        {
        	
           mGLView.onResume();
           
        }
    }
    
    public class LoadDataThread extends AsyncTask<Uri, Void ,PCLMesh>  {
        public float[] triangleCoords;
        PCLMesh testModel;
        private ProgressDialog pdialog;
        
        protected void onProgressUpdate()
        {
             	
        }
        
        @Override protected void onPreExecute()
        {
        	pdialog = new ProgressDialog(OpenGLES20Complete.this);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdialog.setTitle("Loading Model...");
        	pdialog.show();
        }
     	@Override
    	protected PCLMesh doInBackground(Uri... params) {
    		try {
				testModel = new PCLMesh(OpenGLES20Complete.this,params[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	Log.i("Done loading","loading done");  
    		// TODO Auto-generated method stub
    		return testModel;
    	}
     	@Override protected void onPostExecute(PCLMesh model) {
     		 pdialog.dismiss();
     		 Log.i("end of onStart","thread ended");
     		 MeshModel = model;
     		 load = true;
     		 
     		setContentView(R.layout.main);
     		mGLView = (MyGLSurfaceView) findViewById(R.id.glSurface);
     	//	mGLView = new MyGLSurfaceView(OpenGLES20Complete.this);
    	    mGLView.loadmodel(MeshModel);
    	    
    	   
    	    
    	    Display display = getWindowManager().getDefaultDisplay();
    	    final float hh = display.getHeight();
    	    
    	    // Handle touch of GL VIEWER 
    	    mGLView.setOnTouchListener(new View.OnTouchListener() {
    	        @Override
    	        public boolean onTouch(View v, MotionEvent event) {   
    	        //	Log.i("touching: touchevent ",event.getX()+" "+event.getY());
    	        	if(event.getY() < 0.85 * hh)
    	        	{
    	        	float x = event.getX();
    	    		float y = event.getY();
    	    		float w = mGLView.getWidth();
    	    		float h = mGLView.getHeight();
    	    		
    	    		switch (event.getAction() & event.ACTION_MASK) {
    	    		case MotionEvent.ACTION_DOWN:			
    	    			tap2time = System.currentTimeMillis();
    	    			
    	    			
    	    			
    	    			if(tap2time-taptime <= 300 && taptime != tap2time)
    	    			{
    	    				Log.d("ShaderActivity", "mode=SELECT" );
        	    			mode = SELECT;
    	    			}
    	    			else
    	    			{
    	    				Log.d("ShaderActivity", "mode=DRAG" );
        	    			mode = DRAG;
        	    			mGLView.mRenderer.handsoff = false;
    	    			}
    	    			taptime = tap2time;
                        
    	    			break;
    	    		case MotionEvent.ACTION_POINTER_DOWN:	// two touches: zoom
    	    			
    	    			oldDist = spacing(event);
    	    			
    	    			if (oldDist >= 10.0f) {
    	    				Log.d("ShaderActivity", "mode=ZOOM" );
    	    				mode = ZOOM; // zoom
    	    			}
    	    			break;
    	    		case MotionEvent.ACTION_UP:		// no mode
    	    			mGLView.mRenderer.handsoff = true;
    	    			if(mode == SELECT)
    	    			{
    	    				mGLView.mRenderer.tapcordx = event.getX();
    	    				mGLView.mRenderer.tapcordy = event.getY();
    	    				Log.d("tap cordinates",event.getX()+" "+event.getY());
    	    				mGLView.mRenderer.picking(event.getX(), event.getY());
    	    			}
    	    			mode = NONE;
    	    			Log.d("ShaderActivity", "mode=NONE" );
    	    			oldDist = 10.0f;
    	    			break;
    	    		case MotionEvent.ACTION_POINTER_UP:		// no mode
    	    			mode = NONE;
    	    			Log.d("ShaderActivity", "mode=NONE" );
    	    			oldDist = 10.0f;
    	    			break;
    	    		case MotionEvent.ACTION_MOVE:						// rotation
    	    			mGLView.mRenderer.handsoff = false;
    	    			if (event.getPointerCount() > 1 && mode == ZOOM) {
    	    				newDist = spacing(event);
    	    			//	Log.d("SPACING: ", "OldDist: " + oldDist + ", NewDist: " + newDist);
    	    			//	Log.i("Point of occur", event.getRawX()+" "+event.getRawY());
    	    				if (newDist >= 10.0f) {
    	    					
    	    					float scale = newDist/oldDist; // scale
    	    					// scale in the renderer
    	    				/*	if(scale > 1.0)
    	    						scale = (float) 1.1;
    	    					else
    	    						scale = (float) 0.95;*/
    	    					
    	    					mGLView.mRenderer.changeScale(scale);
    	    			//		mGLView.requestRender();
    	    					oldDist = newDist;
    	    				}
    	    			}
    	    			else if (mode == DRAG){
    	    				float dx = x - mPreviousX;
    	    				float dy = y - mPreviousY;
    	    				mGLView.mRenderer.handsoff = false;
    	    				mGLView.mRenderer.xAngle += dx * TOUCH_SCALE_FACTOR;
    	    				mGLView.mRenderer.yAngle += dy * TOUCH_SCALE_FACTOR;
    	    		//		mGLView.mRenderer.eyeX += dx;
    	    		//		mGLView.mRenderer.eyeY += dy;
    	    			//	mGLView.requestRender();
    	    			}
    	    			
    	    			break;
    	    		}
    	    		mPreviousX = x;
    	    		mPreviousY = y;
    	    		mGLView.requestRender();
    	    		return true;
    	        }
    	        else
    	        {
    	        	mGLView.requestRender();
    	        
    	        	return false;
    	        }
    	        }
    	    });
    	    
    	    // Load files onto GALLERY VIEW
    	    File images = Environment.getExternalStorageDirectory();
             imagelist = images.listFiles(new ImageFilter());


            mFiles = new String[imagelist.length];

            Log.i("OpenGLES", ""+imagelist.length);
            for(int i= 0 ; i< imagelist.length; i++){
                mFiles[i] = imagelist[i].getAbsolutePath();
            }

         //   mGLView.mRenderer.ImgPath = mFiles;
            
            mUrls = new Uri[mFiles.length];
            Log.i("length:",mFiles.length+"size");
            for(int i=0; i < mFiles.length; i++){
                mUrls[i] = Uri.parse(mFiles[i]); 
              //  Log.i("Image file:",mUrls[i]+".jpg");
            }
    	    
    	    GalView = (Gallery)findViewById(R.id.Gallery01);
            GalView.setAdapter(new ImageAdapter(OpenGLES20Complete.this,mUrls,mFiles));
            GalView.setOnTouchListener(null);
            GalView.setOnItemSelectedListener(new OnItemSelectedListener()
            {
            	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            	{
            	
            		movePosition = position;
            	
            	}
            
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
            }
            );
    	 //   GalView.setAnimationDuration(100);
            }
     	

    }


    class ImageFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return (name.endsWith(".jpg"));
        }
    }

}


