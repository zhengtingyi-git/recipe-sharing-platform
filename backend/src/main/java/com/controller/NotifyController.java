package com.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.entity.TokenEntity;
import com.entity.UserFollowEntity;
import com.entity.UserInteractionsEntity;
import com.entity.ForeignRecipeEntity;
import com.entity.UserEntity;
import com.entity.ChineseRecipeEntity;
import com.entity.ForumPostEntity;
import com.entity.ForumPostCommentEntity;
import com.entity.ChineseRecipeCommentEntity;
import com.entity.ForeignRecipeCommentEntity;
import com.service.UserFollowService;
import com.service.UserInteractionsService;
import com.service.TokenService;
import com.service.UserService;
import com.service.ChineseRecipeService;
import com.service.ForeignRecipeService;
import com.service.ForumPostService;
import com.service.ForumPostCommentService;
import com.service.ChineseRecipeCommentService;
import com.service.ForeignRecipeCommentService;
import com.utils.R;

/**
 * ?????????????????????
 */
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private UserInteractionsService userInteractionsService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChineseRecipeService chinese_recipeService;
    @Autowired
    private ForeignRecipeService foreign_recipeService;
    @Autowired
    private ForumPostService forumPostService;
    @Autowired
    private ForumPostCommentService forumPostCommentService;
    @Autowired
    private ChineseRecipeCommentService chinese_recipe_commentService;
    @Autowired
    private ForeignRecipeCommentService foreign_recipe_commentService;
    @Autowired
    private TokenService tokenService;

    /**
     * ??????? ?????????????? ???????
     */
    @RequestMapping("/center")
    public R center(HttpServletRequest request) {
        Long userId = resolveCurrentUserId(request);
        if (userId == null) {
            return R.error("请先登录");
        }
        Map<String, Object> data = buildNotifyCenterData(userId);
        return R.ok().put("data", data);
    }

    /**
     * 未读条数：
     * - 若传 lastReadByTab（JSON，毫秒）：reply / thumbsup / userInteractions / followMe 各分类独立已读时间，缺省分类视为从未已读。
     * - 否则若传 lastReadAt（单值毫秒）：四类共用该时间（兼容旧前端）。
     * - 均未传：全部计未读。
     */
    @RequestMapping("/unreadCount")
    public R unreadCount(HttpServletRequest request) {
        Long userId = resolveCurrentUserId(request);
        if (userId == null) {
            return R.ok().put("data", unreadPayload(0));
        }
        if (!isFrontUser(request)) {
            return R.ok().put("data", unreadPayload(0));
        }
        Map<String, Object> data = buildNotifyCenterData(userId);
        Map<String, Long> byTab = parseLastReadByTab(request);
        int n;
        if (byTab != null) {
            n = countUnreadByTab(data, byTab);
        } else {
            Long lastReadMs = parseLastReadAtMillis(request);
            n = countUnreadAfter(data, lastReadMs);
        }
        return R.ok().put("data", unreadPayload(n));
    }

    private static final String[] NOTIFY_TAB_KEYS = { "reply", "thumbsup", "userInteractions", "followMe" };

    /** 解析 lastReadByTab JSON；无效或空串返回 null（走单值 lastReadAt 或全未读） */
    private static Map<String, Long> parseLastReadByTab(HttpServletRequest request) {
        String p = request.getParameter("lastReadByTab");
        if (p == null || p.trim().isEmpty()) {
            return null;
        }
        try {
            JSONObject o = JSONObject.parseObject(p.trim());
            if (o == null || o.isEmpty()) {
                return null;
            }
            Map<String, Long> m = new LinkedHashMap<>();
            for (String k : NOTIFY_TAB_KEYS) {
                if (o.containsKey(k) && o.get(k) != null) {
                    m.put(k, o.getLongValue(k));
                }
            }
            return m.isEmpty() ? null : m;
        } catch (Exception e) {
            return null;
        }
    }

    private static long tabCutoff(Map<String, Long> byTab, String key) {
        Long v = byTab.get(key);
        return v != null ? v : Long.MIN_VALUE;
    }

    private int countUnreadByTab(Map<String, Object> data, Map<String, Long> byTab) {
        int n = 0;
        n += countListUnread(data.get("replyList"), tabCutoff(byTab, "reply"));
        n += countListUnread(data.get("thumbsupList"), tabCutoff(byTab, "thumbsup"));
        n += countListUnread(data.get("userInteractionsList"), tabCutoff(byTab, "userInteractions"));
        n += countListUnread(data.get("followMeList"), tabCutoff(byTab, "followMe"));
        return n;
    }

    private static Long parseLastReadAtMillis(HttpServletRequest request) {
        String p = request.getParameter("lastReadAt");
        if (p == null || p.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(p.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Map<String, Object> unreadPayload(int n) {
        Map<String, Object> m = new HashMap<>();
        m.put("unreadCount", n);
        return m;
    }

    /**
     * 兼容旧前端：已读时间改由浏览器 localStorage 保存，此接口保留为成功应答即可。
     */
    @RequestMapping("/markRead")
    public R markRead(HttpServletRequest request) {
        Long userId = resolveCurrentUserId(request);
        if (userId == null) {
            return R.error("请先登录");
        }
        return R.ok();
    }

    private boolean isFrontUser(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        TokenEntity te = tokenService.getTokenEntity(token.trim());
        return te != null && "user".equals(te.getTablename());
    }

    private int countUnreadAfter(Map<String, Object> data, Long lastReadMs) {
        long cutoff = lastReadMs != null ? lastReadMs : Long.MIN_VALUE;
        int n = 0;
        n += countListUnread(data.get("replyList"), cutoff);
        n += countListUnread(data.get("thumbsupList"), cutoff);
        n += countListUnread(data.get("userInteractionsList"), cutoff);
        n += countListUnread(data.get("followMeList"), cutoff);
        return n;
    }

    @SuppressWarnings("unchecked")
    private int countListUnread(Object listObj, long cutoff) {
        if (!(listObj instanceof List)) {
            return 0;
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
        int c = 0;
        for (Map<String, Object> row : list) {
            if (toEpochMillis(row != null ? row.get("createdAt") : null) > cutoff) {
                c++;
            }
        }
        return c;
    }

    private Map<String, Object> buildNotifyCenterData(Long userId) {
        UserEntity currentUser = userService.selectById(userId);
        String selfName = currentUser != null ? currentUser.getYonghuxingming() : null;
        String selfTouxiang = currentUser != null ? currentUser.getTouxiang() : null;

        Map<String, Object> result = new HashMap<>();

        // 1. ???????????????????? / ??????
        List<Map<String, Object>> replyList = new ArrayList<>();

        // 1.1 ?????????????????/???????
        EntityWrapper<ForumPostEntity> forumPostEw = new EntityWrapper<>();
        forumPostEw.eq("user_id", userId);
        List<ForumPostEntity> myForumPosts = forumPostService.selectList(forumPostEw);
        if (!myForumPosts.isEmpty()) {
            List<Long> myForumPostIds = myForumPosts.stream().map(ForumPostEntity::getId).collect(Collectors.toList());
            EntityWrapper<ForumPostCommentEntity> discussEw = new EntityWrapper<>();
            discussEw.in("post_id", myForumPostIds);
            discussEw.orderBy("created_at", false);
            List<ForumPostCommentEntity> comments = forumPostCommentService.selectList(discussEw);
            for (ForumPostCommentEntity c : comments) {
                // ???????????????????????????????
                if (c.getUserId() != null && c.getUserId().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("resourceId", c.getResourceId());
                item.put("createdAt", c.getCreatedAt());
                item.put("content", c.getContent());
                ForumPostEntity post = forumPostService.selectById(c.getResourceId());
                if (post != null) {
                    item.put("messageContent", post.getTitle());
                    item.put("picture", post.getPicture());
                }
                UserEntity commenter = c.getUserId() != null ? userService.selectById(c.getUserId()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "??"));
                item.put("avatar", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.2 中式菜品下他人评论（与赞藏列表一致，兼容 source_type 为空）
        EntityWrapper<ChineseRecipeEntity> zhongshiEw = new EntityWrapper<>();
        zhongshiEw.eq("user_id", userId);
        zhongshiEw.and("(source_type = 'chinese_recipe' OR source_type IS NULL OR source_type = '')");
        List<ChineseRecipeEntity> myZhongshi = chinese_recipeService.selectList(zhongshiEw);
        if (!myZhongshi.isEmpty()) {
            List<Long> myZhongshiIds = myZhongshi.stream().map(ChineseRecipeEntity::getId).collect(Collectors.toList());
            EntityWrapper<ChineseRecipeCommentEntity> discussZhongshiEw = new EntityWrapper<>();
            // recipe_comment 字段语义化：refid -> recipe_id, recipetype -> source_type, addtime -> created_at
            discussZhongshiEw.in("recipe_id", myZhongshiIds);
            discussZhongshiEw.eq("source_type", "chinese_recipe");
            discussZhongshiEw.orderBy("created_at", false);
            List<ChineseRecipeCommentEntity> commentsZhongshi = chinese_recipe_commentService.selectList(discussZhongshiEw);
            for (ChineseRecipeCommentEntity c : commentsZhongshi) {
                // ??????????????
                if (c.getUserId() != null && c.getUserId().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("resourceId", c.getResourceId());
                item.put("createdAt", c.getCreatedAt());
                item.put("content", c.getContent());
                ChineseRecipeEntity dish = chinese_recipeService.selectById(c.getResourceId());
                if (dish != null) {
                    item.put("messageContent", dish.getCaipinmingcheng());
                    item.put("picture", dish.getTupian());
                }
                UserEntity commenter = c.getUserId() != null ? userService.selectById(c.getUserId()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "??"));
                item.put("avatar", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.3 ???????????
        EntityWrapper<ForeignRecipeEntity> waiguoEw = new EntityWrapper<>();
        waiguoEw.eq("user_id", userId);
        waiguoEw.eq("source_type", "foreign_recipe");
        List<ForeignRecipeEntity> myWaiguo = foreign_recipeService.selectList(waiguoEw);
        if (!myWaiguo.isEmpty()) {
            List<Long> myWaiguoIdsForReply = myWaiguo.stream().map(ForeignRecipeEntity::getId).collect(Collectors.toList());
            EntityWrapper<ForeignRecipeCommentEntity> discussWaiguoEw = new EntityWrapper<>();
            discussWaiguoEw.in("recipe_id", myWaiguoIdsForReply);
            discussWaiguoEw.eq("source_type", "foreign_recipe");
            discussWaiguoEw.orderBy("created_at", false);
            List<ForeignRecipeCommentEntity> commentsWaiguo = foreign_recipe_commentService.selectList(discussWaiguoEw);
            for (ForeignRecipeCommentEntity c : commentsWaiguo) {
                // ??????????????
                if (c.getUserId() != null && c.getUserId().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("resourceId", c.getResourceId());
                item.put("createdAt", c.getCreatedAt());
                item.put("content", c.getContent());
                ForeignRecipeEntity dish = foreign_recipeService.selectById(c.getResourceId());
                if (dish != null) {
                    item.put("messageContent", dish.getCaipinmingcheng());
                    item.put("picture", dish.getTupian());
                }
                UserEntity commenter = c.getUserId() != null ? userService.selectById(c.getUserId()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "??"));
                item.put("avatar", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.4 ???????????????????????????????
        // 1.4.1 ???????????
        EntityWrapper<ForumPostCommentEntity> myForumPostCommentEw = new EntityWrapper<>();
        myForumPostCommentEw.eq("user_id", userId)
                .isNotNull("reply_content")
                .ne("reply_content", "");
        myForumPostCommentEw.orderBy("created_at", false);
        List<ForumPostCommentEntity> myForumPostComments = forumPostCommentService.selectList(myForumPostCommentEw);
        for (ForumPostCommentEntity c : myForumPostComments) {
            String replyStr = c.getReply();
            if (replyStr == null || replyStr.trim().isEmpty()) {
                continue;
            }
            ForumPostEntity post = forumPostService.selectById(c.getResourceId());
            String picture = post != null ? post.getPicture() : null;
            String defaultName = "??";
            String defaultTouxiang = "";
            if (post != null && post.getUserId() != null) {
                UserEntity author = userService.selectById(post.getUserId());
                if (author != null) {
                    if (author.getYonghuxingming() != null) {
                        defaultName = author.getYonghuxingming();
                    }
                    if (author.getTouxiang() != null) {
                        defaultTouxiang = author.getTouxiang();
                    }
                }
            }
            addReplyItemsFromJson(replyList, replyStr, c.getId(), c.getResourceId(), c.getCreatedAt(),
                    c.getContent(), picture, defaultName, defaultTouxiang, selfName, selfTouxiang);
        }

        // 1.4.2 ???????????
        EntityWrapper<ChineseRecipeCommentEntity> myDiscussZhongshiEw = new EntityWrapper<>();
        myDiscussZhongshiEw.eq("user_id", userId)
                .eq("source_type", "chinese_recipe")
                .isNotNull("reply")
                .ne("reply", "");
        myDiscussZhongshiEw.orderBy("created_at", false);
        List<ChineseRecipeCommentEntity> myDiscussZhongshi = chinese_recipe_commentService.selectList(myDiscussZhongshiEw);
        for (ChineseRecipeCommentEntity c : myDiscussZhongshi) {
            String replyStr = c.getReply();
            if (replyStr == null || replyStr.trim().isEmpty()) {
                continue;
            }
            ChineseRecipeEntity dish = chinese_recipeService.selectById(c.getResourceId());
            String picture = dish != null ? dish.getTupian() : null;
            String defaultName = "??";
            String defaultTouxiang = "";
            if (dish != null && dish.getUserId() != null) {
                UserEntity author = userService.selectById(dish.getUserId());
                if (author != null) {
                    if (author.getYonghuxingming() != null) {
                        defaultName = author.getYonghuxingming();
                    }
                    if (author.getTouxiang() != null) {
                        defaultTouxiang = author.getTouxiang();
                    }
                }
            }
            addReplyItemsFromJson(replyList, replyStr, c.getId(), c.getResourceId(), c.getCreatedAt(),
                    c.getContent(), picture, defaultName, defaultTouxiang, selfName, selfTouxiang);
        }

        // 1.4.3 ???????????
        EntityWrapper<ForeignRecipeCommentEntity> myDiscussWaiguoEw = new EntityWrapper<>();
        myDiscussWaiguoEw.eq("user_id", userId)
                .eq("source_type", "foreign_recipe")
                .isNotNull("reply")
                .ne("reply", "");
        myDiscussWaiguoEw.orderBy("created_at", false);
        List<ForeignRecipeCommentEntity> myDiscussWaiguo = foreign_recipe_commentService.selectList(myDiscussWaiguoEw);
        for (ForeignRecipeCommentEntity c : myDiscussWaiguo) {
            String replyStr = c.getReply();
            if (replyStr == null || replyStr.trim().isEmpty()) {
                continue;
            }
            ForeignRecipeEntity dish = foreign_recipeService.selectById(c.getResourceId());
            String picture = dish != null ? dish.getTupian() : null;
            String defaultName = "??";
            String defaultTouxiang = "";
            if (dish != null && dish.getUserId() != null) {
                UserEntity author = userService.selectById(dish.getUserId());
                if (author != null) {
                    if (author.getYonghuxingming() != null) {
                        defaultName = author.getYonghuxingming();
                    }
                    if (author.getTouxiang() != null) {
                        defaultTouxiang = author.getTouxiang();
                    }
                }
            }
            addReplyItemsFromJson(replyList, replyStr, c.getId(), c.getResourceId(), c.getCreatedAt(),
                    c.getContent(), picture, defaultName, defaultTouxiang, selfName, selfTouxiang);
        }

        // ??????????????????????????? + ???????????
        replyList.sort(this::compareNotifyTimeDesc);
        result.put("replyList", replyList);

        // 2. 点赞 / 收藏（interaction_type=21 / 1）
        // 兼容 recipe 表里 source_type 为空的历史数据，否则「我的作品」ID 集合为空，消息中心一直无赞藏提醒
        EntityWrapper<ChineseRecipeEntity> zew = new EntityWrapper<>();
        zew.eq("user_id", userId);
        zew.and("(source_type = 'chinese_recipe' OR source_type IS NULL OR source_type = '')");
        List<Long> myZhongshiIds = chinese_recipeService.selectList(zew).stream()
                .map(ChineseRecipeEntity::getId).collect(Collectors.toList());
        EntityWrapper<ForeignRecipeEntity> wew = new EntityWrapper<>();
        wew.eq("user_id", userId);
        wew.eq("source_type", "foreign_recipe");
        List<Long> myWaiguoIds = foreign_recipeService.selectList(wew).stream()
                .map(ForeignRecipeEntity::getId).collect(Collectors.toList());

        EntityWrapper<ForumPostEntity> forumPostThumbEw = new EntityWrapper<>();
        forumPostThumbEw.eq("user_id", userId);
        List<Long> myForumPostIdsForLike = forumPostService.selectList(forumPostThumbEw).stream()
                .map(ForumPostEntity::getId).collect(Collectors.toList());

        List<Map<String, Object>> thumbsupList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw = new EntityWrapper<>();
            storeEw.eq("interaction_type", "21").in("resource_id", myZhongshiIds);
            storeEw.orderBy("created_at", false);
            List<UserInteractionsEntity> thumbs = userInteractionsService.selectList(storeEw);
            for (UserInteractionsEntity s : thumbs) {
                if (isSelfActionOnOwnContent(s, userId)) {
                    continue;
                }
                thumbsupList.add(buildUserInteractionsItem(s, "thumbsup"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw2 = new EntityWrapper<>();
            storeEw2.eq("interaction_type", "21").in("resource_id", myWaiguoIds);
            storeEw2.orderBy("created_at", false);
            List<UserInteractionsEntity> thumbs = userInteractionsService.selectList(storeEw2);
            for (UserInteractionsEntity s : thumbs) {
                if (isSelfActionOnOwnContent(s, userId)) {
                    continue;
                }
                thumbsupList.add(buildUserInteractionsItem(s, "thumbsup"));
            }
        }
        if (!myForumPostIdsForLike.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEwForumPost = new EntityWrapper<>();
            storeEwForumPost.eq("interaction_type", "21").in("resource_id", myForumPostIdsForLike);
            storeEwForumPost.orderBy("created_at", false);
            List<UserInteractionsEntity> forumPostThumbs = userInteractionsService.selectList(storeEwForumPost);
            for (UserInteractionsEntity s : forumPostThumbs) {
                if (isSelfActionOnOwnContent(s, userId)) {
                    continue;
                }
                thumbsupList.add(buildUserInteractionsItem(s, "thumbsup"));
            }
        }
        thumbsupList.sort(this::compareNotifyTimeDesc);
        result.put("thumbsupList", thumbsupList);

        // 3. ????????? / ?? ????type=1?
        List<Map<String, Object>> userInteractionsList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw3 = new EntityWrapper<>();
            storeEw3.eq("interaction_type", "1").in("resource_id", myZhongshiIds);
            storeEw3.orderBy("created_at", false);
            for (UserInteractionsEntity s : userInteractionsService.selectList(storeEw3)) {
                if (isSelfActionOnOwnContent(s, userId)) {
                    continue;
                }
                userInteractionsList.add(buildUserInteractionsItem(s, "user-interactions"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw4 = new EntityWrapper<>();
            storeEw4.eq("interaction_type", "1").in("resource_id", myWaiguoIds);
            storeEw4.orderBy("created_at", false);
            for (UserInteractionsEntity s : userInteractionsService.selectList(storeEw4)) {
                if (isSelfActionOnOwnContent(s, userId)) {
                    continue;
                }
                userInteractionsList.add(buildUserInteractionsItem(s, "user-interactions"));
            }
        }
        if (!myForumPostIdsForLike.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEwForumPost2 = new EntityWrapper<>();
            storeEwForumPost2.eq("interaction_type", "1").in("resource_id", myForumPostIdsForLike);
            storeEwForumPost2.orderBy("created_at", false);
            for (UserInteractionsEntity s : userInteractionsService.selectList(storeEwForumPost2)) {
                if (isSelfActionOnOwnContent(s, userId)) {
                    continue;
                }
                userInteractionsList.add(buildUserInteractionsItem(s, "user-interactions"));
            }
        }
        userInteractionsList.sort(this::compareNotifyTimeDesc);
        result.put("userInteractionsList", userInteractionsList);

        // 4. ??????????
        List<Map<String, Object>> followMeList = new ArrayList<>();
        EntityWrapper<UserFollowEntity> guanzhuEw = new EntityWrapper<>();
        guanzhuEw.eq("followed_id", userId).orderBy("created_at", false);
        List<UserFollowEntity> guanzhuList = userFollowService.selectList(guanzhuEw);
        for (UserFollowEntity g : guanzhuList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("createdAt", g.getCreatedAt());
            item.put("followerId", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "??");
            item.put("operatorAvatar", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
            followMeList.add(item);
        }
        followMeList.sort(this::compareNotifyTimeDesc);
        result.put("followMeList", followMeList);

        return result;
    }

    /** 消息时间倒序：最新的在前（不能使用 Date#toString 做字符串比较，否则顺序错乱） */
    private int compareNotifyTimeDesc(Map<String, Object> a, Map<String, Object> b) {
        long ta = toEpochMillis(a != null ? a.get("createdAt") : null);
        long tb = toEpochMillis(b != null ? b.get("createdAt") : null);
        return Long.compare(tb, ta);
    }

    private static long toEpochMillis(Object timeVal) {
        if (timeVal == null) {
            return 0L;
        }
        if (timeVal instanceof Date) {
            return ((Date) timeVal).getTime();
        }
        if (timeVal instanceof Number) {
            long n = ((Number) timeVal).longValue();
            return n > 1_000_000_000_000L ? n : n * 1000L;
        }
        String s = timeVal.toString().trim();
        if (s.isEmpty()) {
            return 0L;
        }
        if (s.matches("\\d+")) {
            long n = Long.parseLong(s);
            return n > 1_000_000_000_000L ? n : n * 1000L;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            return sdf.parse(s).getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }

    private Map<String, Object> buildUserInteractionsItem(UserInteractionsEntity s, String type) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", s.getId());
        item.put("resourceId", s.getResourceId());
        fillUserInteractionsTargetInfo(item, s.getResourceId());
        item.put("createdAt", s.getCreatedAt());
        item.put("type", type);
        Long uid = s.getUserId();
        if (uid != null) {
            UserEntity u = userService.selectById(uid);
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "??");
            item.put("operatorAvatar", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
        } else {
            item.put("operatorName", "??");
            item.put("operatorAvatar", "");
        }
        return item;
    }

    private void fillUserInteractionsTargetInfo(Map<String, Object> item, Long refId) {
        if (refId == null) {
            return;
        }
        ForumPostEntity post = forumPostService.selectById(refId);
        if (post != null) {
            item.put("tablename", "forum_post");
            item.put("name", post.getTitle());
            item.put("picture", post.getPicture());
            return;
        }
        ChineseRecipeEntity recipeRow = chinese_recipeService.selectById(refId);
        if (recipeRow != null) {
            String st = recipeRow.getSourceType();
            if ("foreign_recipe".equals(st)) {
                item.put("tablename", "foreign_recipe");
            } else {
                item.put("tablename", "chinese_recipe");
            }
            item.put("name", recipeRow.getCaipinmingcheng());
            item.put("picture", recipeRow.getTupian());
        }
    }

    /** 内容作者本人对自己作品的赞/藏不写入消息列表 */
    private static boolean isSelfActionOnOwnContent(UserInteractionsEntity s, Long contentOwnerUserId) {
        if (contentOwnerUserId == null) {
            return false;
        }
        Long actor = s.getUserId();
        return actor != null && actor.equals(contentOwnerUserId);
    }

    private Long resolveCurrentUserId(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj != null) {
            return Long.valueOf(uidObj.toString());
        }
        String token = request.getHeader("Token");
        if (token != null && !token.trim().isEmpty()) {
            TokenEntity te = tokenService.getTokenEntity(token.trim());
            if (te != null && te.getUserId() != null) {
                return te.getUserId();
            }
        }
        return null;
    }

    /**
     * ????????????? JSON ?? / ?? / ?????????????????
     */
    private void addReplyItemsFromJson(List<Map<String, Object>> replyList,
                                       String replyStr,
                                       Long commentId,
                                       Long refId,
                                       Object addtime,
                                       String messageContent,
                                       String picture,
                                       String defaultUsername,
                                       String defaultTouxiang,
                                       String selfName,
                                       String selfTouxiang) {
        if (replyStr == null || replyStr.trim().isEmpty()) {
            return;
        }
        String s = replyStr.trim();

        try {
            // ???????????????
            if (s.startsWith("[")) {
                JSONArray arr = JSONArray.parseArray(s);
                for (int i = 0; i < arr.size(); i++) {
                    Object obj = arr.get(i);
                    if (obj instanceof JSONObject) {
                        JSONObject o = (JSONObject) obj;
                        String content = o.getString("content");
                        String nickname = o.getString("nickname");
                        String touxiang = o.getString("touxiang");
                        // ?????????
                        if (isSelfReply(nickname, touxiang, selfName, selfTouxiang)) {
                            continue;
                        }
                        String useTouxiang = (touxiang != null && !touxiang.isEmpty()) ? touxiang : defaultTouxiang;
                        addSingleReplyItem(replyList, commentId, refId, addtime, messageContent, picture,
                                nickname, useTouxiang, content);
                    } else {
                        String content = String.valueOf(obj);
                        addSingleReplyItem(replyList, commentId, refId, addtime, messageContent, picture,
                                null, defaultTouxiang, content);
                    }
                }
                return;
            }

            // ????
            if (s.startsWith("{")) {
                JSONObject o = JSONObject.parseObject(s);
                String content = o.getString("content");
                String nickname = o.getString("nickname");
                String touxiang = o.getString("touxiang");
                if (isSelfReply(nickname, touxiang, selfName, selfTouxiang)) {
                    return;
                }
                String useTouxiang = (touxiang != null && !touxiang.isEmpty()) ? touxiang : defaultTouxiang;
                addSingleReplyItem(replyList, commentId, refId, addtime, messageContent, picture,
                        nickname, useTouxiang, content != null ? content : s);
                return;
            }
        } catch (Exception e) {
            // ????????????
        }

        // ? JSON ??????????????
        // ????????????????????????????????????
        addSingleReplyItem(replyList, commentId, refId, addtime, messageContent, picture,
                null, defaultTouxiang, s);
    }

    /**
     * ??????????????
     */
    private void addSingleReplyItem(List<Map<String, Object>> replyList,
                                    Long commentId,
                                    Long refId,
                                    Object addtime,
                                    String messageContent,
                                    String picture,
                                    String username,
                                    String touxiang,
                                    String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        Map<String, Object> item = new HashMap<>();
        item.put("id", commentId);
        item.put("resourceId", refId);
        item.put("createdAt", addtime);
        item.put("messageContent", messageContent);
        item.put("content", content);
        if (picture != null) {
            item.put("picture", picture);
        }
        item.put("username", username != null && !username.isEmpty() ? username : "??");
        item.put("avatar", touxiang != null ? touxiang : "");
        replyList.add(item);
    }

    /**
     * ?????????????????
     */
    private boolean isSelfReply(String nickname, String touxiang, String selfName, String selfTouxiang) {
        if (selfName != null && nickname != null && selfName.equals(nickname)) {
            return true;
        }
        if (selfTouxiang != null && touxiang != null && selfTouxiang.equals(touxiang)) {
            return true;
        }
        return false;
    }
}

