package pavlik.john.blockdrop.opengl;

import pavlik.john.blockdrop.objects.TBlock;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private static final int	worldWidth		= 10;
	private static final int	worldHeight		= 22;

	private static final String	TAG				= "MyGLSurfaceView";
	private float				mPreviousX, mPreviousY;

	private MyRenderer			mRenderer;
	TBlock						block;
	private int					lastRotation	= 0;

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mRenderer = new MyRenderer(context);

		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data.
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		block = new TBlock(context);
		mRenderer.addBlock(block);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:

				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				Log.i(TAG, dx + " " + dy + " " + mPreviousX + " " + mPreviousY);
				// reverse direction of rotation above the mid-line
				if (y < getHeight() / 2) {
					dx = dx * -1;
				}

				// reverse direction of rotation to left of the mid-line
				if (x > getWidth() / 2) {
					dy = dy * -1;
				}

				block.setRotation( lastRotation = (lastRotation + 10) % 360);
				// + ((dx + dy) * TOUCH_SCALE_FACTOR)); // = 180.0f / 320
				requestRender();
				Log.i(TAG, "Rotation set to " + lastRotation);
		}

		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
}
