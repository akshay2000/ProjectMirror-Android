package in.zakshay.projectmirror;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.microsoft.windowsazure.mobileservices.*;

import android.R.integer;
import android.R.string;
import android.accounts.Account;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.util.Log;

@SuppressWarnings("unused")
public class MirrorSyncService {
	private MobileServiceClient mobileServiceClient;

	private DatabaseHelperBase dbHelper = null;
	private Context context;
	private Calendar calendar;

	private final Date beginingOfTheTime;
	private final Date riseOfHumanity;
	private final Date fallOfHumanity;

	// Constructor
	public MirrorSyncService(Context passedContext) {
		Log.e("ColorsMe", "MirrorServiceOnCreate");
		calendar = Calendar.getInstance();
		calendar.set(1992, Calendar.DECEMBER, 31, 11, 40, 6);
		beginingOfTheTime = calendar.getTime();

		calendar.set(1993, Calendar.JANUARY, 1);
		riseOfHumanity = calendar.getTime();

		calendar.set(3993, Calendar.DECEMBER, 30);
		fallOfHumanity = calendar.getTime();

		context = passedContext;
	}

	@SuppressWarnings("rawtypes")
	public void configureSQLite(String dbName, int dbVersion, Class... tables) {
		Log.e("ColorsMe", "Configuring");
		DatabaseHelperBase.setDbName(dbName);
		DatabaseHelperBase.setDbVersion(dbVersion);
		DatabaseHelperBase.setTables(tables);
		getDbHelper(); // Not really necessary, but let's be safe.
	}

	public void configureMobileService(String endPoint, String accessKey) {
		try {
			mobileServiceClient = new MobileServiceClient(endPoint, accessKey,
					context);
		} catch (MalformedURLException e) {
			Log.e("ColorsMe", "Invalid endpoint URL");
			e.printStackTrace();
		}
	}

	// Authentication region
	public void authenticate(MobileServiceAuthenticationProvider provider,
			UserAuthenticationCallback callback) {
		mobileServiceClient.login(provider, callback);
	}

	public void authenticate(MobileServiceAuthenticationProvider provider,
			JsonObject oAuthToken, UserAuthenticationCallback callback) {
		mobileServiceClient.login(provider, oAuthToken, callback);
	}

	public void authenticate(Activity activity, Account account, String scopes,
			UserAuthenticationCallback callback) {
		mobileServiceClient.loginWithGoogleAccount(activity, account, scopes,
				callback);
	}
	
	//Authentication region complete

	private <TEntity extends SyncableBase> Date getLastSyncedTime(
			Class<TEntity> clazz) {
		DatabaseHelperBase dbHelperBase = getDbHelper();
		String entityName = clazz.getSimpleName();
		Dao<SyncTimeStamp, String> dao;
		try {
			dao = dbHelper.getDao(SyncTimeStamp.class);
			SyncTimeStamp tableRow = dao.queryForId(entityName);
			return tableRow != null ? tableRow.getLastSynced() : null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private <TEntity extends SyncableBase> void setLastSyncedTime(
			Class<TEntity> clazz, Date recentUpdated) {
		DatabaseHelperBase dbHelperBase = getDbHelper();
		String entityName = clazz.getSimpleName();
		Dao<SyncTimeStamp, String> dao;
		try {
			dao = dbHelper.getDao(SyncTimeStamp.class);
			SyncTimeStamp tableRow = dao.queryForId(entityName);
			if (tableRow != null) {
				tableRow.setLastSynced(recentUpdated);
				dao.update(tableRow);
			} else {
				SyncTimeStamp newStamp = new SyncTimeStamp(entityName,
						recentUpdated);
				dao.create(newStamp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public <TEntity extends SyncableBase> void synchronize(
			final Class<TEntity> clazz, final SyncCallback callback)
			throws SQLException {
		DatabaseHelperBase dbHelper = getDbHelper();
		final Dao<TEntity, Integer> localTable = dbHelper.getDao(clazz);

		MobileServiceTable<TEntity> remoteTable = (MobileServiceTable<TEntity>) mobileServiceClient
				.getTable(clazz);

		final Date lastSynced = this.<TEntity> getLastSyncedTime(clazz);
		final Date currentTimeStamp = lastSynced != null ? lastSynced
				: beginingOfTheTime;
		final Date[] newTimeStamp = { currentTimeStamp };

		QueryBuilder<TEntity, Integer> qb = localTable.queryBuilder();
		qb.where().gt("lastModified", currentTimeStamp);
		PreparedQuery<TEntity> preparedQuery = qb.prepare();

		// List holds newly added or newly modified local items
		final List<TEntity> newLocalItems = localTable.query(preparedQuery);

		// This list will hold newly added or newly modified remote items
		// NOTE: It will remain empty until callback is returned!
		final List<TEntity> newRemoteItems = new ArrayList<TEntity>();
		remoteTable.where().field("lastSynchronized").gt(currentTimeStamp)
				.execute(new TableQueryCallback<TEntity>() {
					public void onCompleted(List<TEntity> result, int count,
							Exception exception, ServiceFilterResponse response) {
						if (exception == null) {
							newRemoteItems.clear();
							for (TEntity item : result) {
								newRemoteItems.add(item);
							}
							resolveConflicts(newLocalItems, newRemoteItems); // Modify
																				// the
																				// lists
																				// directly

							try {
								newTimeStamp[0] = applyRemoteToLocal(
										newRemoteItems, clazz, newTimeStamp[0]);
								if (newLocalItems.isEmpty()) {// The list is
																// empty. No
																// need to
																// continue the
																// method. Do
																// maintenance
																// now.
									setLastSyncedTime(clazz, newTimeStamp[0]);
									callback.onSyncComplete(null);
								} else {
									applyLocalToRemote(newLocalItems, clazz,
											newTimeStamp[0], callback);
								}

							} catch (SQLException e) {
								callback.onSyncComplete(e);
							}
						} else {
							callback.onSyncComplete(exception);
						}
					}
				});
	}

	private <TEntity extends SyncableBase> void resolveConflicts(
			List<TEntity> newLocalItems, List<TEntity> newRemoteItems) {
		for (TEntity localItem : newLocalItems) {
			String localId = localItem.getRemoteId();

			for (TEntity remoteItem : newRemoteItems) {
				if (remoteItem.getRemoteId().equals(localId)) {
					boolean removed = localItem.getLastModified().after(
							remoteItem.getLastModified()) ? newRemoteItems
							.remove(remoteItem) : newLocalItems
							.remove(localItem);
				}
			}
		}
	}

	private <TEntity extends SyncableBase> Date applyRemoteToLocal(
			List<TEntity> newRemoteItems, Class<TEntity> clazz,
			Date currentTimeStamp) throws SQLException {
		DatabaseHelperBase dbHelper = getDbHelper();
		final Dao<TEntity, Integer> localTable = dbHelper.getDao(clazz);

		Date newTimeStamp = currentTimeStamp;

		for (TEntity remoteItem : newRemoteItems) {
			TEntity localItem = getItemByRemoteId(remoteItem);
			if (localItem == null) {
				localTable.create(remoteItem);
			} else {
				remoteItem.setLocalId(localItem.getLocalId());
				int i = localTable.update(remoteItem);
			}

			newTimeStamp = newTimeStamp.after(remoteItem.getLastSynchronized()) ? newTimeStamp
					: remoteItem.getLastSynchronized();
		}
		return newTimeStamp;
	}

	private <TEntity extends SyncableBase> void applyLocalToRemote(
			List<TEntity> newLocalItems, final Class<TEntity> clazz,
			Date currentTimeStamp, final SyncCallback callback)
			throws SQLException {
		DatabaseHelperBase dbHelper = getDbHelper();
		final Dao<TEntity, Integer> localTable = dbHelper.getDao(clazz);

		MobileServiceTable<TEntity> remoteTable = (MobileServiceTable<TEntity>) mobileServiceClient
				.getTable(clazz);

		final Date[] newTimeStamp = { currentTimeStamp };

		final int[] countdown = { newLocalItems.size() };

		for (TEntity localItem : newLocalItems) {
			if (localItem.getRemoteId() == null) {
				remoteTable.insert(localItem,
						new TableOperationCallback<TEntity>() {
							public void onCompleted(TEntity entity,
									Exception exception,
									ServiceFilterResponse response) {
								countdown[0]--;
								if (exception == null) {
									try {
										localTable.update(entity);
										newTimeStamp[0] = newTimeStamp[0]
												.after(entity
														.getLastSynchronized()) ? newTimeStamp[0]
												: entity.getLastSynchronized();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (countdown[0] == 0) {
									setLastSyncedTime(clazz, newTimeStamp[0]);
									callback.onSyncComplete(null);
								}
							}
						});
			} else {
				remoteTable.update(localItem,
						new TableOperationCallback<TEntity>() {
							public void onCompleted(TEntity entity,
									Exception exception,
									ServiceFilterResponse response) {
								countdown[0]--;
								if (exception == null) {
									try {
										localTable.update(entity);
										newTimeStamp[0] = newTimeStamp[0]
												.after(entity
														.getLastSynchronized()) ? newTimeStamp[0]
												: entity.getLastSynchronized();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (countdown[0] == 0) {
									setLastSyncedTime(clazz, newTimeStamp[0]);
									callback.onSyncComplete(null);
								}
							}
						});
			}
		}
	}

	// Syncronization stuff ends
	// ----------------------------------------------------------------------------------

	private DatabaseHelperBase getDbHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(context,
					DatabaseHelperBase.class);
		}
		return dbHelper;
	}

	@SuppressWarnings("unchecked")
	public <TEntity extends SyncableBase> void addRemoteItem(TEntity itemToAdd)
			throws InterruptedException {
		MobileServiceTable<TEntity> remoteTable = (MobileServiceTable<TEntity>) mobileServiceClient
				.getTable(itemToAdd.getClass());

		remoteTable.insert(itemToAdd, new TableOperationCallback<TEntity>() {
			public void onCompleted(TEntity entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					Log.e("SuccessMe", "Success");
					// Log.e("Bingosss", i.getRemoteId());
					// Insert succeeded
				} else {
					Log.e("SuccessMe", "Nah" + exception.getMessage());
					// Insert failed
				}
			}
		});
	}

	// CRUD
	// Operations--------------------------------------------------------------------

	public <TEntity extends SyncableBase> List<TEntity> loadItems(
			Class<TEntity> clazz) {
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(clazz);
			return dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<TEntity>();
		}
	}

	public <TEntity extends SyncableBase> void updateItem(TEntity itemToUpdate) {
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

	public <TEntity extends SyncableBase> void addItem(TEntity itemToAdd) {
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

	private <TEntity extends SyncableBase> TEntity getItemByRemoteId(
			TEntity remoteItem) {
		DatabaseHelperBase dbHelper = getDbHelper();
		Dao<TEntity, Integer> dao;
		try {
			dao = dbHelper.getDao(remoteItem.getClass());
			QueryBuilder<TEntity, Integer> qb = dao.queryBuilder();
			qb.where().eq("remoteId", remoteItem.getRemoteId());
			PreparedQuery<TEntity> preparedQuery = qb.prepare();
			List<TEntity> queryResult = dao.query(preparedQuery);
			return queryResult.isEmpty() ? null : queryResult.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
