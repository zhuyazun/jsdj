package com.sum.alchemist.model.db;

import org.xutils.db.sqlite.WhereBuilder;

import java.util.List;

/**
 * 数据访问基础抽象接口
 * Created by Qiu on 2016/3/31
 */
interface BaseDao <T> {
	
	/** 
	*  
	* 向数据库请求插入一条数据
	* @param t 泛型对象，这个对象将被插入到数据库，每个对象对应数据库一笔记录
	* @return boolean 插入成功返回true ，插入失败返回 false 
	*/
	boolean insert(T t);
	
	/**
	* 像数据库请求插入一笔数据
	* @param ts 泛型对象，这个对象将被插入到数据库，每个对象对应数据库一笔记录
	* @return boolean 插入成功返回true ，插入失败返回 false 
	*/
	boolean insert(List<T> ts);
	
	/**
	* 向数据库请求替换式插入一条数据（这个数据必须遵循唯一约束）
	* @param t 泛型对象，这个对象将被替换式插入到数据库，每个对象对应数据库一笔记录
	* @return boolean  插入成功返回true ，插入失败返回 false  
	*/
	boolean replace(T t);
	
	
	/**
	* 像数据库请求批量替换插入数据
	* @param ts 泛型对象，这个对象将被插入到数据库，每个对象对应数据库一笔记录
	* @return boolean 插入成功返回true ，插入失败返回 false 
	*/
	boolean replace(List<T> ts);
	
	/**
	* 向数据库请求更新一条数据
	* @param t 泛型对象，这个对象将更新数据库里现有的一笔数据（如果该数据存在）
	* @return boolean    修改成功返回true ，修改失败返回 false  
	*/
	boolean update(T t);
	
	/**
	* 向数据库请求删除数据库所有数据（如果该数据存在）
	* @return boolean   删除成功返回true ，  失败返回false
	*/
	boolean deleteAll();
	
	/**
	* 向数据库请求删除一笔数据，根据传入的对象
	* @param o id对象
	* @return boolean  删除成功返回true ，  失败返回false
	*/
	boolean deleteById(Object o);
	
	/**
	* 根据id 获取某笔数据
	* @param o 查询id
	* @return T 查询到的对象，如果该对象为null，说明不存在该笔数据对应id ‘o’
	*/
	T queryById(Object o);
	
	/**
	* 获取一个数据库表里的所有数据
	* @return List<T> 返回null 表示数据库没有任何内容，不为null则每一个list的元素就对应数据库里的一笔记录，默认按id排序
	*/
	List<T> queryList();
	
	/**
	* 获取一个数据库表里的所有数据  ，并按某一字段排序
	* @param sortBy 按该字段作为排序依据 ，如果该值为null那么使用数据库默认排序
	* @param desc 是否降序排列 
	* @return List<T>   返回null 表示数据库没有任何内容，不为null则每一个list的元素就对应数据库里的一笔记录，默认按sortBy排序
	*/
	List<T> queryList(String sortBy, boolean desc);
	
	/**
	* 获取一个数据库表里的所有数据  ，并按某一字段排序，支持分页查询
	* @param  sortBy 按该字段作为排序依据 ，如果该值为null那么使用数据库默认排序
	* @param  desc 是否降序排列
	* @param  startIndex 从这个位置开始获取数据
	* @param  limit 获取多少笔数据
	* @return List<T>  返回null 表示数据库没有任何内容，不为null则每一个list的元素就对应数据库里的一笔记录，默认按sortBy排序,按startIndex ,limit 做分页查询
	*/
	List<T> queryList(String sortBy, boolean desc, int startIndex, int limit);

	List<T> queryList(String sortBy, boolean desc, int startIndex, int limit, WhereBuilder whereBuilder);
	

}
