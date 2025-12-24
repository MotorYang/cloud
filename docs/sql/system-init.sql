-- 用户表
CREATE TABLE sys_user (
                          user_id VARCHAR(50) PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(100) NOT NULL,
                          nick_name VARCHAR(50),
                          user_type VARCHAR(2) DEFAULT '00',
                          phone VARCHAR(11),
                          email VARCHAR(50),
                          sex VARCHAR(1),
                          avatar VARCHAR(255),
                          status VARCHAR(1) DEFAULT '0',
                          deleted VARCHAR(1) DEFAULT '0',
                          create_by VARCHAR(64),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_by VARCHAR(64),
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          remark VARCHAR(500)
);

-- 角色表
CREATE TABLE sys_role (
                          role_id VARCHAR(50) PRIMARY KEY,
                          role_name VARCHAR(50) NOT NULL,
                          role_key VARCHAR(100) NOT NULL,
                          role_sort INTEGER DEFAULT 0,
                          status VARCHAR(1) DEFAULT '0',
                          deleted VARCHAR(1) DEFAULT '0',
                          create_by VARCHAR(64),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_by VARCHAR(64),
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          remark VARCHAR(500)
);

-- 菜单表
CREATE TABLE sys_menu (
                          menu_id VARCHAR(50) PRIMARY KEY,
                          menu_name VARCHAR(50) NOT NULL,
                          parent_id BIGINT DEFAULT 0,
                          order_num INTEGER DEFAULT 0,
                          path VARCHAR(200),
                          component VARCHAR(255),
                          menu_type VARCHAR(1),
                          visible VARCHAR(1) DEFAULT '0',
                          status VARCHAR(1) DEFAULT '0',
                          perms VARCHAR(100),
                          icon VARCHAR(100),
                          create_by VARCHAR(64),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_by VARCHAR(64),
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          remark VARCHAR(500)
);

-- 用户角色关联表
CREATE TABLE sys_user_role (
                               user_id varchar(50) NOT NULL,
                               role_id varchar(50) NOT NULL,
                               PRIMARY KEY (user_id, role_id)
);

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
                               role_id varchar(50) NOT NULL,
                               menu_id varchar(50) NOT NULL,
                               PRIMARY KEY (role_id, menu_id)
);

-- 插入测试数据
INSERT INTO sys_user (username, password, nick_name, user_type, status)
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU.8aVq88kW', '管理员', '00', '0');
-- 密码是 admin123