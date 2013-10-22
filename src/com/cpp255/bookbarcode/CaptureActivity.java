package com.cpp255.bookbarcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.cpp255.bookbarcode.Intents.ViewBookInfo;
import com.cpp255.bookbarcode.book.BookInfo;
import com.cpp255.bookbarcode.book.Utilities;
import com.cpp255.bookbarcode.camera.CameraManager;
import com.cpp255.bookbarcode.result.ResultHandler;
import com.cpp255.bookbarcode.result.ResultHandlerFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.ClipboardManager;
import android.text.StaticLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 重新移植原有的扫描
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = CaptureActivity.class.getSimpleName();

	private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
	private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;
	private boolean hasSurface;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result saveResultToShow;
	private ViewfinderView viewfinderView;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private IntentSource source;
	private BeepManager beepManager;
	private Context mContext;
	private ProgressBar progressBar;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_capture);
		progressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
		hasSurface = false;
		mContext = this;
		Utilities.createFold();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;
		resetStatusView();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();

		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		Intent intent = getIntent();
		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
		if (intent != null) {

			String action = intent.getAction();
			String dataString = intent.getDataString();
			if (Intents.Scan.ACTION.equals(action)) {
				// Scan the formats the intent requested, and return the result
				// to the calling activity.
				source = IntentSource.NATIVE_APP_INTENT;
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
				decodeHints = DecodeHintManager.parseDecodeHints(intent);

				if (intent.hasExtra(Intents.Scan.WIDTH)
						&& intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}

				String customPromptMessage = intent
						.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				if (customPromptMessage != null) {
				}

			}

			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

		}
	}

	@Override
	protected void onPause() {
		if (handler != null) {
		   handler.quitSynchronously();
			handler = null;
		}

		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}

		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		if (handler == null) {
			saveResultToShow = result;
		} else {
			if (result != null) {
				saveResultToShow = result;
			}

			if (saveResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, saveResultToShow);
				handler.sendMessage(message);
			}
			saveResultToShow = null;
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("no SurfaceHolder provided");
		}

		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}

		try {
			cameraManager.openDriver(surfaceHolder);
			// creating the handlers starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_book_info, menu);
		return true;
	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(
				this, rawResult);

		switch (source) {
		case NONE:
			if(networkIsAvailable()) {
				handleDecodeInternally(rawResult, resultHandler, barcode);
			} else {
				restartPreviewAfterDelay(500L);
			}
			break;
		}
	}

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult,
			ResultHandler resultHandler, Bitmap barcode) {
		viewfinderView.setVisibility(View.GONE);
		CharSequence displayContents = resultHandler.getDisplayContents();
		progressBar.setVisibility(View.VISIBLE);
		new DownloadBookInfoTask().execute(displayContents.toString());
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	// Implementation of AsyncTask used to download bookInfo from douban.com.
	private class DownloadBookInfoTask extends
			AsyncTask<String, Integer, BookInfo> {
		private int progressCount = 0;

		@Override
		protected BookInfo doInBackground(String... isbn) {
			try {
				DouBanBookInfoXmlParser bookInfo = new DouBanBookInfoXmlParser();
				return bookInfo.fetchBookInfoByXML(isbn[0]);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(BookInfo result) {
			progressBar.setVisibility(View.INVISIBLE);
			if (result == null) {
				new AlertDialog.Builder(mContext)
						.setMessage(R.string.msg_rescan_bookbar_info)
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										restartPreviewAfterDelay(0L);
									}
								}).show();
				return;
			}

			Intent intent = new Intent(CaptureActivity.this,
					BookDetailActivity.class);
			intent.putExtra(BookInfo.class.getName(), result);
			intent.setAction(ViewBookInfo.SCAN_BOOKINFO_INTENT);
			startActivity(intent);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressCount += values[0];
			progressBar.setProgress(progressCount);
			super.onProgressUpdate(values);
		}
	}

	/**
	 * 由于书本信息需要联网获取，因此要先检查网络状态
	 * @return
	 */
	private boolean networkIsAvailable() {
		boolean isConnected = false;
		
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if((info != null) && info.isConnected()) {
			isConnected = true;
		} else {
			Toast toast = Toast.makeText(this, R.string.msg_no_network, 300);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		
		return isConnected;
	}
}
