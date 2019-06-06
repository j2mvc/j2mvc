package example;

import java.util.List;

import com.j2mvc.framework.Session;
import com.j2mvc.framework.config.Config;


public class TestMain {

	public static void main(String...args){

		// 初始化配置文件
		Config.init();
		System.out.println("是否输出sql语句:"+Session.sqlLog);

	
		TestUserService service = new TestUserService();
//		TestUser user = new TestUser();
//		user.setId("100212");
//		user.setUsername("test1");
//		user.setPassword("test123");
//		user = service.save(user);
//		
//		if(user!=null){
//			System.out.println("user数据已保存");
//		}
//		
		List<TestUser> users = service.query("select * from sys_users", null);
		for(int i=0;i<users.size();i++){
			TestUser u = users.get(i);
			System.out.println(i+":username="+u.getUsername()+";password="+u.getPassword());
		}
//
//		TestCartService cs = new TestCartService();
//		TestCart cart = new TestCart();
//		cart.setId("102212");
//		cart.setName("苹果笔记本电脑FG");
//		cart.setPrice(8988);;
//		cart = cs.save(cart);
//		
//		if(cart!=null){
//			System.out.println("cart数据已保存");
//		}
//		
//		List<TestCart> carts = cs.query("select * from carts", null);
//		for(TestCart ca:carts){
//			System.out.println("cart:name="+ca.getName()+";price="+ca.getPrice());
//		}
	}
}
