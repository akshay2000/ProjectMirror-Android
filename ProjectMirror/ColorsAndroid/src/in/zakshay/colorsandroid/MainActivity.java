package in.zakshay.colorsandroid;

import in.zakshay.colorsandroid.Models.ToDoCategory;
import in.zakshay.projectmirror.SyncCallback;

import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private ColorsApp appContext = ColorsApp.getInstance();
	private ToDoCategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new ToDoCategoryAdapter(this, R.layout.row_to_do);
        ListView categoriesListView = (ListView)findViewById(R.id.listViewToDo);
        categoriesListView.setAdapter(mAdapter);
        loadAdapter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void syncit(View view){
    	try {
			reloadCategories();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    private void reloadCategories() throws SQLException {
    	appContext.getMirrorSyncService().synchronize(ToDoCategory.class, new SyncCallback() {
			
			@Override
			public void onSyncComplete(Exception exception) {
				if(exception == null) {
					loadAdapter();
				}				
			}
		});
    }
    
    private void loadAdapter(){
    	List<ToDoCategory> categoryList = appContext.getMirrorSyncService().loadItems(ToDoCategory.class);
		mAdapter.clear();
		for (ToDoCategory toDoCategory : categoryList) {
			mAdapter.add(toDoCategory);
		}
    }
    
}
