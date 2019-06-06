package example;

import java.util.List;

import com.j2mvc.util.Utils;
import com.j2mvc.framework.dao.DaoSupport;


/**
 * description 用户测试
 * user 贵州沃尔达科技有限公司
 * version 1.0 2014-9-5 创建@杨朔
 */
public class TestUserService{

	DaoSupport dao = new DaoSupport(TestUser.class);
	String tableName = "users";
	
	/**
	 * 插入
	 * @param user
	 * 
	 */
	public TestUser insert(TestUser user) {		
		return (TestUser) dao.insert(user);
	}
	/**
	 * 更新
	 * @param user
	 * 
	 */
	public TestUser update(TestUser user) {
		return (TestUser)dao.update(user);
	}

	/**
	 * 保存
	 * @param user
	 * 
	 */
	public TestUser save(TestUser user){
		if(user == null)
			return null;
		if(get(user.getId())!=null){
			// 更新
			user = update(user);
		}else{
			// 新增
			user = insert(user);
		}
		return user;
	}

	/**
	 * 获取
	 * @param id
	 * 
	 */
	public TestUser get(String id) {
		Object object = dao.get(id);
		return object!=null?(TestUser)object:null;
	}

	/**
	 * 删除一组
	 * @param id
	 * 
	 */
	public Integer delete(String...ids) {
		Object[] object = ids;
		return dao.delete(object);
	}
	/**
	 * 查看用户是否注册
	 * @param username
	 * 
	 */
	public boolean existsUsername(String username,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE username=? and id<>?";
		Integer result = dao.number(preSql, new String[]{username,id});
		return (result!=null?result:0)>0;
	}


	/**
	 * 查看指定手机号，指定ID用户是否存在
	 * @param mobilephone
	 * @param id
	 * 
	 */
	public boolean existsMobile(String mobile,String id) {
		String preSql = "SELECT COUNT(*) FROM " + tableName + " WHERE mobile=? and  id=?";
		Integer result = dao.number(preSql, new String[]{mobile,id});
		return (result!=null?result:0)>0;
	}

	/**
	 * 预编译获取
	 * @param preSql 预编译语句	
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<TestUser> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql,params);
		return list!=null && list.size()>0?(List<TestUser>)list:null;
	}

	/**
	 * 登陆
	 * @param username
	 * @param password
	 * @param domain
	 * 
	 */
	public TestUser login(String username,String password){
		if(username.equals("") || password.equals(""))
			return null;
		String sql = "SELECT * FROM "+ tableName
				+ " WHERE username=? and  password=MD5(?)";
		Object object = dao.queryForObject(sql,new String[]{username,username,password});
		TestUser user = object!=null?(TestUser)object:null;
		TestUser sessionUser = null;
		if(user!=null){
			String sessionid = Utils.createSessionid(username, "user.worda.cn",password);
			sessionUser = setSessionUser(user, password);
			// 保存sessionid
			saveSessionid(sessionid,user.getId());
		}
		return sessionUser;
	}
	/**
	 * 根据sessionid获取
	 * @param sessionid
	 * 
	 */
	public TestUser getBySessionid(String sessionid){
		if(!(sessionid!=null && !sessionid.trim().equals("")))
			return null;
		String sql = "SELECT * FROM "+ tableName  + " WHERE sessionid=? ";
		Object object = dao.queryForObject(sql,new String[]{sessionid});
		TestUser user = object!=null?(TestUser)object:null;
		TestUser sessionUser = null;
		if(user!=null){
			String password = Utils.getPassword(sessionid);
			sessionUser = setSessionUser(user, password);
		}
		return sessionUser;
	}
	/**
	 * 设置会话用户变量
	 * @param user
	 * @param domain
	 * @param username
	 * @param password
	 * 
	 */
	private TestUser setSessionUser(TestUser user,String password){
		TestUser sessionUser = new TestUser();
		sessionUser.setId(user.getId());
		sessionUser.setUsername(user.getUsername());
		return sessionUser;
	}
	
	/**
	 * 将sessionkey保存到用户表
	 * @param sessionKey
	 * @param userid
	 * 
	 */
	public boolean saveSessionid(String sessionid,String userid){
		String updateSql = "update "+tableName+" set sessionid=? WHERE id=?";
		return dao.update(updateSql, new String []{sessionid,userid})>0;
	}
	/**
	 * 清除Sessionid
	 * @param id 可以是用户ID，也可是会话ID
	 * 
	 */
	public boolean clearSessionid(String id){
		String updateSql = "update "+tableName+" set sessionid='' WHERE id=? or sessionid=?";
		return dao.update(updateSql, new String []{id,id})>0;
	}
}
