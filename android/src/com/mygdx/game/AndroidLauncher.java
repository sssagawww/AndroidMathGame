package com.mygdx.game;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//передаёт размеры экрана для сета камеры (cam.setToOrtho), центровки элементов и т.д.
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		config.useImmersiveMode = true; //set fullScreen
		initialize(new MyGdxGame(), config);

		MyGdxGame.setWidth(displaymetrics.widthPixels);
		MyGdxGame.setHeight(displaymetrics.heightPixels);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//2 вариант запуска на фулл скрин, = 1 варианту
		/*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
}
