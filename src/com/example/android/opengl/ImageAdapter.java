package com.example.android.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAdapter extends BaseAdapter {
	/** The parent context */
	private Context myContext;
	public int selecteditem;
	 public String[] mFiles=null;
	// Put some images to project-folder: /res/drawable/
	// format: jpg, gif, png, bmp, ...
	private int[] myImageIds = { R.drawable.india, R.drawable.brazil,
			       R.drawable.italy, R.drawable.china };
    private Uri[] iUri;
	/** Simple Constructor saving the 'parent' context. */
	public ImageAdapter(Context c, Uri[] mUri, String[] paths) {
		this.myContext = c;
		iUri = mUri;
		mFiles = paths;
		
	}


public void OnItemSelected(AdapterView<?> parent, View v, int position, long id)
{
     	selecteditem = position;
     	Log.i(selecteditem+"","parent");
}


	// inherited abstract methods - must be implemented
	// Returns count of images, and individual IDs
	public int getCount() {
		return this.iUri.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	// Returns a new ImageView to be displayed,
	public View getView(int position, View convertView, 
			ViewGroup parent) {

		// Get a View to display image data 		
		ImageView iv = new ImageView(this.myContext);
		//iv.setImageURI(iUri[position]);
	//	iv.setScaleType(ImageView.ScaleType.CENTER_CROP);			

		//iv.setLayoutParams(new Gallery.LayoutParams((int) (parent.getWidth()/3.0), parent.getHeight()));

		
		// NEW METHOD
		
		     Bitmap bitmap  =  BitmapFactory.decodeFile(mFiles[position]);
		     Log.i(mFiles[position], "filename");
		    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (parent.getWidth()/3.0), parent.getHeight(), false);
		    Log.i("ImageVIew", "making bitmap");
		    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
		    // Apply the scaled bitmap
		    iv.setImageDrawable(result);
		    
		    iv.setLayoutParams(new Gallery.LayoutParams((int) (parent.getWidth()/3.0), parent.getHeight()));
		    iv.setScaleType(ImageView.ScaleType.FIT_XY);
		    // Now change ImageView's dimensions to match the scaled image
	//	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams(); 
		//    params.width = width;
		//    params.height = height;
	//	    iv.setLayoutParams(params);
		//
		    bitmap.recycle();
		return iv;
	}
	
	
}// ImageAdapter