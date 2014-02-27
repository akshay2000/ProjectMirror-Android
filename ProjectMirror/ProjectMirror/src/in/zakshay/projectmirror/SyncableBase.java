package in.zakshay.projectmirror;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class SyncableBase {
	
	@DatabaseField(generatedId = true)
	private int localId;
	
	@DatabaseField
	private int remoteId;

	@DatabaseField
	private boolean isDeleted;

	@DatabaseField
	private Date lastSynchronized;

	@DatabaseField
	private Date lastModified;
	
	public int getLocalId() {
		return localId;
	}
	public void setLocalId(int localId) {
		this.localId = localId;
	}
	public int getRemoteId() {
		return remoteId;
	}
	public void setRemoteId(int remoteId) {
		this.remoteId = remoteId;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Date getLastSynchronized() {
		return lastSynchronized;
	}
	public void setLastSynchronized(Date lastSynchronized) {
		this.lastSynchronized = lastSynchronized;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
}
