-- Admin 表字段规范化迁移（执行前请先备份）
-- 目标：admin.addtime -> admin.created_at

-- 1) 结构迁移
ALTER TABLE `admin`
  CHANGE COLUMN `addtime` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 2) 自检
-- SELECT `id`,`username`,`created_at` FROM `admin` LIMIT 20;

-- 回滚 SQL（如需）
-- ALTER TABLE `admin`
--   CHANGE COLUMN `created_at` `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间';
