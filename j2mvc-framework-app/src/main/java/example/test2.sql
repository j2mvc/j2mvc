-- 测试系统数据库
drop database test2;
create database test2;
grant all privileges on test2.* to test2@localhost identified by 'test2@123' WITH GRANT OPTION;
flush privileges;
use test2;

-- 用户
DROP TABLE IF EXISTS carts;
CREATE TABLE IF NOT EXISTS carts(
	id					varchar(30)	NOT NULL,
	name				varchar(50)	NOT NULL COMMENT '名称',			
	price				double(10,2)	NOT NULL COMMENT '价格',			
  	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

