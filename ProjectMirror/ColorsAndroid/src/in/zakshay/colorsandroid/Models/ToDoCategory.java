/**
 * 
 */
package in.zakshay.colorsandroid.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import in.zakshay.projectmirror.*;

/**
 * @author Akshay
 *
 */
@DatabaseTable
public class ToDoCategory extends SyncableBase {
	
	@DatabaseField
	private String categoryColor;
	
	@DatabaseField
	private String categoryName;
	
	public String getCategoryColor() {
		return categoryColor;
	}
	
	public void setCategoryColor(String color) {
		categoryColor = color;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryName(String name) {
		categoryName = name;
	}
	
	//Constructor
	public ToDoCategory()
	{}
	
	public ToDoCategory(String name, String color){
		categoryName = name;
		categoryColor = color;
	}
}
