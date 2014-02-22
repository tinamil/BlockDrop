package pavlik.john.blockdrop.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import pavlik.john.blockdrop.opengl.MyRenderer;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class World {
	private final String		TAG							= this.getClass().getSimpleName();
	protected int				mProgram;

	private final int			maxHeight					= 20;
	private final int			minHeight					= 0;
	private final float			minYCoordinate				= -.5f;
	private final float			maxYCoordinate				= .5f;
	private final float			yRange						= maxYCoordinate - minYCoordinate;
	private final float			heightRange					= maxHeight - minHeight;

	private final int			leftLimit					= 0;
	private final int			rightLimit					= 10;
	private float				minXCoordinate				= -.5f;
	private float				maxXCoordinate				= .5f;
	private final float			xRange						= maxXCoordinate - minXCoordinate;
	private final float			widthRange					= rightLimit - leftLimit;

	private final float			mRotationAngle				= 0f;
	private final float			xCoordinate					= 0f;
	private final float			yCoordinate					= 0f;

	// number of coordinates per vertex in this array
	protected static final int	COORDS_PER_VERTEX			= 3;

	private FloatBuffer			vertexBuffer;
	private ShortBuffer			drawListBuffer;

	/** Size of the texture coordinate data in elements. */
	private final int			mTextureCoordinateDataSize	= 2;

	/** This is a handle to our texture data. */
	private int					mTextureDataHandle;

	/** Store our model data in a float buffer. */
	private FloatBuffer			mTextureCoordinates;

	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you
	// move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping
	// the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[]				mLineTextureCoordinateData	= {
															// Front face
			0.0f, 0.0f, // Bottom left
			0.0f, 1.0f, // Top Left
			1.0f, 1.0f, // Top right
			1.0f, 0.0f										// Bottom right
															};

	static float				mLineCoords[]				= { -0.125f, 0.5f, 0.0f, // top left
			-0.125f, -0.5f, 0.0f, // bottom left
			0.125f, -0.5f, 0.0f, // bottom right
			0.125f, 0.5f, 0.0f								};									// top
	// right

	private final short			mDrawOrder[]				= { 0, 2, 3, 0, 1, 2 };			// order
																								// to
																								// draw
																								// vertices

	float[]						mTranslationMatrix			= new float[16];

	private final Context		mContext;

	public World(Context mContext) {
		this.mContext = mContext;
	}

	protected void draw(float[] viewProjectionMatrix) {
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
		Matrix.multiplyMM(mMVPMatrix, 0, viewProjectionMatrix, 0, mModelMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix, and stores the result in
		// the MVP matrix
		// (which now contains model * view * projection).
		// Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);

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
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder.length, GLES20.GL_UNSIGNED_SHORT,
				drawListBuffer);
		MyRenderer.checkGlError("glDrawElements");

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		MyRenderer.checkGlError("glDisableVertexAttribArray");
	}

	protected void regen() {
		Log.i(TAG, "Regenerating block textures and data");

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

		vertexBuffer = Util.initializeFloatBuffer(mLineCoords);
		drawListBuffer = Util.initializeShortBuffer(mDrawOrder);

		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		// Load the texture
		mTextureDataHandle = Util.loadTexture(mContext, R.drawable.i);

		mTextureCoordinates = Util.initializeFloatBuffer(mLineTextureCoordinateData);
	}

	private float getHeightRange() {
		return heightRange;
	}

	private float getYRange() {
		return yRange;
	}

	int getMaxHeight() {
		return maxHeight;
	}

	int getMinHeight() {
		return minHeight;
	}

	int getRightLimit() {
		return rightLimit;
	}

	int getLeftLimit() {
		return leftLimit;
	}

	private float getXRange() {
		return xRange;
	}

	private float getWidthRange() {
		return widthRange;
	}

	public void setRatio(float ratio) {
		minXCoordinate = -ratio;
		maxXCoordinate = ratio;
	}

	public int translateYCoordinate(float yCoordinate) {
		final float yBlock = (yCoordinate + getYRange() / 2.0f) * getHeightRange() * getYRange();
		return Math.round(yBlock);
	}

	public int translateXCoordinate(float xCoordinate) {
		final float xBlock = (xCoordinate + getXRange() / 2.0f) * getWidthRange() * getXRange();
		return Math.round(xBlock);
	}

	public float translateYBlock(int yBlock) {
		float yRange = getYRange();
		return (yBlock * 1.0f / getHeightRange() * yRange) - yRange / 2.0f;
	}

	public float translateXBlock(int xBlock) {
		float xRange = getXRange();
		return (xBlock * 1.0f / getWidthRange() * xRange) - xRange / 2.0f;
	}
}
