package pavlik.john.blockdrop.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pavlik.john.blockdrop.objects.BlockObject;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class MyRenderer implements Renderer {

	List<BlockObject>			mObjects			= new ArrayList<BlockObject>();
	private static final String	TAG					= "MyRenderer";
	// mMVPMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[]		mMVPMatrix			= new float[16];
	private final float[]		mProjectionMatrix	= new float[16];
	private final float[]		mViewMatrix			= new float[16];
	private Context				mContext;

	public MyRenderer(Context context) {
		this.mContext = context;
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		for (BlockObject object : mObjects) {
			object.regenChild();
		}
	}

	public void onDrawFrame(GL10 unused) {
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		//Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

		for (BlockObject obj : mObjects) {
			obj.draw(mViewMatrix, mProjectionMatrix);
		}
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		MyRenderer.checkGlError("glViewport");
		float ratio = (float) width / height;

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10f);
	}

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call just after making it:
	 * 
	 * <pre>
	 * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
	 * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	public void addBlock(BlockObject block) {
		block.regenChild();
		mObjects.add(block);
	}

}
