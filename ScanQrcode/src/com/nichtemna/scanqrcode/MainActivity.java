package com.nichtemna.scanqrcode;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class MainActivity extends Activity implements OnClickListener {
	public static final String QRCODE = "qrcode";
	public static final int SELECT_PHOTO = 100;
	public static final int SCAN_CODE = 200;
	private Button btn_scan, btn_open;
	private TextView tv_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		setListeners();

	}

	private void initViews() {
		btn_scan = (Button) findViewById(R.id.ac_main_btn_scan);
		btn_open = (Button) findViewById(R.id.ac_main_btn_open);
		tv_code = (TextView) findViewById(R.id.ac_main_tv_code);
	}

	private void setListeners() {
		btn_scan.setOnClickListener(this);
		btn_open.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ac_main_btn_scan:
			/*
			 * Intent intent = new Intent(MainActivity.this,
			 * ScanLaunchActivity.class)
			 * .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
			 * Intent.FLAG_ACTIVITY_NO_ANIMATION); startActivity(intent);
			 */
			// Intent intent = new Intent(MainActivity.this,
			// CaptureActivity.class);
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, SCAN_CODE);

			break;

		case R.id.ac_main_btn_open:
			getCodeFromImage();
			break;

		default:
			break;
		}
	}

	private void getCodeFromImage() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SCAN_CODE:
				String capturedQrValue = intent.getStringExtra("SCAN_RESULT");
				tv_code.setText(capturedQrValue);
				break;
			case SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					Uri selectedImage = intent.getData();
					try {
						InputStream imageStream = getContentResolver()
								.openInputStream(selectedImage);
						Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
						if (bitmap == null) {
							// return null;
						}
						int width = bitmap.getWidth(), height = bitmap
								.getHeight();
						int[] pixels = new int[width * height];
						bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
						bitmap.recycle();
						bitmap = null;
						RGBLuminanceSource source = new RGBLuminanceSource(
								width, height, pixels);
						BinaryBitmap bBitmap = new BinaryBitmap(
								new HybridBinarizer(source));
						MultiFormatReader reader = new MultiFormatReader();
						try {
							Result result = reader.decode(bBitmap);
							tv_code.setText(result.getText());
							Log.d("TAG", "decoded " + result.getText());
							// return result;
						} catch (NotFoundException e) {
							Log.e("TAG", "decode exception", e);
							// return null;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}
}
