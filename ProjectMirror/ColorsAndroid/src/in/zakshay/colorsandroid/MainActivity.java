package in.zakshay.colorsandroid;

import in.zakshay.colorsandroid.Models.ToDoCategory;
import in.zakshay.projectmirror.MirrorSyncService;
import in.zakshay.projectmirror.SyncCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ToDoCategoryAdapter mAdapter;
	private static MirrorSyncService mirrorService;
	
	public static MirrorSyncService getmirrorService(){
		return mirrorService;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mirrorService = new MirrorSyncService(this);
		configureMirrorService();		
		login();
		
		mAdapter = new ToDoCategoryAdapter(this, R.layout.row_to_do);
		ListView categoriesListView = (ListView) findViewById(R.id.listViewToDo);
		categoriesListView.setAdapter(mAdapter);

		categoriesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GlobalVariables.currenToDoCategory = mAdapter.getItem(position);
				Intent intent = new Intent(getApplicationContext(),
						DetailsActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		loadAdapter();
	}
	
	public void syncit(View view) {
		try {
			reloadCategories();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void onAddCategoryClick(View view){
		GlobalVariables.currenToDoCategory = new ToDoCategory();
		Intent intent = new Intent(getApplicationContext(),
				DetailsActivity.class);
		startActivity(intent);
	}
	
	

	private void reloadCategories() throws SQLException {
			mirrorService.synchronize(ToDoCategory.class,
					new SyncCallback() {

						@Override
						public void onSyncComplete(Exception exception) {
							if (exception == null) {
								loadAdapter();
							}
						}
					});		
	}

	private void loadAdapter() {
		List<ToDoCategory> categoryList = mirrorService.loadItems(ToDoCategory.class);
		mAdapter.clear();
		for (ToDoCategory toDoCategory : categoryList) {
			mAdapter.add(toDoCategory);
		}
	}
	
	private void configureMirrorService() {
		mirrorService.configureSQLite("mainDB.sqlite", 1, ToDoCategory.class);

		/*
		 * Read Azure API details from file.Don't publish the keys to GitHub
		 * again!
		 */
		AssetManager assetManager = this.getAssets();
		String keyJson = "";
		String endPoint = "";
		String accessKey = "";
		try {
			InputStream iStream = assetManager.open("AzureKeys.json");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int i;
			i = iStream.read();
			while (i != -1) {
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
	
	private void login() {
		// Login using the Google provider.
	    mirrorService.authenticate(MobileServiceAuthenticationProvider.Google,
	            new UserAuthenticationCallback() {

	                @Override
	                public void onCompleted(MobileServiceUser user,
	                        Exception exception, ServiceFilterResponse response) {

	                    if (exception == null) {
	                    	Toast toast = Toast.makeText(getApplicationContext(), String.format(
	                                        "You are now logged in - %1$2s",
	                                        user.getUserId()), Toast.LENGTH_SHORT);
	                    	toast.show();
	                    } else {
	                    	Toast toast = Toast.makeText(getApplicationContext(), "You must log in!", Toast.LENGTH_SHORT);
	                    	toast.show();
	                    }
	                }
	            });
	}

}
