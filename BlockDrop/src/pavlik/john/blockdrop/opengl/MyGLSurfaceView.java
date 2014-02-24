package pavlik.john.blockdrop.opengl;

import pavlik.john.blockdrop.Util;
import pavlik.john.blockdrop.objects.BlockObject;
import pavlik.john.blockdrop.objects.World;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private static final String	TAG	= "MyGLSurfaceView";

	private MyRenderer			mRenderer;
	
	World mWorld;

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mWorld = new World(context);
		mRenderer = new MyRenderer(context, mWorld);

		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data.
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	BlockObject	blockTest;

	public void addBlock(final BlockObject block) {
		blockTest = block;
		queueEvent(new Runnable() {

			@Override
			public void run() {
				mRenderer.addBlock(block);
			}
		});

	}

	@Override
	public boolean onTouchEvent(final MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				float[] worldCoords = Util.GetWorldCoords(e.getX(), e.getY(), getWidth(), getHeight(), mRenderer.getViewProjectionMatrix());
				float xWorld = worldCoords[0];
				Log.i(TAG, "Touched world at " + worldCoords[0] + " " + worldCoords[1]);
				final int xChange = xWorld > 0f ? 1 : -1;
				queueEvent(new Runnable() {
					@Override
					public void run() {
						blockTest.setXBlock(blockTest.getXBlock() + xChange);
					}
				});
		}

		return true;
	}
}
