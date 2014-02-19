package pavlik.john.blockdrop.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import pavlik.john.blockdrop.opengl.MyRenderer;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class ZBlock extends BlockObject {

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

			0.75f, 1.00f, // right bottom (0)
			0.75f, 0.50f, // right middle (1)
			0.50f, 0.50f, // center-right middle (2)
			0.50f, 0.00f, // center-right top (3)
			0.00f, 0.00f, // left top (4)
			0.00f, 0.50f, // left middle (5)
			0.25f, 0.50f, // Center-left middle (6)
			0.25f, 1.00f, // center-left bottom (7)

														};

	private final String	TAG							= this.getClass().getSimpleName();

	static float			mVertexCoords[]				= {
														// Vertex coordinates
			0.375f, -0.25f, 0.0f,// right bottom (0)
			0.375f, 0.00f, 0.0f,// right middle (1)
			0.025f, 0.00f, 0.0f,// center-right middle (2)
			0.025f, 0.25f, 0.0f,// center-right top (3)
			-0.375f, 0.25f, 0.0f, // left top (4)
			-0.375f, 0.00f, 0.0f,// left middle (5)
			-0.125f, 0.00f, 0.0f, // Center-left middle (6)
			-0.125f, -0.25f, 0.0f, // center-left bottom (7)
														};

	final short				mDrawOrder[]				= { 1, 0, 2, 7, 6, 2, 3, 5, 4 };

	float[]					mTranslationMatrix			= new float[16];

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param mContext
	 */

	public ZBlock(Context mContext) {
		super(mContext);
		Log.i(TAG, "Initializing ZBlock");
	}

	public void draw(float[] viewProjectionMatrix) {
		super.draw(viewProjectionMatrix, mTextureCoordinateDataSize,
				mLineTextureCoordinates, mTextureDataHandle, vertexBuffer, drawListBuffer,
				mDrawOrder.length, GLES20.GL_TRIANGLE_STRIP);
	}

	@Override
	public void regenChild() {
		super.regen();
		vertexBuffer = initializeFloatBuffer(mVertexCoords);
		drawListBuffer = initializeShortBuffer(mDrawOrder);

		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);
		MyRenderer.checkGlError("glGenTextures");

		// Load the texture
		mTextureDataHandle = Util.loadTexture(mContext, R.drawable.z);

		mLineTextureCoordinates = initializeFloatBuffer(mLineTextureCoordinateData);
	}
}
