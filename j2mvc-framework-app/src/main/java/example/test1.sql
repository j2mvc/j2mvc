-- 测试系统数据库
drop database test1;
create database test1;
grant all privileges on test1.* to test1@localhost identified by 'test1@123' WITH GRANT OPTION;
flush privileges;
use test1;

-- 用户
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users(
	id					varchar(30)	NOT NULL,
	username			varchar(50)	NOT NULL COMMENT '用户名',			
	password			varchar(50)	NOT NULL COMMENT '密码',				
	sessionid			varchar(50)	NOT NULL COMMENT '会话ID',				
  	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

