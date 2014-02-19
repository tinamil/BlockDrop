package pavlik.john.blockdrop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pavlik.john.blockdrop.opengl.MyRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class Util {
	private static final String TAG = "Util";

	public static int loadTexture(final Context context, final int resourceId) {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);
		MyRenderer.checkGlError("glGenTextures");

		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), resourceId, options);

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			MyRenderer.checkGlError("glBindTexture");

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			MyRenderer.checkGlError("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			MyRenderer.checkGlError("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			MyRenderer.checkGlError("glTexParameteri");
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
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

	public static String readTextFileFromRawResource(final Context context,
			final int resourceId) {
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

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
	public static int compileShader(final int shaderType,
			final String shaderSource) {
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
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS,
					compileStatus, 0);
			MyRenderer.checkGlError("glGetShaderiv");

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				Log.e(TAG,
						"Error compiling shader: "
								+ GLES20.glGetShaderInfoLog(shaderHandle));
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

}
