package example;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.DataSourceName;
import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.mapping.Table;


/**
 * description 用户测试
 * company 贵州沃尔达科技有限公司
 * version 1.0 2014-8-30 创建@杨朔
 */
@DataSourceName("jdbc/tanghaola_user")
@Table("sys_users")
@PrimaryKey(autoIncrement=false)
public class TestUser extends BaseEntity{
	private static final long serialVersionUID = -5956010554090972403L;

	/** id*/
	@Column(name="id",length=32,notnull=true)
	private String id;
	
	/** 用户名*/
	@Column(name="username",length=32,notnull=true)
	private String username;

	/** 密码 */
	@Column(name="password",length=64,notnull=true)
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}
