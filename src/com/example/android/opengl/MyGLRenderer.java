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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
//    public Triangle mTriangle;
    private static float[] mMVPMatrix = new float[16];
    private static float[] mProjMatrix = new float[16];
    private static float[] mVMatrix = new float[16];
    private static float[] mRotationMatrix = new float[16];
    private static float[] mModelMatrix = new float[16];
    private Context mycontext;
    
    public boolean handsoff = true;
    public boolean GALview = false;
    public String GALimage;
    public int GALindex;
    public boolean going = false;
    
    PCLMesh mModel;
    String[] ImgPath;
    ImageTexture Img;
    static ImageTexture[] Images;
    private int texProgram;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int vertexShader;
    private int texShader;
    private int fragmentShader;
    private int texvertexShader;
    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    public volatile static float xAngle;
    public volatile float yAngle;
    public volatile float xcord;
    public volatile float ycord;
    public volatile float zcord;
    public float tapcordx;
    public float tapcordy;
    public boolean MoveMode;
    public int MoveIndex;
    public float fraction;
    public boolean tempMode = true;
    
    private float [] vRotation = {1,0,0,0};
    private float [] hRotation = {0,1,0,0};
    
    private float[] EYE = {0,0,-2,1};
    
    public static float[] Scale={1,1,1};
    public float zoom;
    public float[] eye={0f, 2f, -2f};
    int wat = 0; 
    
    float [] StartPos = new float[3];
    float [] EndPos = new float[3];
    float percentDone=0;
    float [] StartlookAt = new float[3];
    float [] EndlookAt = new float[3];
    public boolean StopLerp = false;
 
    
    //Parameters for Raycasting
    public static Vector3f lookAt = new Vector3f(0,0,0);
    public static Vector3f position = new Vector3f(0,0,0);
    public static Vector3f Start = new Vector3f(0,0,0);
    public static Vector3f view = new Vector3f(0,0,0);
    public static Vector3f screenHoritzontally = new Vector3f(0,0,0);
    public static Vector3f screenVertically = new Vector3f(0,0,0);
    public float viewAngle;
    public static Vector3f up = new Vector3f(0,0,0);
    public static float ratio;
    public static float ViewWidth;
    public static float ViewHeight;
    public PickingRay Ray = new PickingRay();
    public Vector3f moveTO = new Vector3f(0,0,0);
    
    
    final String PVShader = "uniform mat4 u_MVPMatrix;"+      		
    "attribute vec4 a_Position;"+     		
    "void main() {"+                              
    	"gl_Position = u_MVPMatrix * a_Position;"+   
       " gl_PointSize = 5.0;"+         
    "}     ";        	       
    
    final String PFShader = "precision mediump float;"+       
"void main(){"+                              
"gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);"+             
"}" ;
   
	private int aVerPos, aTexPos, aVerCol;
	private int uSamp;
	private int texture;
	private int[] ImgTexture;
	private int[] TextureCap;
    private Bitmap textureBitmap;
    private Bitmap[] CollectionBitmap;
    public int Camnos;
    
    // NEW ADDITION
    private final String TVShader=
    "uniform mat4 u_MVPMatrix;"+       // A constant representing the combined model/view/projection matrix.
    "uniform mat4 u_MVMatrix;"+        // A constant representing the combined model/view matrix.
    "attribute vec4 a_Position;"+      // Per-vertex position information we will pass in.
    "attribute vec4 a_Color;"+         // Per-vertex color information we will pass in.
    "attribute vec3 a_Normal;"+        // Per-vertex normal information we will pass in.
    "attribute vec2 a_TexCoordinate;"+ // Per-vertex texture coordinate information we will pass in.
     
    "varying vec3 v_Position;"+        // This will be passed into the fragment shader.
    "varying vec4 v_Color;"+           // This will be passed into the fragment shader.
    "varying vec3 v_Normal;"+          // This will be passed into the fragment shader.
    "varying vec2 v_TexCoordinate;"+   // This will be passed into the fragment shader.
     
    // The entry point for our vertex shader.
    "void main()"+
    "{"+
        // Transform the vertex into eye space.
        "v_Position = vec3(u_MVMatrix * a_Position);"+
     
        // Pass through the color.
        "v_Color = a_Color;"+
     
        // Pass through the texture coordinate.
        "v_TexCoordinate = a_TexCoordinate;"+
     
        // Transform the normal's orientation into eye space.
        "v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));"+
     
        // gl_Position is a special variable used to store the final position.
        // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
        "gl_Position = u_MVPMatrix * a_Position;"+
    "}";
    
    private final String TFShader =
    		"precision mediump float;"+        // Set the default precision to medium. We don't need as high of a
    				"uniform vec3 u_LightPos; "+
    				"uniform sampler2D u_Texture;"+    // The input texture.
"varying vec3 v_Position;"+        // Interpolated position for this fragment.
"varying vec4 v_Color;"+           // This is the color from the vertex shader interpolated across the
    // triangle per fragment.
"varying vec3 v_Normal;"+          // Interpolated normal for this fragment.
"varying vec2 v_TexCoordinate;"+   // Interpolated texture coordinate per fragment.

//The entry point for our fragment shader.
"void main()"+
"{"+
"float distance = length(u_LightPos - v_Position);"+

// Get a lighting direction vector from the light to the vertex.
"vec3 lightVector = normalize(u_LightPos - v_Position);"+

// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
// pointing in the same direction then it will get max illumination.
"float diffuse = max(dot(v_Normal, lightVector), 0.0);"+

// Add attenuation.
"diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));"+

// Add ambient lighting
"diffuse = diffuse + 0.3;"+

// Multiply the color by the diffuse illumination level and texture value to get final output color.
"gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate).rgba);"+
"}";
    
    private final String textureShaderCode = 
    		"precision highp float;"+
"varying vec3 vVerCol;"+
"varying vec2 vTexPos;"+
"uniform sampler2D uSampler;"+

"void main(void) {"+
 "   gl_FragColor = texture2D(uSampler, vTexPos);"+
"}";
    
    private final String texvertexShaderCode = 
    		"attribute vec3 aVerPos;"+
"attribute vec4 aVerCol;"+
"attribute vec2 aTexPos;"+
"uniform mat4 uMVPMatrix;" +
"varying vec4 vVerCol;"+
"varying vec2 vTexPos;"+
"void main(void){"+
    "vTexPos = aTexPos;"+
    "vVerCol = aVerCol;"+
    "gl_Position = uMVPMatrix *vec4(aVerPos, 1.0);"+
"}";
    
    private final String vertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 uMVPMatrix;" +
            "attribute vec4 color;" +
	        "attribute vec4 vPosition;" +
            "varying lowp vec4 vColor;"+
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	      //  "gl_Position = vPosition * uMVPMatrix;"+
	        "gl_Position = uMVPMatrix * vPosition;"+
	        "vColor = color;"+     
	        "}";

	    private final String fragmentShaderCode =
	        "precision highp float;" +
	        		"varying vec3 vVerCol;"+
	        		"varying vec2 vTexPos;"+
	        		"uniform sampler2D uSampler;"+
	      //  "uniform vec4 vColor;" +
	        		"uniform int TEXTSELECT;"+
	        		"varying lowp vec4 vColor;"+
	        "void main() {" +
	        	
	        "  gl_FragColor = vColor; " +
	        		
	        "}";

    public MyGLRenderer(Context context)
    {
    	  mycontext = context;
    	  Img = new ImageTexture(mycontext);
    	  
    	  // for ray casting 
    	  
    	  MoveMode = false;
    	  
    	  position.x = 0;
    	  position.y = 0;
    	  position.z = 0;
    	  
    	  view.x = 0;
    	  view.y = 0;
    	  view.z = 0;
    	  
    	  up.x = 0;
    	  up.y = 1;
    	  up.z = 0;
    	  
    	  lookAt.x = (float) 0;
    	  lookAt.y = (float) 0;
    	  lookAt.z = (float) -1;
    	  
    	  fraction = 0;
    	  zoom=0;
    }
	 
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
Log.i("On create","GLSURFACE");

        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
  //      GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
       
        
        
        
               //Enable Texture Mapping ( NEW )
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    	mProgram = GLES20.glCreateProgram();
    	/*early Initialization (Buffer creation)*/

  
    	GLES20.glEnable(GLES20.GL_BLEND);
    	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    	
    	// TEXTURE 
    	
   //   	 Img.init();      
      	 try {
			loadImage();
		//	 Img.init(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
         texProgram = GLES20.glCreateProgram();
         texvertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,texvertexShaderCode); 
         texShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,textureShaderCode);
         GLES20.glAttachShader(texProgram, texvertexShader);   
         GLES20.glAttachShader(texProgram, texShader);
         GLES20.glLinkProgram(texProgram);
 
    	
      	vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);    
    	mProgram = GLES20.glCreateProgram(); 
        GLES20.glAttachShader(mProgram, vertexShader);   
        GLES20.glAttachShader(mProgram, fragmentShader); 
        GLES20.glLinkProgram(mProgram);
        
      
        
    }
   
    public void stringMatching()
    {
 
    	if(going == true)
    		return;
    	going = true;
    	
       for(int i=0;i<Camnos;i++)
       {
    	   Log.i(GALimage,  ImgPath[i]);
           if(ImgPath[i].equalsIgnoreCase(GALimage))
           {
        	   GALindex = i;
        	   break;
           }
       }
       Log.i(" ",  " ");
       Log.i("instringmatch", GALindex+" ");
       MoveIndex = GALindex;
   	
   	Start = position;
   	
   //	Images[MoveIndex].worldCamCent(keeppos);
   	
   	moveTO.x = Images[MoveIndex].result[0];
   	moveTO.y = Images[MoveIndex].result[1];
   	moveTO.z = (float) (Images[MoveIndex].result[2]);
      
   	
   	
   	tempMode = false;
   	
   	
   	float[] npos = new float[4];
   	float[] opos = new float[4];
   	opos[0]=position.x;
   	opos[1]=position.y;
   	opos[2]=position.z;
   	opos[3]=1;
   	
   	Matrix.multiplyMV(npos, 0, mVMatrix, 0, opos, 0);
   	
   	npos[0]/=npos[3];
   	npos[1]/=npos[3];
   	npos[2]/=npos[3];
   	
   	float[] moveit = new float[3];
   	moveit[0]=  moveTO.x-npos[0];
   	moveit[1]=moveTO.y-npos[1];
   	moveit[2]=moveTO.z-npos[2];
   	
   	position.x=npos[0];
   	position.y=npos[1];
   	position.z=npos[2];
   	
   	if(MoveMode == false)
   	{
   	float [] getnowcords = new float[4];
   	float [] suppcords = {0,0,zoom,1};
   	Matrix.multiplyMV(getnowcords, 0, mVMatrix, 0, suppcords, 0);
   	getnowcords[0]/=getnowcords[3];
   	getnowcords[1]/=getnowcords[3];
   	getnowcords[2]/=getnowcords[3];
   	StartPos[0] = getnowcords[0];
   	StartPos[1] = getnowcords[1];
   	StartPos[2] = getnowcords[2];
   	}
   	else
   	{
   	   StartPos[0] = EndPos[0];
   	   StartPos[1] = EndPos[1];
   	   StartPos[2] = EndPos[2];
   	   
   	   StartlookAt[0] = EndlookAt[0];
   	   StartlookAt[1] = EndlookAt[1];
   	   StartlookAt[2] = EndlookAt[2];
   	   
   	}
   	EndPos[0] = Images[MoveIndex].testTran[0]+(zoom*Images[MoveIndex].ImgTransf[8]);
   	EndPos[1] = Images[MoveIndex].testTran[1]+(zoom*Images[MoveIndex].ImgTransf[9]);
   	EndPos[2] = Images[MoveIndex].testTran[2]+(zoom*Images[MoveIndex].ImgTransf[10]);
   	
   	EndlookAt[0] =  Images[MoveIndex].testTran[0]-(zoom*Images[MoveIndex].ImgTransf[8]);
   	EndlookAt[1] = Images[MoveIndex].testTran[1]-(zoom*Images[MoveIndex].ImgTransf[9]);
   	EndlookAt[2] = Images[MoveIndex].testTran[2]-(zoom*Images[MoveIndex].ImgTransf[10]);
   	
   	 Log.i(StartPos[0]+" "+StartPos[1]+" "+StartPos[2], "Start VALUE");
   	 Log.i(EndPos[0]+" "+EndPos[1]+" "+EndPos[2], "ENd VALUE");
   	MoveMode = true;
   	StopLerp = false;
   	
  // 	Matrix.translateM(mVMatrix, 0, moveit[0], moveit[1], moveit[2]);

   	float[] trmat = {0,0,zoom};
       
    }
    
    public static Vector3f unProject(float winx, float winy, float winz, float[] mmvp){
        
    	
       float[] in = new float[4],
        out = new float[4];
       
        float[] invMat = new float[16];
        
        Log.i("Center Cords ", winx+" "+winy+" "+winz);
        
    //    Matrix.transposeM(mmvp, 0, mmvp, 0);
        Matrix.invertM(invMat, 0, mmvp, 0);

        in[0] = (float) ((winx / (float)ViewWidth) * 2 - 1.0);
        in[1] = (float) ((winy / (float)ViewHeight) * 2- 1.0) ;
        in[2] = (float) (2 * winz - 1.0);
       //  in[2] = -1;
        in[3] = (float) 1.0;

    
  //      Log.i("IN: ",in[0]+" "+in[1]+" "+in[2]+" "+in[3]);
        
        
        Matrix.multiplyMV(out, 0, invMat, 0, in, 0);

      //  Log.i("IN: ",out[0]+" "+out[1]+" "+out[2]+" "+out[3]);
        if (out[3]==0)
            return new Vector3f(0,0,0);

        out[3] = (float) (1.0/out[3]);
        return new Vector3f(out[0] * out[3], out[1] * out[3], out[2] * out[3]);
    }
    
    
    public void picking(float screenX, float screenY)
    {
    	
    	float [] vview, rotM, modelM, mvpM;
    	vview = new float[16];
    	rotM = new float[16];
    	modelM = new float[16];
    	mvpM = new float[16];
        
    	Vector3f near = unProject(screenX, ViewHeight-screenY, 0,mMVPMatrix);
    	Vector3f far = unProject(screenX, ViewHeight-screenY, 1, mMVPMatrix);   // 1 for winz means projected on the far plane
  
    	Vector3f pickingRay = new Vector3f(0,0,0);

    	
    	
    	Images[0].DLCOD(near, far, true);
    	
    	float mindist=10000000;
    	int indx = -1;
    	float [] keeppos = new float[16];
    	float [] temppos = new float[16];
    	for(int i=0;i<Camnos;i++)
    	{
    		  float wincord[] = new float[3];
    	    	
    		
    //		  GLU.gluProject(Images[i].testTran[0], Images[i].testTran[1], Images[i].testTran[2], modelM, 0, mProjMatrix, 0, viewport, 0, wincord, 0);
    		  
    		   Matrix.setIdentityM(modelM, 0);
    		   Matrix.translateM(modelM, 0, Images[i].testTran[0], Images[i].testTran[1], Images[i].testTran[2]);
    		   Matrix.multiplyMM(modelM, 0, Images[i].ImgTransf, 0, modelM,0);
    	//	   Matrix.translateM(modelM, 0, Images[i].TransMat[3], Images[i].TransMat[7], Images[i].TransMat[11]);
    		  
    	//	   Matrix.multiplyMM(modelM, 0, mRotationMatrix, 0, modelM, 0);
    		   
    		   temppos = modelM;
    		   
    		   Matrix.multiplyMM(modelM, 0, mVMatrix, 0, modelM, 0);    		  
    	       Matrix.multiplyMM(mvpM, 0, mProjMatrix, 0, modelM, 0);
    		   
    	    
    	       
    	       
    	       
    	       
    	       float[] nearPos = new float[4];
               float[] farPos = new float[4];
    	       float [] trmvp = new float[16];
    	       
    	 
    	       
    	       Images[i].worldCamCent(mvpM);
    	       Images[i].TargetLines(near,far, new Vector3f(0,0,0));
    	       Images[i].LineBuff(mMVPMatrix);
    	     
    	//       Log.i("Result:", Images[i].result[0]+" "+Images[i].result[1]+" "+Images[i].result[2]);
    	  
    	        
    	        Vector3f II = new Vector3f(Images[i].result[0],Images[i].result[1],Images[i].result[2]);   
    	        Vector3f Nn = new Vector3f(Images[i].T1LCod[0],Images[i].T1LCod[1],Images[i].T1LCod[2]);
    	        Vector3f Ff = new Vector3f(Images[i].T2LCod[0],Images[i].T2LCod[1],Images[i].T2LCod[2]);
    	        Vector3f Subval = new Vector3f(0,0,0);
    	        
    	        
    	        pickingRay.subAndAssign(Ff, Nn);
    	        
    	        Subval.subAndAssign(II, Nn);
    	        float scalar = Subval.dot(pickingRay);
    	        float scalar2 = pickingRay.l2Norm();
    	        pickingRay.normalize();
    	        
    	      
    	        pickingRay.scale(scalar/scalar2);
    	        
    	        Vector3f Q = new Vector3f(Nn.x,Nn.y,Nn.z);
    	        Q.add(pickingRay);
    	        
    	        
    	        
    	        float d= (Q.x-Images[i].result[0])*(Q.x-Images[i].result[0])+(Q.y-Images[i].result[1])*(Q.y-Images[i].result[1])+(Q.z-Images[i].result[2])*(Q.z-Images[i].result[2]);
    	        Images[i].TargetLines(near,far,Q);
    	        
    	        if(Images[i].dbit == true)
    	        	d = 0;
    	        
                
    	        if(Math.abs(d)<mindist && Images[i].dbit == false)
    	        {
    	        	mindist = Math.abs(d);
    	        	indx = i;
    	        	keeppos = temppos;
    	        }
    	}
    	
    	
    	
  

    	if(indx != -1)
    	{
        	MoveIndex = indx;
        	
        	Start = position;
        	
        //	Images[MoveIndex].worldCamCent(keeppos);
        	
        	moveTO.x = Images[MoveIndex].result[0];
        	moveTO.y = Images[MoveIndex].result[1];
        	moveTO.z = (float) (Images[MoveIndex].result[2]);
           
        	
        	
        	tempMode = false;
        	
        	
        	float[] npos = new float[4];
        	float[] opos = new float[4];
        	opos[0]=position.x;
        	opos[1]=position.y;
        	opos[2]=position.z;
        	opos[3]=1;
        	
        	Matrix.multiplyMV(npos, 0, mVMatrix, 0, opos, 0);
        	
        	npos[0]/=npos[3];
        	npos[1]/=npos[3];
        	npos[2]/=npos[3];
        	
        	float[] moveit = new float[3];
        	moveit[0]=  moveTO.x-npos[0];
        	moveit[1]=moveTO.y-npos[1];
        	moveit[2]=moveTO.z-npos[2];
        	
        	position.x=npos[0];
        	position.y=npos[1];
        	position.z=npos[2];
        	
        	if(MoveMode == false)
        	{
        	float [] getnowcords = new float[4];
        	float [] suppcords = {0,0,zoom,1};
        	Matrix.multiplyMV(getnowcords, 0, mVMatrix, 0, suppcords, 0);
        	getnowcords[0]/=getnowcords[3];
        	getnowcords[1]/=getnowcords[3];
        	getnowcords[2]/=getnowcords[3];
        	StartPos[0] = getnowcords[0];
        	StartPos[1] = getnowcords[1];
        	StartPos[2] = getnowcords[2];
        	}
        	else
        	{
        	   StartPos[0] = EndPos[0];
        	   StartPos[1] = EndPos[1];
        	   StartPos[2] = EndPos[2];
        	   
        	   StartlookAt[0] = EndlookAt[0];
        	   StartlookAt[1] = EndlookAt[1];
        	   StartlookAt[2] = EndlookAt[2];
        	   
        	}
        	EndPos[0] = Images[MoveIndex].testTran[0]+(zoom*Images[MoveIndex].ImgTransf[8]);
        	EndPos[1] = Images[MoveIndex].testTran[1]+(zoom*Images[MoveIndex].ImgTransf[9]);
        	EndPos[2] = Images[MoveIndex].testTran[2]+(zoom*Images[MoveIndex].ImgTransf[10]);
        	
        	EndlookAt[0] =  Images[MoveIndex].testTran[0]-(zoom*Images[MoveIndex].ImgTransf[8]);
        	EndlookAt[1] = Images[MoveIndex].testTran[1]-(zoom*Images[MoveIndex].ImgTransf[9]);
        	EndlookAt[2] = Images[MoveIndex].testTran[2]-(zoom*Images[MoveIndex].ImgTransf[10]);
        	
        	 Log.i(StartPos[0]+" "+StartPos[1]+" "+StartPos[2], "Start VALUE");
        	 Log.i(EndPos[0]+" "+EndPos[1]+" "+EndPos[2], "ENd VALUE");
        	MoveMode = true;
        	StopLerp = false;
        	
       // 	Matrix.translateM(mVMatrix, 0, moveit[0], moveit[1], moveit[2]);

        	float[] trmat = {0,0,zoom};
           // Matrix.setLookAtM(mVMatrix, 0, Images[MoveIndex].result[0], Images[MoveIndex].result[1],Images[MoveIndex].result[2], 0, 0, 0, up.x, up.y, up.z);
         //  Matrix.translateM(trmat, 0, trmat[0]-moveTO.x, trmat[1]-moveTO.y, trmat[2]-moveTO.z);
  //         Matrix.setLookAtM(mVMatrix, 0, moveTO.x, moveTO.y,moveTO.z, 0, 0, 0, up.x, up.y, up.z);
    //		Log.i("Collision at: "+indx, "Name of file: "+ImgPath[indx]);
    	}
    	
    }
    
    
    
    float[] Lerp()
    {
         float [] midval = {0,0,0,0,0,0};
             
         percentDone += 0.01;
         midval[0] = percentDone*(EndPos[0]- StartPos[0]);
         midval[1] = percentDone*(EndPos[1]- StartPos[1]);
         midval[2] = percentDone*(EndPos[2]- StartPos[2]);
         
         midval[0] += StartPos[0];
         midval[1] += StartPos[1];
         midval[2] += StartPos[2];
         
         midval[3] = percentDone*(EndlookAt[0]- StartlookAt[0]);
         midval[4] = percentDone*(EndlookAt[1]- StartlookAt[1]);
         midval[5] = percentDone*(EndlookAt[2]- StartlookAt[2]);
         
         midval[3] += StartlookAt[0];
         midval[4] += StartlookAt[1];
         midval[5] += StartlookAt[2];
         
         
     //    Log.i(midval[0]+" "+midval[1]+" "+midval[2]+" "+percentDone, "MIDDLE VALUE");
         
         if(percentDone >= 1.0)
         {
        	 percentDone = 0;
        	 StopLerp = true;
        	 going = false;
         }
         
         
         
         return midval;
    }
    
    public void Kaustav()
    {
    	if (tempMode != true)
    	{
    	        tempMode = true;
    	   //     xAngle = 0;
    	} 
    	if(MoveMode == false)
    	{
    		Matrix.setLookAtM(mVMatrix, 0, 0, 0, zoom, 0, 0, 0, 0, 1, 0);
    	
    	}
    	else
    	{
    		EndPos[0] = Images[MoveIndex].testTran[0]+(zoom*Images[MoveIndex].ImgTransf[8]);
        	EndPos[1] = Images[MoveIndex].testTran[1]+(zoom*Images[MoveIndex].ImgTransf[9]);
        	EndPos[2] = Images[MoveIndex].testTran[2]+(zoom*Images[MoveIndex].ImgTransf[10]);
        	
        	EndlookAt[0] =  Images[MoveIndex].testTran[0]-(zoom*Images[MoveIndex].ImgTransf[8]);
        	EndlookAt[1] = Images[MoveIndex].testTran[1]-(zoom*Images[MoveIndex].ImgTransf[9]);
        	EndlookAt[2] = Images[MoveIndex].testTran[2]-(zoom*Images[MoveIndex].ImgTransf[10]);
    		float [] LERPpos = {EndPos[0],EndPos[1],EndPos[2]};
    		float [] LERPlookAt = {EndlookAt[0],EndlookAt[1],EndlookAt[2]};
    		  if(StopLerp == false)
    				  {
    				         LERPpos = Lerp();
    				         LERPlookAt[0] = LERPpos[3];
    				         LERPlookAt[1] = LERPpos[4];
    				         LERPlookAt[2] = LERPpos[5];
    				     //    Log.i(LERPpos[0]+" "+LERPpos[1]+" "+LERPpos[2]+" "+percentDone, "LERP VALUE");
    				  }
    		
    		  Matrix.setLookAtM(mVMatrix, 0,LERPpos[0],LERPpos[1],LERPpos[2],LERPlookAt[0],LERPlookAt[1],LERPlookAt[2],0, 1, 0);
    	}
    	   Matrix.rotateM(mVMatrix, 0, xAngle, 0, 1, 0);
    	   Matrix.rotateM(mVMatrix, 0, yAngle, 0, 0, 1);
    }
 
    @Override
    public void onDrawFrame(GL10 unused) {
    	
        /*
         * 
         * gluLookAt(camset[image_no].t[0] + (image_zoom * camset[image_no].R[6]), 
						camset[image_no].t[1] + (image_zoom * camset[image_no].R[7]),
						camset[image_no].t[2] + (image_zoom * camset[image_no].R[8]),
						camset[image_no].t[0] - camset[image_no].R[6],
						camset[image_no].t[1] - camset[image_no].R[7],
						camset[image_no].t[2] - camset[image_no].R[8], 0, 1, 0);
         * 
         * 
         * 
         * 
         */
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
     //   Matrix.setLookAtM(mVMatrix, 0, 0, 1,-1, 0, 0, 0, 0, 1, 0);
        Vector3f midval = new Vector3f(0,0,0);
        float x,y,z;
        x=0;
        y=0;
        z=0;
        
    if(handsoff == true)
    {
        if(xAngle > 0.5f || xAngle < -0.5f || yAngle > 0.5f || yAngle < -0.5f)
        {
           
        	if(xAngle > 0)
     
           xAngle -= Math.abs(xAngle/10.0f);
        	else
        		xAngle += Math.abs(xAngle/10.0f);
        	
        	if(yAngle > 0)
        	     
                yAngle -= Math.abs(yAngle/10.0f);
             	else
             		yAngle += Math.abs(yAngle/10.0f);
        }
        else
        {
         xAngle = 0;
         yAngle = 0;
        }
    }
        Kaustav();
        
        Matrix.setIdentityM(mModelMatrix, 0);
        
        DrawModel(mModelMatrix);
       
        
    }
    
    
    public void loadImage() throws FileNotFoundException
    { //Texture Property of the Image
    	
    	// Loading the image name from the file output_list
    	BufferedReader ImgReader;
    	String textread;
        
    	Log.i("LoadImage"," output_list reading");
    	
        try {
        	ImgReader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/P3D/output_list.txt"));
			Camnos = (int)Float.parseFloat(ImgReader.readLine());
			
			// binding the textures..
			ImgTexture = new int[Camnos];
	        GLES20.glGenTextures(Camnos, ImgTexture,0);
	        
	        ImgPath = new String[Camnos];
			
			String[] ImagePath = new String[Camnos];
			int start=0;
			
			Bitmap[] CollectionBitmap = new Bitmap[Camnos];
			
			while((textread = ImgReader.readLine()) != null)
			{
			    ImagePath[start]="";
				ImagePath[start]+=textread;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				textureBitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/P3D/"+textread, options);
				Log.i("IMG NAME", textread);
				
				ImgPath[start]=textread;
				
				if(textureBitmap != null)
					Log.i("LoadImage","Texturebitmap NOT null");
				else
					Log.i("LoadImage","Texturebitmap IS null");
				
			//	textureBitmap = Bitmap.createScaledBitmap(textureBitmap, 256, 256, true);
			//	Bitmap scaledBit = Bitmap.createScaledBitmap(textureBitmap,256,256,true);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ImgTexture[start]);
		   	    GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		   	    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureBitmap, GLES20.GL_UNSIGNED_BYTE, 0);
		   	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		        
		  	    textureBitmap.recycle();
		   	    start+=1;
			}
			Log.i("Images in the file are",Camnos+" ");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("LoadImage","1st try block");
			e.printStackTrace();
		}
    	
    	textureBitmap = BitmapFactory.decodeResource(mycontext.getResources(), R.drawable.background);
       
        File root = Environment.getExternalStorageDirectory();
        File imgfile = new File(root.getAbsolutePath()+"/P3D/abc.bmp");
        Log.v("Bitmap inafo:", textureBitmap.getWidth() + ", " + textureBitmap.getHeight());
        Log.i("File path",imgfile.getAbsolutePath());
        
        
        int[] buffer = new int[1];
        GLES20.glGenTextures(1, buffer, 0);
        texture = buffer[0];
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureBitmap, GLES20.GL_UNSIGNED_BYTE, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        
        textureBitmap.recycle();
        
        Log.i("GLRENDERER : ","After the ImgTexture allocation");
	   
	    Images = new ImageTexture[Camnos+1];
		for(int i=0;i<Camnos;i++)
		{
			Images[i] = new ImageTexture(mycontext);
			Images[i].init();
		}
		BufferedReader ImgParam;
		ImgParam = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/P3D/CameraMatrix.txt"));
		Log.i("LoadImage","Imaged inited");
		String Campoints = null;
		try {
			Campoints = ImgParam.readLine();
			int index=0;
			int imgnos=0;
			String[] Fline = null;
			while((Campoints = ImgParam.readLine())!= null)
			{ 
				
				if(index%4 != 3)
				{
					Fline = Campoints.split("\\s+");
					Images[imgnos].updateTrans(Fline,index%4);
				}
				else
				{
					
					Fline = Campoints.split("\\s+");
					Images[imgnos].updateTrans(Fline,index%4);
					 Log.i("Image name:",ImgPath[imgnos]);
					imgnos++;
				}
				Fline = null;
				index++;
			    

				  //  need to read rotation and translation params for each image. Now what do i take cam ceters and then after getting 
				 //   the new center pos what will be the corners. Do i need a normal for that computation....
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("GLRENDERER: ", " Load image exit");
    }
    
    public void DrawModel(float[] mvpMatrix)
    {
        
    	// COLORING THE POINT CLOUD

        GLES20.glUseProgram(mProgram);
       mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, mModel.COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     mModel.vertexStride, mModel.vertexBuffer);
        
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");

        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glVertexAttribPointer(mColorHandle, 4,
                                     GLES20.GL_FLOAT, false,
                                     16, mModel.colorBuffer);

        
       
        
        Matrix.setIdentityM(mRotationMatrix, 0);
        
  //      Matrix.scaleM(mRotationMatrix, 0, Scale[0], Scale[1], Scale[2]);
        
      //  Matrix.rotateM(mRotationMatrix, 0, xAngle, 0, 1, 0);
        
   
        
     //   Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mRotationMatrix, 0);  
        
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        
    //   float[] mtrans = new float[16];
       
      // Matrix.transposeM(mtrans, 0, mMVPMatrix, 0);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");  
        
        GLES20.glDrawArrays(GLES20.GL_POINTS,0,mModel.vertexCount);
        
        
        if(Images[0].DL == true)
	    {
	    
	    	  mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

	          GLES20.glEnableVertexAttribArray(mPositionHandle);
	    	  
	    GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,12, Images[0].Lbuff);
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");
       GLES20.glVertexAttribPointer(mColorHandle, 4,GLES20.GL_FLOAT, true,16, Images[0].LCbuff);


       mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
       MyGLRenderer.checkGlError("glGetUniformLocation");
       GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
       MyGLRenderer.checkGlError("glUniformMatrix4fv");  

       
       GLES20.glDrawArrays(GLES20.GL_LINES,0,2);
       
       
    
	    }
        
        float[] IdMat = new float[16];
        Matrix.setIdentityM(IdMat, 0);

        
        
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);

       
   // HERE is the IMAGE TEXTURE RENDERING 	

   
    //   GLES20.glUseProgram(texProgram);
       
       
   for(int i=0;i<Camnos;i++)
   {
	   GLES20.glUseProgram(texProgram);
	   Matrix.setIdentityM(mModelMatrix, 0);
	   Matrix.translateM(mModelMatrix, 0, Images[i].testTran[0], Images[i].testTran[1], Images[i].testTran[2]);
	//   Matrix.translateM(mModelMatrix, 0, Images[i].TransMat[3], Images[i].TransMat[7], Images[i].TransMat[11]);
	   Matrix.multiplyMM(mModelMatrix, 0, Images[i].ImgTransf, 0, mModelMatrix,0);
	//   Matrix.translateM(mModelMatrix, 0, Images[i].TransMat[3], Images[i].TransMat[7], Images[i].TransMat[11]);
	  
	 //  Matrix.multiplyMM(mModelMatrix, 0, mRotationMatrix, 0, mModelMatrix, 0); 
	//   Matrix.multiplyMM(mModelMatrix, 0, mRotationMatrix, 0, mModelMatrix, 0); 
	  
	//   Matrix.multiplyMM(mModelMatrix,0, Images[i].TransMat, 0, mModelMatrix, 0);
	  
	   
	   Matrix.multiplyMM(mModelMatrix, 0, mVMatrix, 0, mModelMatrix, 0);
	  
       Matrix.multiplyMM(mModelMatrix, 0, mProjMatrix, 0, mModelMatrix, 0);
       
      // Matrix.transposeM(mtrans, 0, mModelMatrix, 0);
       
	   //updateMVP(Images[i].ImgTransf);
       // Here mMOdel is used instead of MVP for storing mvp due to strange raycasting effect it caused. 
	    

  //     Images[i].BBoxColUpdate(((float) i)/Camnos);
       
 //      Log.i("Image name:",ImgPath[i]);
       
       Images[i].worldCamCent(mModelMatrix);
       if(Images[0].DL == true)
       Images[i].LineBuff(mMVPMatrix);
       
  //     if(MoveMode == true && MoveIndex == i)
  //     Log.i(Images[i].result[0]+" "+Images[i].result[1]+" "+Images[i].result[2],i+" MoveIndex at Insidious");
      
       aVerPos = GLES20.glGetAttribLocation(texProgram, "aVerPos");
        if(aVerPos == -1) {
            Log.e("Shader program", "Cudn't find aVerPos");
        }
        GLES20.glEnableVertexAttribArray(aVerPos);

        aVerCol = GLES20.glGetAttribLocation(texProgram, "aVerCol");
        if(aVerCol == -1) {
            Log.e("Error", "Couldn't find aVColor");
        } 
        GLES20.glEnableVertexAttribArray(aVerCol);

        aTexPos = GLES20.glGetAttribLocation(texProgram, "aTexPos");
        if(aTexPos == -1) {
            Log.e("Error", "Failed 2 find aTexPos");
        }
        GLES20.glEnableVertexAttribArray(aTexPos);
        uSamp = GLES20.glGetUniformLocation(texProgram, "uSampler");

        if(uSamp == -1) {
            Log.e("Error", "Couldn't finda uSampler " + uSamp);
        } 
	    mMVPMatrixHandle = GLES20.glGetUniformLocation(texProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation mein error");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv"); 
	   
	    // The Draw part of Image	       	    

	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ImgTexture[i]);
	    GLES20.glUniform1i(uSamp, 0);
	/*    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureBitmap, GLES20.GL_UNSIGNED_BYTE, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
	*/    
	    GLES20.glVertexAttribPointer(aVerPos, 3, GLES20.GL_FLOAT, false, 12, Images[i].texVertexBufferpointer);	    
	    GLES20.glVertexAttribPointer(aVerCol, 3, GLES20.GL_FLOAT, false, 12, Images[i].texColBufferPointer);	    
	    GLES20.glVertexAttribPointer(aTexPos, 2, GLES20.GL_FLOAT, false, 8, Images[i].textureBufferPointer);
	    

	    
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	    
	    
	    
	    
	    GLES20.glUseProgram(mProgram);
	    
	    /*
	     * to draw the blue outline of the box
	     
	    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                                     GLES20.GL_FLOAT, false,
                                     12, Images[i].shadeBlueVertex);
        
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");

        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glVertexAttribPointer(mColorHandle, 4,
                                     GLES20.GL_FLOAT, true,
                                     16, Images[i].shadeBlueColor);
        
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");  
       
           
        */
	   
	    
	    if(Images[i].dbit == false && Images[0].DL == true)
	    {
	    //	Log.i(" Error : ", i+" here ");
	    	

	      	    	   Matrix.setIdentityM(mModelMatrix, 0);
	      	    	  
	      	    	   mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

	      	           GLES20.glEnableVertexAttribArray(mPositionHandle);
	      	     	  
	      	     GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,12, Images[i].T1buff);
	      	     mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");
	      	    GLES20.glVertexAttribPointer(mColorHandle, 4,GLES20.GL_FLOAT, true,16, Images[i].T1Cbuff);


	      	    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	      	    MyGLRenderer.checkGlError("glGetUniformLocation");
	      	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelMatrix, 0);
	      	    MyGLRenderer.checkGlError("glUniformMatrix4fv");  


	     // 	    GLES20.glDrawArrays(GLES20.GL_LINES,0,2);
	      	    
	      	    
	      	    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

	              GLES20.glEnableVertexAttribArray(mPositionHandle);
	        	  
	        GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,12, Images[i].T2buff);
	        mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");
	       GLES20.glVertexAttribPointer(mColorHandle, 4,GLES20.GL_FLOAT, true,16, Images[i].T2Cbuff);


	       mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	       MyGLRenderer.checkGlError("glGetUniformLocation");
	       GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelMatrix, 0);
	       MyGLRenderer.checkGlError("glUniformMatrix4fv");  


	//       GLES20.glDrawArrays(GLES20.GL_LINES,0,2);
	            
	       mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

           GLES20.glEnableVertexAttribArray(mPositionHandle);
     	  
     GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,12, Images[i].Pbuff);
     mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");
    GLES20.glVertexAttribPointer(mColorHandle, 4,GLES20.GL_FLOAT, true,16, Images[i].PCbuff);


    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    MyGLRenderer.checkGlError("glGetUniformLocation");
    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelMatrix, 0);
    MyGLRenderer.checkGlError("glUniformMatrix4fv");  


   // GLES20.glDrawArrays(GLES20.GL_LINES,0,2);
         
	           
	 /*   	
	    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                                     GLES20.GL_FLOAT, false,
                                     12, Images[i].redV);
        
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");

        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glVertexAttribPointer(mColorHandle, 4,
                                     GLES20.GL_FLOAT, true,
                                     16, Images[i].redC);
        
        
        Matrix.setIdentityM(mModelMatrix, 0);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");  
	    
           GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,3);
        */
	   
  
        
     //   GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        // updating the color id for picking
        
    /*    Images[i].BBoxColUpdate(((float)i)/ ((float)Camnos));
        
        GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,12, Images[i].bboxvertexbuffer);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "color");

       GLES20.glVertexAttribPointer(mColorHandle, 4,GLES20.GL_FLOAT, true,16, Images[i].bboxcolorbuffer);


       mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
       MyGLRenderer.checkGlError("glGetUniformLocation");
       GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
       MyGLRenderer.checkGlError("glUniformMatrix4fv");  

       GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,12);
       */
        
        

	    }
	 //   Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mRotationMatrix, 0);
	//    Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
	    
       GLES20.glDisableVertexAttribArray(mPositionHandle);
       GLES20.glDisableVertexAttribArray(mColorHandle);
        
	 //   updateMVP(Images[i].invTransf);
   }
  }
    
 

    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        ratio = (float) width / height;
        
        ViewWidth = width;
        ViewHeight = height;
        
        Log.d("Width and height", width+" "+height);
        
       Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f,1000);
       
   //    viewAngle = (float) (2.0*(float) Math.atan(ratio));
       
      // RayCastInit();
    }
    
  
    
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        IntBuffer params = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params);
        if(params.get(0)==GLES20.GL_TRUE)
        Log.v("Shader compilation","CORRECT");
        else
        	Log.v("Shader compilation","ERRORS");
        return shader;
    }
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    public void changeScale(float scale) {
	//	if (Scale[0] * scale > 2.5f || Scale[0] *scale < 0.5)
	//		return;
		Scale[0] *= scale;Scale[1] *= scale;Scale[2] *= scale;
		
    	float lambda = (float) Math.sqrt(position.x*position.x+position.y*position.y+position.z*position.z); 
    	
    	if(scale>1)
    	zoom+=0.025f;
    	else
    		zoom-=0.025f;
				
		
		
    //    position.x = (float) ((position.x/lambda)* (0.05)+position.x);
   // 	position.y = (float) ((position.y/lambda)* (0.05)+position.y);
    //	position.z = (float) ((position.z/lambda)* (0.05)+position.z);
		
    	
    	
	//	Matrix.setLookAtM(mVMatrix, 0, position.x, position.y,position.z, 0, 0, 0, 0, 1, 0);
		Log.d("SCALE: ", scale+ "");
	}
}

