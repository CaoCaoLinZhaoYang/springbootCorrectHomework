-- 向homework_types表添加subject_id字段
ALTER TABLE homework_types ADD COLUMN subject_id INT NOT NULL DEFAULT 1 COMMENT '科目ID，1-语文，2-数学，3-英语';

-- 向correction_records表添加subject_id字段
ALTER TABLE correction_records ADD COLUMN subject_id INT NOT NULL DEFAULT 1 COMMENT '科目ID，1-语文，2-数学，3-英语';

-- 创建科目表
CREATE TABLE subjects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '科目名称',
    type INT NOT NULL COMMENT '科目类型，1-语文，2-数学，3-英语'
);

-- 插入科目数据
INSERT INTO subjects (name, type) VALUES 
('语文', 1),
('数学', 2),
('英语', 3);

-- 更新作业类型与科目的关联关系
UPDATE homework_types SET subject_id = 1 WHERE name IN ('听写本', '生字本', '词语本', '综合本', '家默本');
UPDATE homework_types SET subject_id = 2 WHERE name = '数学通关题';
UPDATE homework_types SET subject_id = 3 WHERE name = '英语听写';

-- 更新订正记录与科目的关联关系
UPDATE correction_records cr 
JOIN homework_types ht ON cr.homework_type_id = ht.id 
SET cr.subject_id = ht.subject_id;