package pavlik.john.blockdrop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pavlik.john.blockdrop.opengl.MyRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class Util {
	private static final String	TAG				= "Util";

	/** How many bytes per float. */
	protected final static int	mBytesPerFloat	= 4;
	/** How many bytes per short. */
	protected final static int	mBytesPerShort	= 2;

	public static int loadTexture(final Context context, final int resourceId) {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);
		MyRenderer.checkGlError("glGenTextures");

		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId,
					options);

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			MyRenderer.checkGlError("glBindTexture");

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_NEAREST);
			MyRenderer.checkGlError("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
					GLES20.GL_NEAREST);
			MyRenderer.checkGlError("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
					GLES20.GL_CLAMP_TO_EDGE);
			MyRenderer.checkGlError("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
					GLES20.GL_CLAMP_TO_EDGE);
			MyRenderer.checkGlError("glTexParameteri");

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			MyRenderer.checkGlError("texImage2D");

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public static String readTextFileFromRawResource(final Context context, final int resourceId) {
		final InputStream inputStream = context.getResources().openRawResource(resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return body.toString();
	}

	/**
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType
	 *            The shader type.
	 * @param shaderSource
	 *            The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(final int shaderType, final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);
		MyRenderer.checkGlError("glCreateShader");

		if (shaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);
			MyRenderer.checkGlError("glShaderSource");

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);
			MyRenderer.checkGlError("glCompileShader");

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			MyRenderer.checkGlError("glGetShaderiv");

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				MyRenderer.checkGlError("glDeleteShader");
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating shader.");
		}

		return shaderHandle;
	}

	public static FloatBuffer initializeFloatBuffer(float[] data) {
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				data.length * mBytesPerFloat);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer returnValue = bb.asFloatBuffer();
		returnValue.put(data);
		returnValue.position(0);
		return returnValue;
	}

	public static ShortBuffer initializeShortBuffer(short[] data) {
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 2 bytes per short)
				data.length * mBytesPerShort);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer returnValue = bb.asShortBuffer();
		returnValue.put(data);
		returnValue.position(0);
		return returnValue;
	}
	
	/**
	 * Calculates the transform from screen coordinate system to world coordinate system coordinates
	 * for a specific point, given a camera position.
	 * 
	 * @param touch
	 *            Vec2 point of screen touch, the actual position on physical screen (ej: 160, 240)
	 * @param cam
	 *            camera object with x,y,z of the camera and screenWidth and screenHeight of the
	 *            device.
	 * @return position in WCS.
	 */
	public static float[] GetWorldCoords(float f, float g, int screenW, int screenH,
			float[] projectionViewMatrix) {
		// Initialize auxiliary variables.
		float[] worldPos = new float[2];

		// Auxiliary matrix and vectors
		// to deal with ogl.
		float[] invertedMatrix, normalizedInPoint, outPoint;
		invertedMatrix = new float[16];
		normalizedInPoint = new float[4];
		outPoint = new float[4];

		// Invert y coordinate, as android uses
		// top-left, and ogl bottom-left.
		int oglTouchY = (int) (screenH - g);

		/*
		 * Transform the screen point to clip space in ogl (-1,1)
		 */
		normalizedInPoint[0] = (float) (f * 2.0f / screenW - 1.0);
		normalizedInPoint[1] = (float) (oglTouchY * 2.0f / screenH - 1.0);
		normalizedInPoint[2] = -1.0f;
		normalizedInPoint[3] = 1.0f;

		/*
		 * Obtain the transform matrix and then the inverse.
		 */

		Matrix.invertM(invertedMatrix, 0, projectionViewMatrix, 0);

		/*
		 * Apply the inverse to the point in clip space
		 */
		Matrix.multiplyMV(outPoint, 0, invertedMatrix, 0, normalizedInPoint, 0);

		worldPos[0] = outPoint[0];
		worldPos[1] = outPoint[1];

		if (outPoint[3] == 0.0) {
			// Avoid /0 error.
			Log.e("World coords", "ERROR!");
			return worldPos;
		}

		// Divide by the 3rd component to find
		// out the real position.

		worldPos[0] /= outPoint[3];
		worldPos[0] /= outPoint[3];

		return worldPos;
	}
}
