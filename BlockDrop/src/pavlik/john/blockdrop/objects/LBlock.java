package pavlik.john.blockdrop.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class LBlock extends BlockObject {

	 FloatBuffer vertexBuffer;
	 ShortBuffer drawListBuffer;

	/** This will be used to pass in model texture coordinate information. */
	int mTextureCoordinateHandle;

	/** Size of the texture coordinate data in elements. */
	final int mTextureCoordinateDataSize = 2;

	/** This is a handle to our texture data. */
	int mTextureDataHandle;

	/** Store our model data in a float buffer. */
	 FloatBuffer mLineTextureCoordinates;

	int mPositionHandle;
	int mMVPMatrixHandle;

	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you
	// move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping
	// the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[] mLineTextureCoordinateData = {
			// Front face

			0.0f, 0.25f, // Top left
			0.5f, 0.25f, // Top center
			0.0f, 0.75f, // Center left
			0.0f, 1.0f, // Bottom left
			1.0f, 1.0f, // Bottom right
			1.0f, 0.75f, // Top right
			0.5f, 0.75f, // Center Center

	};

	private final String	TAG							= this.getClass().getSimpleName();
	static float mVertexCoords[] = { 
		    -0.25f, 0.375f, 0.0f, // left top
			0.0f, 0.375f, 0.0f, // center top
			-0.25f, -0.125f, 0.0f, // Center left
			-0.25f, -0.375f, 0.0f, // left bottom
			0.25f, -0.375f, 0.0f,// right bottom
			0.25f, -0.125f, 0.0f,// right top
			0.0f, -0.125f, 0.0f,// center middle
	};

	final short mDrawOrder[] = { 0, 1, 2, 6, 3, 4, 6, 5 };

	float[] mTranslationMatrix = new float[16];

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param mContext
	 */

	public LBlock(Context mContext) {
		super(mContext);
		Log.i(TAG, "Initializing LBlock");

		}

	public void draw(float[] viewMatrix, float[] projectionMatrix) {
		super.draw(viewMatrix, projectionMatrix, mTextureCoordinateDataSize, mLineTextureCoordinates,
				mTextureDataHandle, vertexBuffer, drawListBuffer, mDrawOrder.length,
				GLES20.GL_TRIANGLE_STRIP);
	}

	
	@Override
	public void regenChild() {
		super.regen();
		vertexBuffer = initializeFloatBuffer(mVertexCoords);
		drawListBuffer = initializeShortBuffer(mDrawOrder);

		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		// Load the texture
		mTextureDataHandle = Util.loadTexture(mContext, R.drawable.l);

		mLineTextureCoordinates = initializeFloatBuffer(mLineTextureCoordinateData);
	}
}
