package in.zakshay.colorsandroid;

import java.util.List;

import android.util.Log;

import in.zakshay.colorsandroid.Models.ToDoCategory;
import in.zakshay.projectmirror.*;

@SuppressWarnings("unused")
public class ViewModel {
	
	private ColorsApp appContext = ColorsApp.getInstance();
	private MirrorSyncService mirrorService = appContext.getMirrorSyncService();
	
	public ViewModel(){
		mirrorService.configureSQLite(appContext, "mainDB.sqlite", 1, ToDoCategory.class);
		
		//Add
//		for(int i=0; i<5; i++){
//			mirrorService.addItem(new ToDoCategory("cat" + i, "Bingo"));
//		}
//		
//		ToDoCategory cat2 = new ToDoCategory("Updated", "Ting");
//		cat2.setLocalId(2);
//		mirrorService.updateItem(cat2);
//		
//		ToDoCategory cat3 = new ToDoCategory("Deleted", "bing");
//		cat3.setLocalId(3);
//		mirrorService.deleteItem(cat3);
		
		//Load
		List<ToDoCategory> catList = mirrorService.<ToDoCategory>loadItems(ToDoCategory.class);
		Log.e("ColorsMe", "Bleeeep");
//		ToDoCategory cat0 = mirrorService.<ToDoCategory>loadItemById(categoryToAdd, 2);
//		Log.e("ColorsMe", "Something happened");
		
		//Update
//		ToDoCategory cat1 = catList.get(0);
//		cat1.setCategoryName("Updated");
//		mirrorService.updateItem(cat1);
//		
//		//Delete
//		ToDoCategory cat2 = catList.get(1);
//		mirrorService.deleteItem(cat2);		
	}
	
}
