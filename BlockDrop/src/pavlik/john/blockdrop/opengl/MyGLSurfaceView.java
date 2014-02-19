package pavlik.john.blockdrop.opengl;

import pavlik.john.blockdrop.objects.BlockObject;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private static final String	TAG				= "MyGLSurfaceView";

	private MyRenderer			mRenderer;

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mRenderer = new MyRenderer(context);

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
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX() / getWidth();
		final float xCoordinate = x - 0.5f;
		float y = e.getY() / getHeight();
		final float yCoordinate = (-y + 0.5f)*2;
				
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:

				Log.i(TAG, x + " " + y);
				
				queueEvent(new Runnable() {

					@Override
					public void run() {
						blockTest.setXCoordinate(xCoordinate);
						blockTest.setYCoordinate(yCoordinate);
					}
				});
		}

		return true;
	}
}
