package com.sum.alchemist.model.db;

import android.text.TextUtils;

import com.sum.xlog.core.XLog;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;

import java.util.List;

public class BaseDaoImpl<T> implements BaseDao<T>{
	
	public static final String TAG = "BaseDaoImpl";
	private Class <T> entityClass;
	
	public BaseDaoImpl(Class<T> t){
		entityClass = t;
	}

	@Override
	public boolean insert(T t) {
		
		boolean result = false;
		if(t == null)
			return false;
		try {
			DaoManager.getDbManager().saveOrUpdate(t);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "insert", e);
		}
		
		return result;
		
	}

	@Override
	public boolean insert(List<T> ts) {
		
		boolean result = false;
		if(ts == null || ts.isEmpty())
			return false;
		try {
			DaoManager.getDbManager().saveOrUpdate(ts);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "insert all", e);
		}
		
		return result;
	}

	@Override
	public boolean replace(T t) {
		
		boolean result = false;
		if(t == null)
			return false;
		try {
			DaoManager.getDbManager().saveOrUpdate(t);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "replace", e);
		}
		
		return result;
	}

	@Override
	public boolean replace(List<T> ts) {
			
		boolean result = false;
		if(ts == null || ts.isEmpty())
			return false;
		try {
			DaoManager.getDbManager().saveOrUpdate(ts);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "replace all", e);
		}
		
		return result;
	}

	@Override
	public boolean update(T t) {
		
		boolean result = false;
		if(t == null)
			return false;
		try {
			DaoManager.getDbManager().update(t);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "update", e);
		}
		
		return result;
	}

	@Override
	public boolean deleteAll() {
		
		boolean result = false;
		
		try {
			DaoManager.getDbManager().delete(entityClass);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "delete all", e);
		}
		
		return result;
	}

	@Override
	public boolean deleteById(Object o) {
		boolean result = false;
		if(o == null)
			return false;
		try {
			DaoManager.getDbManager().deleteById(entityClass, o);
			result = true;
		} catch (Exception e) {
			XLog.e(TAG, "deleteById", e);
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T queryById(Object o) {
		
		T result = null;
		if(o == null)
			return null;
		try {
			result = DaoManager.getDbManager().findById(entityClass, o);
		} catch (Exception e) {
			XLog.e(TAG, "queryById", e);
		}
		
		return result;
	}

	@Override
	public List<T> queryList() {

		return queryList(null, false, 0, 0, null);
	}

	@Override
	public List<T> queryList(String sortBy, boolean desc) {

		return queryList(sortBy, desc, 0, 0, null);
	}

	@Override
	public List<T> queryList(String sortBy, boolean desc, int startIndex,
			int limit) {
		return queryList(sortBy, desc, startIndex, limit, null);
	}

	@Override
	public List<T> queryList(String sortBy, boolean desc, int startIndex, int limit, WhereBuilder whereBuilder) {
		List<T> result = null;

		try {
			Selector<T> selector = DaoManager.getDbManager().selector(entityClass);
			if(!TextUtils.isEmpty(sortBy))
				selector.orderBy(sortBy, desc);
			if(limit > 0) {
				selector.limit(limit);
				selector.offset(startIndex);
			}
			if(whereBuilder != null)
				selector.where(whereBuilder);
			result = selector.findAll();
		} catch (Exception e) {
			XLog.e(TAG, "queryList", e);
		}

		return result;
	}


}
