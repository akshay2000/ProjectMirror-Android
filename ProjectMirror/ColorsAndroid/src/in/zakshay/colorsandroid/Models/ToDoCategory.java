/**
 * 
 */
package in.zakshay.colorsandroid.Models;

import java.util.Date;

import in.zakshay.projectmirror.SyncableBase;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Akshay
 *
 */
@DatabaseTable
public class ToDoCategory extends SyncableBase {
	
	//Constructor
	public ToDoCategory()
	{}
		
	public ToDoCategory(String name, String color){
		categoryName = name;
		categoryColor = color;
	}
	
	@DatabaseField
	private String categoryColor;
	
	@DatabaseField
	private String categoryName;
	
	@DatabaseField
	@SerializedName("id")
	private String remoteId;
	
	@DatabaseField
	private Date lastSynchronized;

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

	@Override
	public String getRemoteId() {
		return remoteId;
	}

	@Override
	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}
	
	@Override
	public Date getLastSynchronized() {
		return lastSynchronized;
	}

	@Override
	public void setLastSynchronized(Date lastSynchronized) {
		this.lastSynchronized = lastSynchronized;
	}
}