-- KnowChain数据库初始化脚本
-- 如果数据库不存在则创建数据库
CREATE DATABASE IF NOT EXISTS knowchain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE knowchain;

-- 用户表
CREATE TABLE IF NOT EXISTS usertable (
    userid BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(MD5加密)',
    mailbox VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT chk_password_length CHECK (CHAR_LENGTH(password) >= 6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 文件和目录表
CREATE TABLE IF NOT EXISTS fileanddirtable (
    fID BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件/目录ID',
    fName VARCHAR(255) NOT NULL COMMENT '文件/目录名称',
    URL VARCHAR(500) COMMENT '文件路径(目录为NULL)',
    parentFID BIGINT COMMENT '父目录ID',
    isDir BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为目录',
    userID BIGINT NOT NULL COMMENT '所属用户ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_parent (userID, parentFID),
    INDEX idx_user_name (userID, fName),
    FOREIGN KEY (userID) REFERENCES usertable(userid) ON DELETE CASCADE,
    FOREIGN KEY (parentFID) REFERENCES fileanddirtable(fID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件和目录表';

-- 插入测试用户数据
INSERT IGNORE INTO usertable (username, password, mailbox) VALUES 
('admin123', '0192023a7bbd73250516f069df18b500', 'admin123@knowchain.com'), -- 密码: admin123 (8位,符合要求)
('test123', 'cc03e747a6afbbcbf8be7668acfebee5', 'test123@knowchain.com');   -- 密码: test123 (7位,符合要求)

-- 显示创建结果
SELECT 'Database initialization completed successfully!' as result;
SELECT COUNT(*) as user_count FROM usertable;
SELECT COUNT(*) as file_count FROM fileanddirtable;

-- DDL 表
CREATE TABLE IF NOT EXISTS DDLTable (
    dID BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'DDL ID',
    dTitle VARCHAR(255) NOT NULL COMMENT 'DDL 标题',
    dNotes TEXT COMMENT 'DDL 备注',
    dCreateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    dEndTime TIMESTAMP NOT NULL COMMENT '截止时间',
    userID BIGINT NOT NULL COMMENT '所属用户ID',
    FOREIGN KEY (userID) REFERENCES usertable(userid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='DDL 表';

-- TODO 表
CREATE TABLE IF NOT EXISTS todotable (
    tdID BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'TODO ID',
    tdDate DATE NOT NULL COMMENT 'TODO 日期',
    tdContent VARCHAR(255) NOT NULL COMMENT 'TODO 内容',
    tdStartTime TIME NOT NULL COMMENT '开始时间',
    tdEndTime TIME NOT NULL COMMENT '结束时间',
    userID BIGINT NOT NULL COMMENT '所属用户ID',
    FOREIGN KEY (userID) REFERENCES usertable(userid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='TODO 表';
