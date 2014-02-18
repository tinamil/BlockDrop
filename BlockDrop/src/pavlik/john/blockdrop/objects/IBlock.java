package pavlik.john.blockdrop.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class IBlock extends BlockObject {
	private FloatBuffer		vertexBuffer;
	private ShortBuffer		drawListBuffer;

	/** Size of the texture coordinate data in elements. */
	private final int		mTextureCoordinateDataSize	= 2;

	/** This is a handle to our texture data. */
	private int				mTextureDataHandle;

	/** Store our model data in a float buffer. */
	private FloatBuffer		mLineTextureCoordinates;

	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you
	// move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping
	// the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[]			mLineTextureCoordinateData	= {
														// Front face
			0.0f, 0.0f, // Bottom left
			0.0f, 1.0f, // Top Left
			1.0f, 1.0f, // Top right
			1.0f, 0.0f									// Bottom right
														};

	private final String	TAG							= this.getClass().getSimpleName();
	static float			mLineCoords[]				= { 
		    -0.125f,  0.5f, 0.0f, // top left
		    -0.125f, -0.5f, 0.0f, // bottom left
			 0.125f, -0.5f, 0.0f, // bottom right
			 0.125f,  0.5f, 0.0f							};									// top
																							// right

	private final short		mDrawOrder[]				= { 0, 2, 3, 0, 1, 2 };			// order
																							// to
																							// draw
																							// vertices

	float[]					mTranslationMatrix			= new float[16];

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param mContext
	 */

	public IBlock(Context mContext) {
		super(mContext);
		Log.i(TAG, "Initializing IBlock");
	}

	public void draw(float[] viewMatrix, float[] projectionMatrix) {
		super.draw(viewMatrix, projectionMatrix, mTextureCoordinateDataSize, mLineTextureCoordinates,
				mTextureDataHandle, vertexBuffer, drawListBuffer, mDrawOrder.length,
				GLES20.GL_TRIANGLES);
	}


	@Override
	public void regen() {
		super.regen();

		vertexBuffer = initializeFloatBuffer(mLineCoords);
		drawListBuffer = initializeShortBuffer(mDrawOrder);

		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		// Load the texture
		mTextureDataHandle = Util.loadTexture(mContext, R.drawable.i);

		mLineTextureCoordinates = initializeFloatBuffer(mLineTextureCoordinateData);
	}
}
