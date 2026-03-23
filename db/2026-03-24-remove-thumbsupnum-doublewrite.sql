-- 目标：将点赞统一到 storeup(type='21')，停止主表 thumbsupnum 作为真值来源。
-- 执行顺序建议：
-- 1) 先发布应用代码（已改为从 storeup 实时统计点赞）
-- 2) 再执行本脚本的“校验 + 可选清理”

-- =========================
-- 1) 校验数据（只读）
-- =========================

-- recipe：主表字段值 vs storeup 聚合值
SELECT
    r.id,
    r.thumbsupnum AS recipe_thumbsupnum,
    COALESCE(s.cnt, 0) AS storeup_thumbsupnum
FROM recipe r
LEFT JOIN (
    SELECT refid, COUNT(*) AS cnt
    FROM storeup
    WHERE tablename IN ('waiguomeishi', 'zhongshimeishi')
      AND type = '21'
    GROUP BY refid
) s ON s.refid = r.id
WHERE COALESCE(r.thumbsupnum, 0) <> COALESCE(s.cnt, 0);

-- news：主表字段值 vs storeup 聚合值
SELECT
    n.id,
    n.thumbsupnum AS news_thumbsupnum,
    COALESCE(s.cnt, 0) AS storeup_thumbsupnum
FROM news n
LEFT JOIN (
    SELECT refid, COUNT(*) AS cnt
    FROM storeup
    WHERE tablename = 'news'
      AND type = '21'
    GROUP BY refid
) s ON s.refid = n.id
WHERE COALESCE(n.thumbsupnum, 0) <> COALESCE(s.cnt, 0);

-- =========================
-- 2) 可选：一次性对齐（如果你仍想保留字段作缓存展示）
-- =========================

-- UPDATE recipe r
-- LEFT JOIN (
--     SELECT refid, COUNT(*) AS cnt
--     FROM storeup
--     WHERE tablename IN ('waiguomeishi', 'zhongshimeishi')
--       AND type = '21'
--     GROUP BY refid
-- ) s ON s.refid = r.id
-- SET r.thumbsupnum = COALESCE(s.cnt, 0);

-- UPDATE news n
-- LEFT JOIN (
--     SELECT refid, COUNT(*) AS cnt
--     FROM storeup
--     WHERE tablename = 'news'
--       AND type = '21'
--     GROUP BY refid
-- ) s ON s.refid = n.id
-- SET n.thumbsupnum = COALESCE(s.cnt, 0);

-- =========================
-- 3) 可选：彻底去冗余（确认运行稳定后执行）
-- =========================

-- ALTER TABLE recipe DROP COLUMN thumbsupnum;
-- ALTER TABLE news DROP COLUMN thumbsupnum;

