# 命名规范（落地版）

本规范用于统一本项目数据库、后端、前端命名，避免拼音/英文混用和风格不一致。

## 1. 数据库命名

- 表名：全英文 `snake_case`，优先业务语义（如 `user_follow`, `recipe_comment`）。
- 字段名：全英文 `snake_case`。
- 外键：统一 `*_id`（如 `user_id`, `recipe_id`）。
- 时间字段：统一 `created_at`, `updated_at`。
- 状态字段：使用可读英文（如 `review_status`），禁止不可读缩写。
- 枚举值：避免魔法值，需在注释中标注含义，或改为字符串枚举。

## 2. Java 命名

- 类名：`PascalCase`（如 `UserFollowController`）。
- 方法/属性：`camelCase`（如 `followersCount`, `createdAt`）。
- 实体映射：
  - 业务层字段使用英文语义命名；
  - 兼容期通过 `@TableField` 或返回字段双写保持兼容；
  - 兼容结束后移除旧字段别名。

## 3. 前端命名

- 目录/文件：`kebab-case`（如 `my-follow.html`）。
- JS 变量与方法：`camelCase`。
- 接口路径：使用英文资源语义（如 `/user-follows/following`）。
- 页面渲染字段：优先英文字段，兼容期可兜底旧字段。

## 4. 迁移策略（必须遵守）

- 第一步：新增英文字段/路径，不删旧字段/旧路径。
- 第二步：双写双读，所有调用方切新。
- 第三步：灰度观察稳定后，下线旧命名。
- 第四步：执行清理脚本，删除旧字段和旧接口别名。

## 5. 关注模块改造基线（已开始）

- 路由兼容：`/guanzhu` + `/user-follows`。
- 返回字段兼容：保留 `yonghuxingming`，新增 `displayName`。
- 时间字段兼容：保留 `addtime`，新增 `createdAt`。

