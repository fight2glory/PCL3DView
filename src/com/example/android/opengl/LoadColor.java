package com.example.android.opengl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.os.Environment;
import android.util.Log;

public class LoadColor extends Thread {
 
	 public boolean workDone;
	 public  FloatBuffer colorBuffer;
	 public float[] color;
	@Override
	public void run()
	{
		 BufferedReader reader2;
			
			try {

	    		reader2 = new BufferedReader(new FileReader("/mnt/sdcard/P3D/Truevals.txt"));
		    //	reader2 = new File(Environment.getExternalStorageDirectory(),"P3D/model_Cube.txt");
	    		String text = null;
	    	//	  Log.i("Ending here",reader2.toString());
	    		text = reader2.readLine();
	    		int nos = (int) Float.parseFloat(text);
	    		color = new float[nos*4+5];
	    	    int pidx = 0;
	    	    Float var;
	    	  
	    	    while ((text = reader2.readLine()) != null) {
	    	       //  list.add(Float.parseFloat(text));
	    	    			var = Float.parseFloat(text);
	    	    			
	    	    			color[pidx] = (float)(var.floatValue());
	    	    		    if(pidx%4!=3)
	    	    		    	color[pidx]=(float) (color[pidx]/255.0);
	    	    			pidx++;
	    	    }
	    	} catch (FileNotFoundException e) {
	    	    e.printStackTrace();
	    	}catch (IOException e) {
	    	    e.printStackTrace();
	    	}
			  ByteBuffer cb = ByteBuffer.allocateDirect(
		                // (number of coordinate values * 4 bytes per float)
		                color.length * 4);
		        // use the device hardware's native byte order
		        cb.order(ByteOrder.nativeOrder());
		        // create a floating point buffer from the ByteBuffer
		        colorBuffer = cb.asFloatBuffer();
		        // add the coordinates to the FloatBuffer
		        colorBuffer.put(color);
		        // set the buffer to read the first coordinate
		        colorBuffer.position(0);
		        workDone = true;
	     	
	}
	public LoadColor()
	{
	       workDone = false;
	}
	public boolean check()
	{
		return workDone;
	}
}
