package com.chan.hibeaver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chan.demo.Demo;
import com.shanbay.beaver.ShanbayBeaver;
import com.shanbay.beaver.annotation.Ignore;
import com.shanbay.beaver.config.BeaverConfig;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		findViewById(R.id.button).setOnClickListener(this);
//		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//
//				int x = 0;
//				int y = 0;
//
//				Window window = getWindow();
//				View dest = window.findViewById(Window.ID_ANDROID_CONTENT);
//				StringBuilder stringBuilder = new StringBuilder();
//
//
//				try {
//					Class<?> clazz = Class.forName("com.android.internal.R$dimen");
//					Object object = clazz.newInstance();
//					int height = Integer.parseInt(clazz.getField("status_bar_height")
//							.get(object).toString());
//					int statusBarHeight = getResources().getDimensionPixelSize(height);
//					stringBuilder.append("status bar: ");
//					stringBuilder.append(statusBarHeight);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//
//				ViewParent viewParent = v.getParent();
//				while (viewParent != null && viewParent != dest) {
//
//					x += v.getLeft();
//					y += v.getTop();
//
//					v = (View) viewParent;
//					viewParent = v.getParent();
//				}
//
//				stringBuilder.append("x: ");
//				stringBuilder.append(x);
//				stringBuilder.append("y: ");
//				stringBuilder.append(y);
//
//
//				DisplayMetrics dm = new DisplayMetrics();
//				getWindowManager().getDefaultDisplay().getMetrics(dm);
//				stringBuilder.append("h:");
//				stringBuilder.append(dm.heightPixels);
//				stringBuilder.append("w:");
//				stringBuilder.append(dm.widthPixels);
//
//				DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//				stringBuilder.append("h:");
//				stringBuilder.append(metrics.heightPixels);
//				stringBuilder.append("w:");
//				stringBuilder.append(metrics.widthPixels);
//
//
//				textView.setText(stringBuilder.toString());
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.text)
	void onClicked() {
		new AlertDialog.Builder(this)
				.setMessage("HAHA")
				.setNegativeButton("nagative", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog x = (AlertDialog) dialog;
						Button button = x.getButton(which);
						Log.d("chan_debug", button.getContext().toString());
					}
				})
				.setPositiveButton("666", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.show();
	}
}
