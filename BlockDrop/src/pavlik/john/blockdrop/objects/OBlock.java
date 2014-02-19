/*
 * Copyright (C) 2011 The Android Open Source Project
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
package pavlik.john.blockdrop.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.R;
import pavlik.john.blockdrop.Util;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class OBlock extends BlockObject {

	private FloatBuffer		vertexBuffer;
	private ShortBuffer		drawListBuffer;

	/** Size of the texture coordinate data in elements. */
	private final int		mTextureCoordinateDataSize	= 2;

	/** This is a handle to our texture data. */
	private int				mTextureDataHandle;

	/** Store our model data in a float buffer. */
	private FloatBuffer		mCubeTextureCoordinates;

	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you
	// move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping
	// the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[]			cubeTextureCoordinateData	= {
														// Front face
			0.0f, 0.0f, // Bottom left
			0.0f, 1.0f, // Top left
			1.0f, 1.0f, // Bottom right
			0.0f, 1.0f, // Top left
			1.0f, 1.0f, // Top right
			1.0f, 0.0f									// Bottom right
														};

	private final String	TAG							= this.getClass().getSimpleName();
	static float			squareCoords[]				= { -.5f, .5f, 0.0f, // top left
			-.5f, -.5f, 0.0f, // bottom left
			.5f, -.5f, 0.0f, // bottom right
			.5f, .5f, 0.0f								};									// top
																							// right

	private final short		drawOrder[]					= { 0, 2, 3, 0, 1, 2 };			// order
																							// to
																							// draw
																							// vertices

	float[]					mTranslationMatrix			= new float[16];

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param mContext
	 */
	public OBlock(Context mContext) {
		super(mContext);
		Log.i(TAG, "Initializing OBlock");

	}

	public void draw(float[] viewMatrix, float[] projectionMatrix) {
		super.draw(viewMatrix, projectionMatrix, mTextureCoordinateDataSize, mCubeTextureCoordinates,
				mTextureDataHandle, vertexBuffer, drawListBuffer, drawOrder.length,
				GLES20.GL_TRIANGLES);
	}


	@Override
	public void regenChild() {
		super.regen();
		vertexBuffer = initializeFloatBuffer(squareCoords);

		drawListBuffer = initializeShortBuffer(drawOrder);

		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		// Load the texture
		mTextureDataHandle = Util.loadTexture(mContext, R.drawable.o);

		mCubeTextureCoordinates = initializeFloatBuffer(cubeTextureCoordinateData);
	}

}