-- 已有库升级：菜谱表去掉冗余的用户账号、昵称（以 userid 关联 user 表为准）
ALTER TABLE `recipe`
  DROP COLUMN `yonghuzhanghao`,
  DROP COLUMN `yonghuxingming`;
