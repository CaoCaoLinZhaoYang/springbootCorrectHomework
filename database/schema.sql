-- 创建数据库
CREATE DATABASE IF NOT EXISTS correction_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE correction_system;

-- 学生表
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_number VARCHAR(20) UNIQUE NOT NULL COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    pinyin_initials VARCHAR(50) COMMENT '姓名拼音首字母'
);

-- 作业类型表
CREATE TABLE homework_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '作业类型名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 作业内容表
CREATE TABLE homework_contents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL COMMENT '日期',
    homework_type_id INT NOT NULL COMMENT '作业类型ID',
    content TEXT COMMENT '作业内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (homework_type_id) REFERENCES homework_types(id),
    UNIQUE KEY unique_date_type (date, homework_type_id)
);

-- 订正记录表
CREATE TABLE correction_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL COMMENT '日期',
    student_id INT NOT NULL COMMENT '学生ID',
    homework_type_id INT NOT NULL COMMENT '作业类型ID',
    corrected BOOLEAN DEFAULT FALSE COMMENT '是否已订正',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (homework_type_id) REFERENCES homework_types(id),
    UNIQUE KEY unique_student_date_type (student_id, date, homework_type_id)
);

-- 插入学生数据（10位学生）
INSERT INTO students (student_number, name, pinyin_initials) VALUES
('01', '张三', 'zs'),
('02', '李四', 'ls'),
('03', '王五', 'ww'),
('04', '赵六', 'zl'),
('05', '钱七', 'qq'),
('06', '孙八', 'sb'),
('07', '周九', 'zj'),
('08', '吴十', 'ws'),
('09', '郑一', 'zy'),
('10', '王二', 'we');

-- 插入作业本类型
INSERT INTO homework_types (name) VALUES
('听写本'),
('生字本'),
('词语本'),
('综合本'),
('家默本');