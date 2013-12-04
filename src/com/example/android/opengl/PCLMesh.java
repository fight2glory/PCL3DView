package com.example.android.opengl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class PCLMesh {
		    public  FloatBuffer vertexBuffer,colorBuffer;
		    public int COORDS_PER_VERTEX=3;
		    public float[] triangleCoords;
		    float[] ColorsVals;
		    public int vertexCount ;
		    public int vertexStride ; 
		    public float color[];
		    public LoadColor ColorsClass;
		    
		  
		public PCLMesh(Context context, Uri furi ) throws IOException, URISyntaxException {
		   
			    ColorsClass = new LoadColor();
			    ColorsClass.start();
		    	File file = new File(FileUtils.getPath(context, furi));
		    //	List<Float> list = new ArrayList<Float>();  
				BufferedReader reader;
				try {

		    		reader = new BufferedReader(new FileReader(file));
		    		String text = null;
		    		text = reader.readLine();
		    		int nos = (int) Float.parseFloat(text);
		    		triangleCoords = new float[nos*3+4];
		    	    int pidx = 0;
		    	    Float var;
		    	    Log.i("PCLMESH: ",text);
		    	    while ((text = reader.readLine()) != null) {
		    	       //  list.add(Float.parseFloat(text));
		    	    			var = Float.parseFloat(text);
		    	    			triangleCoords[pidx] = (float)(var.floatValue()/1000000.0);
		    	    			pidx++;
		    	    }
		    	} catch (FileNotFoundException e) {
		    	    e.printStackTrace();
		    	}catch (IOException e) {
		    	    e.printStackTrace();
		    	}
		
				Log.i("colored","bufferig");
		        // initialize vertex byte buffer for shape coordinates
		    	vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
		        vertexStride = COORDS_PER_VERTEX * 4;
		       
		        
		        ByteBuffer bb = ByteBuffer.allocateDirect(
		                // (number of coordinate values * 4 bytes per float)
		                triangleCoords.length * 4);
		        // use the device hardware's native byte order
		        bb.order(ByteOrder.nativeOrder());
		        // create a floating point buffer from the ByteBuffer
		        vertexBuffer = bb.asFloatBuffer();
		        // add the coordinates to the FloatBuffer
		        vertexBuffer.put(triangleCoords);
		        // set the buffer to read the first coordinate
		        vertexBuffer.position(0);
		        while(!ColorsClass.check())
		        {
		        };
		        color = ColorsClass.color;
		        colorBuffer = ColorsClass.colorBuffer;
      }
}
