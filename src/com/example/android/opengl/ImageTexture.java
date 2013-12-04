package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class ImageTexture {
	
	
	public FloatBuffer texVertexBufferpointer;
	public FloatBuffer texColBufferPointer;
	public FloatBuffer textureBufferPointer;
	public FloatBuffer shadeBlueVertex;
	public FloatBuffer shadeBlueColor;
	public FloatBuffer bboxvertexbuffer;
	public FloatBuffer bboxcolorbuffer;
	public FloatBuffer redV;
	public FloatBuffer redC;
	public FloatBuffer redTV;
	public Context mycontext;
	
	public float c_f;
    public float [] ImgCent = {0.0f, 0.0f, 0.0f, 1.0f};
    public float [] ImgL = {-.10f,  -.10f, 0.0f,1.0f};
    public float [] ImgR = {-.10f,  .10f, 0.0f,1.0f}; 
    public float [] ImgC =  {0.0f, 0.0f, -0.1f,1.0f}; 
    public float [] ImgCC = {-.10f,  -.10f, 0.0f,-.10f,  .10f, 0.0f,0.1f, 0.1f, 0.0f};
    
    // Debugging
    public float [] LineCod;
    public float [] T1LCod;
    public float [] T2LCod;
    public float [] touchP = new float[4];
    public FloatBuffer Lbuff;
    public FloatBuffer LCbuff;
    public FloatBuffer T1buff;
    public FloatBuffer T1Cbuff;
    public FloatBuffer T2buff;
    public FloatBuffer T2Cbuff;
    public FloatBuffer Pbuff;
    public FloatBuffer PCbuff;
    public float[] PPoint;
    
    public boolean DL;
    public float[] DCL= {1.0f,0.0f,0.0f,1.0f,   1.0f,0.0f, 0.0f,1.0f};
    public float[] TCL= {0,0,1,1, 0,0,1,1};
    
    float [] near = new float[4];
    float [] far = new float[4];
    
     
    public float [] ImgNorm ;
    public float [] tempNorm = {0f, 0f, -1f, 1f};
	public float [] ImgTransf = new float[16];
	public float [] invTransf = new float[16];
	public float [] TransMat = new float[16];
	public float [] Tmat = new float[4];
	public float [] result = new float[4];

	public float [][] A = new float[3][3];
	public float [] testTran = new float[3];
	public float [][] result1 = new float[3][3];
    public boolean dbit;
	public double invdet;
	public double determinant;
	
	
   
	public void DLCOD(Vector3f first, Vector3f  second, boolean yes)
	{
	      if(yes == true)
	      {
	         LineCod = new float[6];
	         LineCod[0] = first.x;LineCod[1] = first.y;LineCod[2] = first.z;
	         LineCod[3] = second.x;LineCod[4] = second.y;LineCod[5] = second.z;
	         Lbuff = initFloatBuffer(LineCod);
	         LCbuff = initFloatBuffer(DCL);	         	         
	         DL = true;
	      }
	     
	}
	
	public void TargetLines(Vector3f target1, Vector3f target2, Vector3f touchPoint)
	{
	
		near[0] = target1.x;near[1] = target1.y;near[2] = target1.z;near[3] = 1;
		far[0] = target2.x;far[1] = target2.y;far[2] = target2.z;far[3] = 1;
		touchP[0] = touchPoint.x;touchP[1] = touchPoint.y;touchP[2] = touchPoint.z;touchP[3]=1;
		
	/*
	   T1LCod = new float[6];
	   T1LCod[0] = result[0];T1LCod[1] = result[1];T1LCod[2] = result[2];
	   T1LCod[3] = target1.x;T1LCod[4] = target1.y;T1LCod[5] = target1.z;
	   
	   T1buff = initFloatBuffer(T1LCod);
       T1Cbuff = initFloatBuffer(DCL);
       
       T2LCod = new float[6];
	   T2LCod[0] = result[0];T2LCod[1] = result[1];T2LCod[2] = result[2];
	   T2LCod[3] = target2.x;T2LCod[4] = target2.y;T2LCod[5] = target2.z;
	
	   T2buff = initFloatBuffer(T2LCod);
       T2Cbuff = initFloatBuffer(DCL);
       */
	   
	}
	
	public void LineBuff(float[] Mmvp)
	{
		float [] temp = new float[4]; T1LCod = new float[6]; T2LCod = new float[6];
		PPoint = new float[6];
		
		Matrix.multiplyMV(temp, 0, Mmvp, 0, near, 0);
		T1LCod[0] = temp[0]/temp[3];T1LCod[1] = temp[1]/temp[3];T1LCod[2] = temp[2]/temp[3];
		T1LCod[3] = result[0]; T1LCod[4] = result[1];T1LCod[5] = result[2];
		
		  T1buff = initFloatBuffer(T1LCod);
	       T1Cbuff = initFloatBuffer(DCL);
		
	    float[] temp2 = new float[4];
	       
		Matrix.multiplyMV(temp2, 0, Mmvp, 0, far, 0);
		T2LCod[0] = temp2[0]/temp2[3];T2LCod[1] = temp2[1]/temp2[3];T2LCod[2] = temp2[2]/temp2[3];
		T2LCod[3] = result[0]; T2LCod[4] = result[1];T2LCod[5] = result[2];
		
		  T2buff = initFloatBuffer(T2LCod);
	       T2Cbuff = initFloatBuffer(DCL);
		
		Matrix.multiplyMV(temp2, 0, Mmvp, 0, touchP, 0);
		PPoint[0] = touchP[0]; PPoint[1] = touchP[1]; PPoint[2] = touchP[2];
	//	PPoint[0] = temp2[0]/temp2[3];PPoint[1] = temp2[1]/temp2[3];PPoint[2] = temp2[2]/temp2[3];
		PPoint[3] = result[0];PPoint[4] = result[1];PPoint[5] = result[2];
		
		  Pbuff = initFloatBuffer(PPoint);
	       PCbuff = initFloatBuffer(TCL);
	}
	
	public float[] backcord={0,0,-0.05f,1.0f};
	public float[] cambackcords = new float[4];
	
	public void calcBack(float[] M)
	{
	      Matrix.multiplyMV(cambackcords, 0, M, 0, backcord, 0);
	      backcord[0] = backcord[0]/backcord[3];
	      backcord[1] = backcord[1]/backcord[3];
	      backcord[2] = backcord[2]/backcord[3];
	      
	}
	
	private float[] bboxvertices = 
		{
		 -0.1f,-0.1f,0.05f,
		 -0.1f,0.1f,0.05f,
		 0.1f,0.1f,0.05f,
		 -.10f, -.10f, 0.05f, 
         .10f, -.10f, 0.05f, 
         .10f,  .10f, 0.05f,
         
         -0.1f,-0.1f,-0.05f,
		 -0.1f,0.1f,-0.05f,
		 0.1f,0.1f,-0.05f,
		 -.10f, -.10f, -0.05f, 
         .10f, -.10f, -0.05f, 
         .10f,  .10f, -0.05f,
         
		};
	
	private float[] TRvertex;
	private float[] TRcolor = { 1.0f, 0.0f , 0.0f,1.0f, 1.0f, 0.0f , 0.0f,1.0f, 1.0f, 0.0f , 0.0f,1.0f};
	
	private float[] vertices = {-.10f,  -.10f, 0.0f, 
            -.10f,  .10f, 0.0f, 
             .10f, .10f, 0.0f, 
            -.10f, -.10f, 0.0f, 
             .10f, -.10f, 0.0f, 
             .10f,  .10f, 0.0f};
	private float[] textureVertices = {	 
			0.0f, 0.0f, 
	        0.0f, 1.0f, 
	        1.0f, 1.0f, 
			0.0f, 0.0f, 
	        1.0f, 0.0f, 
	        1.0f, 1.0f
	         
	};
	private float[] vertices2 = {-.10f, -.10f, 0.0f,
			-.10f, .10f, 0.0f,
			 0.0f, 0.0f, -0.09f,
			 .10f, -.10f, 0.0f,
			-.10f, -0.10f , 0.0f,
			 0f, 0.0f, -0.09f,
			.10f, -.10f, 0.0f,
			.10f, 0.10f , 0.0f,
			 0.f, 0.0f, -0.09f,
			-.10f, +.10f, 0.0f,
			.10f, 0.10f , 0.0f,
			 0.0f, 0.0f,-0.09f
			};
	private float[] newvertices;
	private float colors2[]= 
		{
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
			0.0f,0.0f,1.0f,0.1f,
		};
private float[] bboxcolors = {
        0.0f, 0.0f, 1.0f,1.0f,
        0.0f, 0.0f, 1.0f,1.0f,
        0.0f, 0.0f, 1.0f,1.0f,
        0.0f, 0.0f, 1.0f,1.0f
        };



public void worldCamCent(float[] Imgmvp)
{		
	

	dbit = false;
	
	TRvertex = new float[9];
	
	Matrix.multiplyMV(result, 0, Imgmvp, 0, ImgL, 0);
	TRvertex[0]=result[0]/result[3];
	TRvertex[1]=result[1]/result[3];
	TRvertex[2]=result[2]/result[3];
	
	if(result[3]==0)
		dbit = true;
	
	Matrix.multiplyMV(result, 0, Imgmvp, 0, ImgR, 0);
	TRvertex[3]=result[0]/result[3];
	TRvertex[4]=result[1]/result[3];
	TRvertex[5]=result[2]/result[3];
	
	if(result[3]==0 || Math.abs(TRvertex[0]-TRvertex[3])>1.5 || Math.abs(TRvertex[1]-TRvertex[4])>1.5 || Math.abs(TRvertex[2]-TRvertex[5])>1.5)
		dbit = true;
	
	Matrix.multiplyMV(result, 0, Imgmvp, 0, ImgC, 0);
	TRvertex[6]=result[0]/result[3];
	TRvertex[7]=result[1]/result[3];
	TRvertex[8]=result[2]/result[3];
	
	if(result[3]==0 || Math.abs(TRvertex[0]-TRvertex[6])>1.5 || Math.abs(TRvertex[1]-TRvertex[7])>1.5 || Math.abs(TRvertex[2]-TRvertex[8])>1.5)
		dbit = true;
	
	if(Math.abs(TRvertex[3]-TRvertex[6])>1.5 || Math.abs(TRvertex[4]-TRvertex[7])>1.5 || Math.abs(TRvertex[5]-TRvertex[8])>1.5)
	dbit=true;
	
	 redV = initFloatBuffer(TRvertex);
	 
//	 Log.i("DetValue:", determinant+" ");
//	 Log.i("Result:1", TRvertex[0]+" "+TRvertex[1]+" "+TRvertex[2]);
//	 Log.i("Result:2", TRvertex[3]+" "+TRvertex[4]+" "+TRvertex[5]);
//	 Log.i("Result:3", TRvertex[6]+" "+TRvertex[7]+" "+TRvertex[8]);

	 
	 
	Matrix.multiplyMV(result, 0, Imgmvp, 0, ImgCent, 0);
	result[0]/= result[3];
	result[1]/= result[3];
	result[2]/= result[3];
//	Log.i("Result:", result[0]+" "+result[1]+" "+result[2]);
	
		
}

public ImageTexture(Context context)
{
	c_f=0.0f;
	mycontext = context;
	Matrix.setIdentityM(ImgTransf, 0);
	Matrix.setIdentityM(TransMat, 0);
}

public void BBoxColUpdate(float value)
{
	c_f = value;
//	bboxcolorbuffer = initFloatBuffer(bboxcolors); 
	
}

public void updateTrans(String[] Rot, int ind)
{
	if(ind<3)
	{
		
	  for(int i=0;i<3;i++)
	  {
		//  Log.i("updateTrans",ind+" indvalue");
		ImgTransf[4*ind+i]= Float.parseFloat(Rot[i]);
		//  ImgTransf[ind+4*i]= Float.parseFloat(Rot[i]);
		  A[ind][i] = ImgTransf[4*ind+i];
		  
		  
		  
		 // ImgNorm[ind] += ImgTransf[ind*4+i]*tempNorm[i];
				  /// update Image Normal
	  }
//	  Log.i("Rotmat:", Rot[0]+" "+Rot[1]+" "+Rot[2]);
	}
	else
	{
		for(int i=0;i<3;i++)
		{
			
			Tmat[i] = (float) (-1.0 * Float.parseFloat(Rot[i]));
			testTran[i] = Tmat[i];
		
			
		}
		
	//	Log.i("TranslationMat ",Rot[0]+" "+Rot[1]+" "+Rot[2]);
		Tmat[3] = (float) 1.0;
		
        ImgTransf[3]= 0;
        ImgTransf[7]= 0;
        ImgTransf[11]= 0;
        ImgTransf[15]=1;
        ImgTransf[14]=0;
        ImgTransf[13]=0;
        ImgTransf[12]=0;
		
		Matrix.invertM(invTransf, 0, ImgTransf, 0);
		
		float [] newtmat = new float[4];
		
		Matrix.multiplyMV(newtmat, 0, invTransf, 0, Tmat, 0);	
		Matrix.translateM(TransMat, 0, newtmat[0]/newtmat[3], newtmat[1]/newtmat[3], newtmat[2]/newtmat[3]);
		
		
		// Inverting Rot (3X3) then multiplying translation to obtain truetranslation
		determinant = +A[0][0]*(A[1][1]*A[2][2]-A[2][1]*A[1][2])
                -A[0][1]*(A[1][0]*A[2][2]-A[1][2]*A[2][0])
                +A[0][2]*(A[1][0]*A[2][1]-A[1][1]*A[2][0]);

		invdet = 1/determinant;
     //   invdet = 1;
		
		
        result1[0][0] =  (float) ((A[1][1]*A[2][2]-A[2][1]*A[1][2])*invdet);
        result1[0][1] = (float) (-(A[0][1]*A[2][2]-A[0][2]*A[2][1])*invdet);
        result1[0][2] =  (float) ((A[0][1]*A[1][2]-A[0][2]*A[1][1])*invdet);
        result1[1][0] = (float) (-(A[1][0]*A[2][2]-A[1][2]*A[2][0])*invdet);
        result1[1][1] =  (float) ((A[0][0]*A[2][2]-A[0][2]*A[2][0])*invdet);
        result1[1][2] = (float) (-(A[0][0]*A[1][2]-A[1][0]*A[0][2])*invdet);
        result1[2][0] =  (float) ((A[1][0]*A[2][1]-A[2][0]*A[1][1])*invdet);
        result1[2][1] = (float) (-(A[0][0]*A[2][1]-A[2][0]*A[0][1])*invdet);
        result1[2][2] =  (float) ((A[0][0]*A[1][1]-A[1][0]*A[0][1])*invdet);
		

        
        for(int i=0;i<3;i++)
        {

        		float sum=0;
        		for(int k=0;k<3;k++)
        		{
        			sum+= result1[i][k]*Tmat[k];
        		}
        		testTran[i] = sum;
        }
        
	}
	

}

public void init()
{
	
 //   texColBufferPointer = initFloatBuffer(bboxcolors);
    textureBufferPointer = initFloatBuffer(textureVertices);        
    texVertexBufferpointer = initFloatBuffer(vertices);
    shadeBlueVertex = initFloatBuffer(vertices2);
    shadeBlueColor = initFloatBuffer(colors2);
    texColBufferPointer = initFloatBuffer(bboxcolors);
    redC = initFloatBuffer(TRcolor);
    redTV = initFloatBuffer(ImgCC);
 //   bboxvertexbuffer = initFloatBuffer(bboxvertices);
    
}


private FloatBuffer initFloatBuffer(float[] data) {
    
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4); //one float size is 4 bytes
    byteBuffer.order(ByteOrder.nativeOrder()); //byte order must be native
    FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    floatBuffer.put(data);
    floatBuffer.position(0);
    
    return floatBuffer;
}

}
