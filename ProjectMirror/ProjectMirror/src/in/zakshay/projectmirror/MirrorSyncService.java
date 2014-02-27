package in.zakshay.projectmirror;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import android.R.integer;
import android.content.Context;
import android.util.Log;

public class MirrorSyncService {
	
	private DatabaseHelperBase dbHelper = null;
	private Context context;
	private Calendar calendar;
	
	private final Date beginingOfTheTime;
	private final Date riseOfHumanity;
	private final Date fallOfHumanity;
	
	//Constructor
	public MirrorSyncService(){
		Log.e("ColorsMe", "MirrorServiceOnCreate");
		calendar = Calendar.getInstance();
		calendar.set(1992, Calendar.DECEMBER, 31, 11, 40, 6);
		beginingOfTheTime = calendar.getTime();
		
		calendar.set(1993, Calendar.JANUARY, 1);
		riseOfHumanity = calendar.getTime();
		
		calendar.set(3993, Calendar.DECEMBER, 30);
		fallOfHumanity = calendar.getTime();
	}
	
	public void configureSQLite(Context passedContext, String dbName, int dbVersion, Class...tables ){
		Log.e("ColorsMe", "Configuring");
		DatabaseHelperBase.setDbName(dbName);
		DatabaseHelperBase.setDbVersion(dbVersion);
		DatabaseHelperBase.setTables(tables);
		context = passedContext;
		getDbHelper(); //Not really necessary, but let's be safe.
	}
	
	private DatabaseHelperBase getDbHelper(){
		if(dbHelper == null){
			dbHelper = OpenHelperManager.getHelper(context, DatabaseHelperBase.class);
		}
		return dbHelper;
	}
	
	//CRUD Operations
	
	public <TEntity extends SyncableBase> List<TEntity> loadItems(Class clazz) {
		
		
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(clazz);
			return dao.queryForAll();
		} catch (SQLException e){			
			e.printStackTrace();
			return new ArrayList<TEntity>();
		}		
	}
	
	public <TEntity extends SyncableBase> TEntity loadItemById(TEntity dummyEntity, int id) {
		TEntity item = null;
		
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(dummyEntity.getClass());
			TEntity entity = dao.queryForId(id);
			Log.e("ColorsMe", "Is the list done?");
		} catch (SQLException e){
			e.printStackTrace();
		}
		return item;
		
	}
	
	public <TEntity extends SyncableBase> void updateItem(TEntity itemToUpdate){
		itemToUpdate.setLastModified(new Date());
		
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(itemToUpdate.getClass());
			dao.update(itemToUpdate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public <TEntity extends SyncableBase> void addItem(TEntity itemToAdd ){
		Log.e("ColorsMe", "AddCalled");
		itemToAdd.setLastSynchronized(riseOfHumanity);
		itemToAdd.setLastModified(new Date());
		
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(itemToAdd.getClass());
			dao.create(itemToAdd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public <TEntity extends SyncableBase> void deleteItem(TEntity itemToDelete) {
		itemToDelete.setDeleted(true);
		itemToDelete.setLastModified(new Date());
		
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(itemToDelete.getClass());
			dao.update(itemToDelete);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
