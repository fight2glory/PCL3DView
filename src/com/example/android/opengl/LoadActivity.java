package com.example.android.opengl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;

public class LoadActivity extends Activity {

	public float[] triangleCoords;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
		try {
			LoadMesh();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("ready to send","here it goes.");
	    Intent intent = new Intent(this, OpenGLES20Complete.class);
	    Log.i("ready to send","putting");
	    intent.putExtra("Cord",triangleCoords);
	    Log.i("ready to send","starting");
   	    startActivity(intent);
   	 Log.i("ready to send","finishing");
	 //   finish();
	//    return;
	}

	public void LoadMesh() throws IOException
	{
		    
	    	File dir = Environment.getExternalStorageDirectory();
	    	File file = new File(dir,"P3D/model_col.txt");
	    	BufferedReader reader = null;
	    	List<Float> list = new ArrayList<Float>();
	    	
	    	try {
	    	    reader = new BufferedReader(new FileReader(file));
	    	    String text = null;
	    	    while ((text = reader.readLine()) != null) {
	    	    	list.add(Float.parseFloat(text));	    	
	    	    }
	    	} catch (FileNotFoundException e) {
	    	    e.printStackTrace();
	    	}catch (IOException e) {
	    	    e.printStackTrace();
	    	}
	    	try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	triangleCoords = new float[list.size()];
	    	for (int i = 0; i < list.size(); i++) {
	    	    Float f = list.get(i);
	    	    triangleCoords[i] = (float) (f.floatValue()/10.0); // Or whatever default you want.
	    	}
	    	Log.i("Done loading","loading done");
	   
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load, menu);
		return true;
	}

}
