package in.zakshay.projectmirror;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable
public class SyncTimeStamp {
	
	//Constructor
	public SyncTimeStamp()
	{}
	
	public SyncTimeStamp(String entityName, Date lastSynced){
		this.entityName = entityName;
		this.lastSynced = lastSynced;
	}
	
	@DatabaseField(id = true)
	private String entityName;
	
	@DatabaseField
	private Date lastSynced;

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Date getLastSynced() {
		return lastSynced;
	}

	public void setLastSynced(Date lastSynced) {
		this.lastSynced = lastSynced;
	}
}
