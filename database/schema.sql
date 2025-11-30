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
    subject_id INT NOT NULL DEFAULT 1 COMMENT '科目ID，1-语文，2-数学，3-英语',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 作业内容表
CREATE TABLE homework_contents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL COMMENT '日期',
    homework_type_id INT NOT NULL COMMENT '作业类型ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (homework_type_id) REFERENCES homework_types(id),
    UNIQUE KEY unique_date_type (date, homework_type_id)
);

-- 作业布置记录表
CREATE TABLE homework_assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL COMMENT '日期',
    homework_type_id INT NOT NULL COMMENT '作业类型ID',
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
    subject_id INT NOT NULL DEFAULT 1 COMMENT '科目ID，1-语文，2-数学，3-英语',
    corrected BOOLEAN DEFAULT FALSE COMMENT '是否已订正',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (homework_type_id) REFERENCES homework_types(id),
    UNIQUE KEY unique_student_date_type (student_id, date, homework_type_id)
);

-- 创建科目表
CREATE TABLE subjects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '科目名称',
    type INT NOT NULL COMMENT '科目类型，1-语文，2-数学，3-英语'
);

-- 插入学生数据（53位学生）
INSERT INTO students (student_number, name, pinyin_initials) VALUES
('1', '张子妍', 'zzxy'),
('2', '吴秋怡', 'wqy'),
('3', '廖依媛', 'lyy'),
('4', '张冬妮', 'zdn'),
('5', '林逸翔', 'lxy'),
('6', '陈一凡', 'cyf'),
('7', '黄钊阳', 'hzy'),
('8', '李海菁', 'lhj'),
('9', '陈国顺', 'cgs'),
('10', '叶梓琪', 'yzq'),
('11', '曾宇萱', 'zyx'),
('12', '邵锦心', 'sjx'),
('13', '谢文雅', 'xwy'),
('14', '邱子瑜', 'qzy'),
('15', '欧阳舒俊', 'oysj'),
('16', '韩瓒雅', 'hzy'),
('17', '谢莀钰', 'xcy'),
('18', '吴志轩', 'wzx'),
('19', '陈锦航', 'cjh'),
('20', '谢文轩', 'xwx'),
('21', '朱凌', 'zl'),
('22', '韩瓒博', 'hzb'),
('23', '何锦承', 'hjc'),
('24', '李晓满', 'lxm'),
('25', '翁紫涵', 'wzh'),
('26', '李婷婷', 'ltt'),
('27', '徐子昊', 'xzh'),
('28', '丁可可', 'dkk'),
('29', '杨梓桐', 'yzt'),
('30', '陈金铃', 'cjl'),
('31', '欧阳舒政', 'oysz'),
('32', '吴昕', 'wx'),
('33', '陈嘉航', 'cjh'),
('34', '丘越诚', 'qyc'),
('35', '蔡曼欣', 'cmx'),
('36', '裴诗语', 'psy'),
('37', '郭子骞', 'gzq'),
('38', '朴智雅', 'pzy'),
('39', '王子越', 'wzy'),
('40', '黄骏邦', 'hjb'),
('41', '刘子航', 'lzh'),
('42', '刘胜典', 'lsd'),
('43', '李瑞阳', 'lry'),
('44', '周湉湉', 'ztt'),
('45', '祝孟宸', 'zmc'),
('46', '钟鼎航', 'zdh'),
('47', '段鸿涛', 'dht'),
('48', '易芊妤', 'yqy'),
('49', '郑大恩', 'zde'),
('50', '刘天戟', 'ltj'),
('51', '范芯蕊', 'fxr'),
('52', '周嘉鸿', 'zjh'),
('53', '郭钰莹', 'gyy');

-- 插入科目数据
INSERT INTO subjects (name, type) VALUES 
('语文', 1),
('数学', 2),
('英语', 3);

-- 插入作业本类型
INSERT INTO homework_types (name, subject_id) VALUES
('听写本', 1),
('生字本', 1),
('词语本', 1),
('综合本', 1),
('家默本', 1),
('数学通关题', 2),
('英语听写', 3);