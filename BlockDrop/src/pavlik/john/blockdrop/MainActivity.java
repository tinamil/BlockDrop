package pavlik.john.blockdrop;

import pavlik.john.blockdrop.objects.BlockObject;
import pavlik.john.blockdrop.objects.OBlock;
import pavlik.john.blockdrop.opengl.MyGLSurfaceView;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String	TAG			= getClass().getSimpleName();

	private long	startTime	= 0L;
	private Handler	timeHandler	= new Handler();

	MenuItem		pauseButton;
	MyGLSurfaceView	mSurfaceView;
	LinearLayout	mTopOverlay;
	Button			startButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSurfaceView = (MyGLSurfaceView) findViewById(R.id.surface_view);
		mTopOverlay = (LinearLayout) findViewById(R.id.top_layout);
		startButton = (Button) findViewById(R.id.start_button);
		final TextView overlayText = (TextView) findViewById(R.id.overlay_text);
		overlayText.setText(R.string.start_overlay);
		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startGame();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		pauseButton = menu.findItem(R.id.action_pause);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_pause:
				pauseGame();
				return true;
			case R.id.action_settings:
				showSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		super.onPause();
		mSurfaceView.onPause();
		pauseGame();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSurfaceView.onResume();
	}

	BlockObject	block;

	private void startGame() {
		// startTime = SystemClock.uptimeMillis();
		timeHandler.postDelayed(updateTimerThread, 0);
		block = new OBlock(getApplicationContext());
		mSurfaceView.addBlock(block);

		startButton.setVisibility(View.GONE);
		mTopOverlay.setVisibility(View.GONE);
		pauseButton.setVisible(true);
	}

	private void pauseGame() {
		timeHandler.removeCallbacks(updateTimerThread);
		mTopOverlay.setVisibility(View.VISIBLE);
		startButton.setVisibility(View.VISIBLE);
		pauseButton.setVisible(false);
	}

	private Runnable	updateTimerThread	= new Runnable() {
												public void run() {
													long timeInMilliseconds = SystemClock
															.uptimeMillis()
															- startTime;
													// int secs = (int) (timeInMilliseconds / 1000);
													// int mins = secs / 60;
													// secs = secs % 60;
													int milliseconds = (int) (timeInMilliseconds % 1000);
													Log.d(TAG, "Timer posted");
													// timerValue.setText("" + mins + ":"
													// + String.format("%02d", secs) + ":"
													// + String.format("%03d", milliseconds));
													timeHandler.postDelayed(this, 1000);
												}
											};

}
