package in.zakshay.projectmirror;

import java.util.Date;

public interface Syncable {
	public int getLocalId();
	public void setLocalId(int localId);
	
	public String getRemoteId();
	public void setRemoteId(String remoteId);

	public boolean isDeleted();
	public void setDeleted(boolean isDeleted);
	
	public Date getLastModified();
	public void setLastModified(Date date);
	
	public Date getLastSynchronized();
	public void setLastSynchronized(Date date);	
}
