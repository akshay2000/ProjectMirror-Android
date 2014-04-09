package in.zakshay.projectmirror;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public abstract class SyncableBase {
	@DatabaseField(generatedId = true)
	@Expose(serialize = false)
	private int localId;
	
	@DatabaseField
	private boolean isDeleted;
	
	@DatabaseField
	private Date lastModified;

	public int getLocalId() {
		return localId;
	}

	public void setLocalId(int localId) {
		this.localId = localId;
	}

	public abstract String getRemoteId();

	public abstract void setRemoteId(String remoteId);

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public abstract Date getLastSynchronized();

	public abstract void setLastSynchronized(Date lastSynchronized);
}
