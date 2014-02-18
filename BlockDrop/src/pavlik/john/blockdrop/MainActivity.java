package pavlik.john.blockdrop;

import pavlik.john.blockdrop.opengl.MyGLSurfaceView;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private String	TAG			= getClass().getSimpleName();

	private boolean	mPaused		= false;
	private boolean	mFinished	= false;
	private Object	mPauseLock;


	private long getElapsedTime() {
		return 0;
	}

	private void checkPaused() {
		synchronized (mPauseLock) {
			while (mPaused) {
				try {
					mPauseLock.wait();
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MyGLSurfaceView glSurfaceView = (MyGLSurfaceView) findViewById(R.id.surface_view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

}
