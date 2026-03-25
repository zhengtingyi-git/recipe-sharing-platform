# 命名规范与迁移策略（Recipe Sharing）

本文档用于统一本仓库的**目录 / API / 数据库表与字段 / Java 代码**命名风格，并定义从历史命名（拼音、缩写、混用）迁移到标准命名的**分阶段策略**。

## 总体策略（强制按阶段执行）

- **P0（不改表结构）**：统一代码层命名与含义表达（枚举/常量/DTO），对外接口保持兼容。
- **P1（兼容迁移）**：数据库采用“**双写双读**”过渡：新增标准字段并回填；代码优先读新字段、同时写新旧字段；验证完成后停写旧字段。
- **P2（最终落库/重命名）**：物理字段/表/目录/路由重命名；移除兼容层（旧字段、旧常量、旧路径）。

> 重要：任何 P1/P2 变更都必须包含**迁移 SQL + 回滚 SQL**，并配套最小回归（接口 + 关键页面 + 数据一致性检查）。

## 通用命名规则

### 数据库

- **表名**：全英文 `snake_case`，语义清晰（如 `users`/`recipes`/`recipe_comments`/`user_follows`）。
- **主键**：统一 `id`。
- **外键**：统一 `*_id`（如 `user_id`、`recipe_id`、`post_id`）。
- **时间字段**：统一 `created_at`、`updated_at`（如无更新时间可先只加 `created_at`）。
- **状态字段**：避免缩写（不要 `sfsh`），推荐 `status` 或 `review_status`。
- **内容字段**：语义化（如 `comment_content`、`reply_content`），避免泛化 `content/reply` 在多个领域混用。

### Java（后端）

- **类名**：`PascalCase`，领域清晰（如 `RecipeEntity`、`RecipeCommentEntity`）。
- **字段/变量**：`camelCase`（如 `createdAt`、`userId`）。
- **兼容映射**：用 `@TableField("created_at")` 映射新列；旧列用 `@TableField(exist=false)` 仅作对外兼容返回或入参兼容（必要时）。

### 前端

- **目录**：`kebab-case`（如 `recipes/recipe-comments/`）。
- **路由**：资源化路径（如 `/recipes`、`/recipes/:id`、`/recipes/:id/comments`）。
- **API**：避免拼音与混拼，统一 REST 风格（如 `/api/recipes`、`/api/user-follows`）。

## 本项目当前已发现的典型“旧命名”

### 字段（旧 -> 新建议）

| 旧字段 | 问题 | 新字段建议 |
|---|---|---|
| `addtime` | 非标准时间名 | `created_at` |
| `userid` | 非 `*_id` 风格 | `user_id` |
| `refid` | 含义不清（引用谁） | `ref_id`（短期）→ 最终建议领域化如 `recipe_id`/`post_id` |
| `type`（旧交互字段示例） | 语义模糊/魔法值 | `interaction_type`（或 `favorite_type`/`action_type`） |
| `sfsh` | 缩写不可读 | `review_status`（或 `status`） |
| `shhf` | 缩写不可读 | `review_reply`（或 `review_comment`） |
| `yonghuzhanghao` | 拼音字段 | `username` |
| `mima` | 拼音字段 | `password` |
| `touxiang` | 拼音字段 | `avatar_url`（或 `avatar`） |
| `caipinmingcheng` | 拼音字段 | `recipe_name`（或 `name`） |
| `caipinleixing` | 拼音字段 | `category`（或 `recipe_category`） |
| `pengrenfangfa` | 拼音字段 | `instructions`（或 `cooking_method`） |
| `cailiao` | 拼音字段 | `ingredients` |
| `recipetype` | 领域不清（分类/来源） | `recipe_type`（短期兼容）→ 最终建议 `origin_type` 或拆表 |

> 说明：`refid`/`recipetype` 属于“短期兼容字段”，最终应在 P2 阶段改成明确外键/枚举字段，减少歧义。

### 表与模块（旧 -> 新建议）

| 当前表/模块名 | 实际含义 | 新建议 |
|---|---|---|
| `旧交互表`（收藏/点赞） | 收藏/点赞等交互 | `user_interactions`（或拆分为 `favorites`、`likes`） |
| `recipe` + `recipetype=waiguomeishi/zhongshimeishi` | 菜谱（按来源分类） | `recipes` + `recipe_type`（短期）→ 最终拆分或改为枚举字段 |
| `recipe_comment` | 菜谱评论 | `recipe_comments` |
| `user` | 用户 | `users` |
| `admin` | 管理员 | `admins` |
| 前端 `waiguomeishi/zhongshimeishi` 目录 | 菜谱列表/详情 | `recipes`（以类型筛选呈现） |
| 前端 `discusswaiguomeishi/discusszhongshimeishi` | 评论 | `recipe-comments`（以类型筛选呈现） |

## 迁移执行清单（建议顺序）

### 第一批（低风险，先统一 created_at）

- `旧交互表`：已提供兼容脚本（新增/回填/索引）。
- `recipe`：新增 `created_at` 回填自 `addtime`，代码排序切换到 `created_at`。
- `recipe_comment`：新增 `created_at` 回填自 `addtime`，代码排序切换到 `created_at`。

### 第二批（统一 *_id）

- `recipe.user_id`（由 `userid` 迁移）
- `recipe_comment.user_id`、`recipe_comment.ref_id`（由 `userid/refid` 迁移）
- `forum_post.user_id`（由 `userid` 迁移）

### 第三批（清理语义不清字段）

- 旧交互的 `type` 字段迁移为 `interaction_type`（并引入枚举常量，禁用魔法值）。
- `sfsh/shhf` 迁移为 `review_status/review_reply`。
- 拼音字段（用户、菜谱详情字段）逐步迁移为英文语义字段。

## 回归与验收（每批必须做）

- **接口回归**：列表/详情/新增/删除/评论/收藏/点赞/关注关键链路。
- **数据一致性**：新旧字段对比（新字段为空的比例应为 0，或符合预期）。
- **索引**：列表过滤与排序使用新字段对应索引。

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

