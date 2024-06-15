package com.mygdx.game;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.db.SugarDb;

public class AndroidLauncher extends AndroidApplication {
	private MyGdxGame game;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//передаёт размеры экрана для сета камеры (cam.setToOrtho), центровки элементов и т.д.
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		config.useImmersiveMode = true; //fullScreen
		game = new MyGdxGame(new SugarDb());
		initialize(game, config);

		MyGdxGame.setWidth(displaymetrics.widthPixels);
		MyGdxGame.setHeight(displaymetrics.heightPixels);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyGdxGame.active = true;
		//2 вариант запуска на фулл скрин, = 1 варианту
		/*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	@Override
	protected void onStop() {
		super.onStop();
		game.dispose();
		MyGdxGame.active = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyGdxGame.active = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyGdxGame.active = false;
	}
}
