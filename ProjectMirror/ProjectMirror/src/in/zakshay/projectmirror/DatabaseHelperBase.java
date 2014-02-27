package in.zakshay.projectmirror;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/* Note: This is DataContext in .NET world!
 * And DAO objects represent the LINQ Tables */
public class DatabaseHelperBase extends OrmLiteSqliteOpenHelper {

	private static String dbName = "main.db";
	private static int dbVersion = 1;
	private static Class[] tables;

	public static String getDbName() {
		return dbName;
	}

	public static void setDbName(String dbName) {
		DatabaseHelperBase.dbName = dbName;
	}

	public static int getDbVersion() {
		return dbVersion;
	}

	public static void setDbVersion(int dbVersion) {
		DatabaseHelperBase.dbVersion = dbVersion;
	}

	public static Class[] getTables() {
		return tables;
	}

	public static void setTables(Class[] tables) {
		DatabaseHelperBase.tables = tables;
	}

	private Dao<SyncTimeStamp, String> syncTimeStampDao;
	

	public DatabaseHelperBase(Context context) {
		super(context, dbName, null, dbVersion);
	}
	
//	public DatabaseHelperBase(Context context, String dbName, int dbVersion){
//		super(context, dbName, null, dbVersion);
//	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		Log.e("ColorsMe", "OncreateWasCalled");
		try{
			TableUtils.createTable(connectionSource, SyncTimeStamp.class);
			for(Class table: tables){
				TableUtils.createTable(connectionSource, table);
			}
		}
		catch (SQLException e) {
			Log.e(DatabaseHelperBase.class.getName(), "Unable to create datbases", e);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		Log.e("ColorsMe", "OnUpdateWasCalled");
		// TODO Auto-generated method stub
		
	}

}
