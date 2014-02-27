package in.zakshay.colorsandroid;

import android.app.Application;
import android.util.Log;
import in.zakshay.projectmirror.*;

public class ColorsApp extends Application {
	
	private static ColorsApp instance;	
	private static MirrorSyncService mirrorService;
	private static ViewModel viewModel;
	
	@Override
	public void onCreate() {
		Log.e("COlorsMe", "ColorsAppOncreate");
		instance = this;
		
		mirrorService = new MirrorSyncService();
		viewModel = new ViewModel();
		super.onCreate();
	}
	
	public static ColorsApp getInstance() {
		return instance;
	}
	
	public MirrorSyncService getMirrorSyncService() {
		return mirrorService;
	}
	
	public ViewModel getViewModel() {		
		return viewModel;
	}

}
