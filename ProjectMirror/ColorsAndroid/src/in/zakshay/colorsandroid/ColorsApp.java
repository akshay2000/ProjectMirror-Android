package in.zakshay.colorsandroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;
import in.zakshay.colorsandroid.Models.ToDoCategory;
import in.zakshay.projectmirror.*;

public class ColorsApp extends Application {
	
	private static ColorsApp instance;	
	private static MirrorSyncService mirrorService;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("COlorsMe", "ColorsAppOncreate");
		instance = this;
		
		mirrorService = new MirrorSyncService(this);
		configureMobileService();
	}
	
	public static ColorsApp getInstance() {
		return instance;
	}
	
	public MirrorSyncService getMirrorSyncService() {
		return mirrorService;
	}
	
	private void configureMobileService(){
		mirrorService.configureSQLite("mainDB.sqlite", 1, ToDoCategory.class);
		
		/*Read Azure API details from file.
		 *Don't publish the keys to GitHub again!*/
		AssetManager assetManager = this.getAssets();
		String keyJson = "";
		String endPoint = "";
		String accessKey = "";
		try {
			InputStream iStream = assetManager.open("AzureKeys.json");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			int i;
			i = iStream.read();
			while (i != -1){
				baos.write(i);
			    i = iStream.read();
			}
			iStream.close();
			keyJson = baos.toString();
			
			JSONObject keyJsonObject = new JSONObject(keyJson);
			endPoint = keyJsonObject.getString("endpoint");
			accessKey = keyJsonObject.getString("accesskey");
			
		} catch (IOException e) {
			Log.e("ColorsMe", "Add AzureKeys.json in assets folder");
			e.printStackTrace();
			
		} catch (JSONException e) {
			Log.e("ColorsMe", "Invalid AzureKeys.json");
			e.printStackTrace();
		}
		
		mirrorService.configureMobileService(endPoint, accessKey);
	}

}
