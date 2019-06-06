package example;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.DataSourceName;
import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.mapping.Table;


/**
 * @description 购物车测试
 * @company 贵州沃尔达科技有限公司
 * @version 1.0 2014-8-30 创建@杨朔
 */
@DataSourceName("jdbc/test2")
@Table("carts")
@PrimaryKey(autoIncrement=false)
public class TestCart extends BaseEntity{
	private static final long serialVersionUID = -5956010554090972403L;

	/** id*/
	@Column(name="id",length=32,notnull=true)
	private String id;
	
	/** 名称 */
	@Column(name="name",length=32,notnull=true)
	private String name;

	/** 价格 */
	@Column(name="price",notnull=true)
	private double price;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
