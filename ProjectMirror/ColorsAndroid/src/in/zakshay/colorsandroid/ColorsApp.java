package in.zakshay.colorsandroid;

import android.app.Application;
import android.util.Log;

public class ColorsApp extends Application {

	private static ColorsApp instance;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("COlorsMe", "ColorsAppOncreate");
		instance = this;

	}

	public static ColorsApp getInstance() {
		return instance;
	}
}
