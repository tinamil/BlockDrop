package pavlik.john.blockdrop.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import pavlik.john.blockdrop.opengl.MyRenderer;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public abstract class BlockObject {

	private final String		TAG					= this.getClass().getSimpleName();
	protected int				mProgram;

	/** How many bytes per float. */
	protected final int			mBytesPerFloat		= 4;
	/** How many bytes per short. */
	protected final int			mBytesPerShort		= 2;

	private transient int		mRotationAngle		= 0;
	private float				xCoordinate			= 0;
	private float				yCoordinate			= 0;

	private boolean				initialized			= false;

	// number of coordinates per vertex in this array
	protected static final int	COORDS_PER_VERTEX	= 3;

	protected final Context		mContext;

	public BlockObject(Context mContext) {
		this.mContext = mContext;
	}

	public synchronized void setXCoordinate(float xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public synchronized void setYCoordinate(float yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	protected FloatBuffer initializeFloatBuffer(float[] data) {
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				data.length * mBytesPerFloat);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer returnValue = bb.asFloatBuffer();
		returnValue.put(data);
		returnValue.position(0);
		return returnValue;
	}

	protected ShortBuffer initializeShortBuffer(short[] data) {
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 2 bytes per short)
				data.length * mBytesPerShort);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer returnValue = bb.asShortBuffer();
		returnValue.put(data);
		returnValue.position(0);
		return returnValue;
	}

	protected void draw(float[] viewMatrix, float[] projectionMatrix,
			int mTextureCoordinateDataSize, FloatBuffer mTextureCoordinates,
			int mTextureDataHandle, FloatBuffer vertexBuffer, ShortBuffer drawListBuffer,
			int drawListBufferSize, int drawType) {

		if(!initialized){
			regenChild();
		}
		
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);
		MyRenderer.checkGlError("glUseProgram");

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		float[] scratch = new float[16];
		Matrix.setIdentityM(scratch, 0);
		float[] scratch2 = new float[16];
		Matrix.setIdentityM(scratch2, 0);

		// Scale the object down
		float[] scaleMatrix = new float[16];
		Matrix.setIdentityM(scaleMatrix, 0);
		Matrix.scaleM(scaleMatrix, 0, 0.3f, 0.3f, 0.3f);
		Matrix.multiplyMM(scratch, 0, scaleMatrix, 0, mModelMatrix, 0);

		// Set the correct translation for the object
		float[] translationMatrix = new float[16];
		Matrix.setIdentityM(translationMatrix, 0);
		Matrix.translateM(translationMatrix, 0, xCoordinate, yCoordinate, 0f);
		Matrix.multiplyMM(scratch2, 0, translationMatrix, 0, scratch, 0);

		// Set the correct rotation for the object
		float[] rotationMatrix = new float[16];
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.rotateM(rotationMatrix, 0, mRotationAngle, 0f, 0f, 1f);
		Matrix.multiplyMM(mModelMatrix, 0, rotationMatrix, 0, scratch2, 0);

		float[] mMVPMatrix = new float[16];
		// This multiplies the view matrix by the model matrix, and stores the result in the MVP
		// matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix, and stores the result in
		// the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);

		// get handle to shape's transformation matrix
		int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		MyRenderer.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		MyRenderer.checkGlError("glGetUniformMatrix4fv");

		// get handle to vertex shader's vPosition member
		int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		MyRenderer.checkGlError("glGetAttribLocation");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		MyRenderer.checkGlError("glEnableVertexAttribArray");

		// Pass in the texture coordinate information
		int mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

		MyRenderer.checkGlError("glGetAttribLocation");
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize,
				GLES20.GL_FLOAT, false, 0, mTextureCoordinates);
		MyRenderer.checkGlError("glVertexAttribPointer");

		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		MyRenderer.checkGlError("glActiveTexture");

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
		MyRenderer.checkGlError("glBindTexture");

		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		MyRenderer.checkGlError("glEnableVertexAttribArray");

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0,
				vertexBuffer);
		MyRenderer.checkGlError("glVertexAttribPointer");

		// Draw the object
		GLES20.glDrawElements(drawType, drawListBufferSize, GLES20.GL_UNSIGNED_SHORT,
				drawListBuffer);
		MyRenderer.checkGlError("glDrawElements");

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		MyRenderer.checkGlError("glDisableVertexAttribArray");
	}

	public abstract void draw(float[] viewMatrix, float[] projectionMatrix);

	public synchronized void setRotation(int degrees) {
		mRotationAngle = degrees;
	}

	public abstract void regenChild();

	protected void regen() {
		Log.i(TAG, "Regenerating block textures and data");
		initialized = true;
		// prepare shaders and OpenGL program
		int vertexShader = Util.compileShader(GLES20.GL_VERTEX_SHADER, Util
				.readTextFileFromRawResource(mContext, R.raw.vertshader));
		int fragmentShader = Util.compileShader(GLES20.GL_FRAGMENT_SHADER, Util
				.readTextFileFromRawResource(mContext, R.raw.fragshader));

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram); // create OpenGL program executables
		// Get the compilation status.
		final int[] compileStatus = new int[1];
		GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, compileStatus, 0);
		if (compileStatus[0] == 0) {
			Log.e(TAG, "Error linking shaders: " + GLES20.glGetProgramInfoLog(mProgram));
		}
	}

}
