package pavlik.john.blockdrop.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class TBlock extends BlockObject {

	FloatBuffer				vertexBuffer;
	ShortBuffer				drawListBuffer;

	/** This will be used to pass in model texture coordinate information. */
	int						mTextureCoordinateHandle;

	/** Size of the texture coordinate data in elements. */
	final int				mTextureCoordinateDataSize	= 2;

	/** This is a handle to our texture data. */
	int						mTextureDataHandle;

	/** Store our model data in a float buffer. */
	FloatBuffer				mLineTextureCoordinates;

	int						mPositionHandle;
	int						mMVPMatrixHandle;

	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you
	// move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping
	// the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[]			mLineTextureCoordinateData	= {
														// Front face

			0.00f, 1.00f, // left bottom (0)
			0.00f, 0.50f, // left middle (1)
			0.25f, 0.50f, // center-left middle (2)
			0.25f, 0.00f, // center-left top (3)
			0.50f, 0.00f, // center-right top (4)
			0.50f, 0.50f, // center-right middle (5)
			0.75f, 0.50f, // right middle (6)
			0.75f, 1.00f, // right bottom (7)

														};

	private final String	TAG							= this.getClass().getSimpleName();
	static float			mVertexCoords[]				= {
														// Vertex coordinates
			-1.50f, -0.50f, 0.0f,// left bottom (0)
			-1.50f, 0.50f, 0.0f,// left middle (1)
			-0.5f, 0.50f, 0.0f,// center-left middle (2)
			-0.5f, 1.50f, 0.0f,// center-left top (3)
			0.5f, 1.50f, 0.0f, // center-right top (4)
			0.5f, 0.50f, 0.0f, // Center-right middle (5)
			1.50f, 0.50f, 0.0f,// right middle (6)
			1.50f, -0.50f, 0.0f, // Right bottom (7)
														};

	final short				mDrawOrder[]				= { 1, 0, 5, 7, 6, 2, 5, 3, 4 };

	float[]					mTranslationMatrix			= new float[16];

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param mContext
	 */

	public TBlock(Context mContext) {
		super(mContext);
		Log.i(TAG, "Initializing TBlock");
	}

	public void draw(float[] viewMatrix, float[] projectionMatrix) {
		super.draw(viewMatrix, projectionMatrix, mTextureCoordinateDataSize, mLineTextureCoordinates,
				mTextureDataHandle, vertexBuffer, drawListBuffer, mDrawOrder.length,
				GLES20.GL_TRIANGLE_STRIP);
	}

	@Override
	public void regen() {
		super.regen();

		vertexBuffer = initializeFloatBuffer(mVertexCoords);
		drawListBuffer = initializeShortBuffer(mDrawOrder);

		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		// Load the texture
		mTextureDataHandle = Util.loadTexture(mContext, R.drawable.t);

		mLineTextureCoordinates = initializeFloatBuffer(mLineTextureCoordinateData);

	}
}
