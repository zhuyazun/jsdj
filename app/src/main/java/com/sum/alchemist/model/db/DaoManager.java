package com.sum.alchemist.model.db;

import com.sum.alchemist.Config;
import com.sum.xlog.core.XLog;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * 数据库访问对象
 * Created by Qiu on 2016/3/31
 */
public class DaoManager {
    private static final String TAG = "DaoManager";

    private static DaoManager mInstance;

    /**
     * xUtils 框架里的数据操作对象
     */
    private DbManager mDbUtils;

    public static DaoManager getInstance() {
        if (mInstance == null)
            synchronized (DaoManager.class) {
                if (mInstance == null)
                    mInstance = new DaoManager();
            }
        return mInstance;
    }

    private DaoManager() {
        mDbUtils = x.getDb(new org.xutils.DbManager.DaoConfig()
                .setDbName(Config.DbConfig.DB_NAME)
                .setDbVersion(Config.DbConfig.DB_VERSION)
                .setDbUpgradeListener(new DbUpgradeListener()));
    }

    public static DbManager getDbManager() {
        return getInstance().mDbUtils;
    }

    public class DbUpgradeListener implements org.xutils.DbManager.DbUpgradeListener {


        @Override
        public void onUpgrade(org.xutils.DbManager db, int oldVersion, int newVersion) {
            try {
                if (oldVersion <= 1) {
                    XLog.i(TAG, "=== 数据库版本升级  14 , 数据库表有变动，删除原有表，建立新表 ===");
                    db.dropDb();
                }
            } catch (DbException e) {
                XLog.e(TAG, "===删除表失败", e);
                e.printStackTrace();
            }



        }
    }

}
