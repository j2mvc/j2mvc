package example;

import java.util.List;

import com.j2mvc.framework.dao.DaoSupport;


/**
 * @description 购物车测试
 * @cart 贵州沃尔达科技有限公司
 * @version 1.0 2014-9-5 创建@杨朔
 */
public class TestCartService{

	DaoSupport dao = new DaoSupport(TestCart.class);
	String tableName = "carts";
	
	/**
	 * 插入
	 * @param cart
	 * 
	 */
	public TestCart insert(TestCart cart) {		
		return (TestCart) dao.insert(cart);
	}
	/**
	 * 更新
	 * @param cart
	 * 
	 */
	public TestCart update(TestCart cart) {
		return (TestCart)dao.update(cart);
	}

	/**
	 * 保存
	 * @param cart
	 * 
	 */
	public TestCart save(TestCart cart){
		if(cart == null)
			return null;
		if(get(cart.getId())!=null){
			// 更新
			cart = update(cart);
		}else{
			// 新增
			cart = insert(cart);
		}
		return cart;
	}

	/**
	 * 获取
	 * @param id
	 * 
	 */
	public TestCart get(String id) {
		Object object = dao.get(id);
		return object!=null?(TestCart)object:null;
	}

	/**
	 * 删除一组
	 * @param ids
	 * 
	 */
	public Integer delete(String...ids) {
		Object[] object = ids;
		return dao.delete(object);
	}
	/**
	 * 预编译获取
	 * @param preSql 预编译语句	
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<TestCart> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql,params);
		return list!=null && list.size()>0?(List<TestCart>)list:null;
	}

}
