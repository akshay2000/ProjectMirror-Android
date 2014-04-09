package in.zakshay.colorsandroid;

import in.zakshay.colorsandroid.Models.ToDoCategory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class DetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		String categoryName = GlobalVariables.currenToDoCategory.getCategoryName();
		String categoryColor = GlobalVariables.currenToDoCategory.getCategoryColor();

		EditText categoryNameET = (EditText) findViewById(R.id.categoryNameET);
		categoryNameET.setText(categoryName);

		EditText categoryColorET = (EditText) findViewById(R.id.categoryColorET);
		categoryColorET.setText(categoryColor);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveCategory(View view) {
		ToDoCategory category = GlobalVariables.currenToDoCategory;
		
		EditText categoryNameET = (EditText) findViewById(R.id.categoryNameET);
		EditText categoryColorET = (EditText) findViewById(R.id.categoryColorET);
		
		category.setCategoryName(categoryNameET.getText().toString());
		category.setCategoryColor(categoryColorET.getText().toString());
		
		if(category.getLocalId() < 1){
			ColorsApp.getInstance().getMirrorSyncService().addItem(category);
		}
		else {
			ColorsApp.getInstance().getMirrorSyncService().updateItem(category);
		}
		finish();
	}

}
