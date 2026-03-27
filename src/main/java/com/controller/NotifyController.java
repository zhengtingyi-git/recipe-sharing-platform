package com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    /**
     * ??????? ?????????????? ???????
     */
    @RequestMapping("/center")
    public R center(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("????");
        }
        Long userId = Long.valueOf(uidObj.toString());
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

        // 1.2 ???????????
        EntityWrapper<ChineseRecipeEntity> zhongshiEw = new EntityWrapper<>();
        zhongshiEw.eq("user_id", userId);
        zhongshiEw.eq("source_type", "chinese_recipe");
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
        replyList.sort((a, b) -> {
            String t1 = a.get("createdAt") != null ? a.get("createdAt").toString() : "";
            String t2 = b.get("createdAt") != null ? b.get("createdAt").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("replyList", replyList);

        // 2. ????????? / ?? ????type=21?
        EntityWrapper<ChineseRecipeEntity> zew = new EntityWrapper<>();
        zew.eq("user_id", userId);
        zew.eq("source_type", "chinese_recipe");
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
                thumbsupList.add(buildUserInteractionsItem(s, "thumbsup"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw2 = new EntityWrapper<>();
            storeEw2.eq("interaction_type", "21").in("resource_id", myWaiguoIds);
            storeEw2.orderBy("created_at", false);
            List<UserInteractionsEntity> thumbs = userInteractionsService.selectList(storeEw2);
            for (UserInteractionsEntity s : thumbs) {
                thumbsupList.add(buildUserInteractionsItem(s, "thumbsup"));
            }
        }
        if (!myForumPostIdsForLike.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEwForumPost = new EntityWrapper<>();
            storeEwForumPost.eq("interaction_type", "21").in("resource_id", myForumPostIdsForLike);
            storeEwForumPost.orderBy("created_at", false);
            List<UserInteractionsEntity> forumPostThumbs = userInteractionsService.selectList(storeEwForumPost);
            for (UserInteractionsEntity s : forumPostThumbs) {
                thumbsupList.add(buildUserInteractionsItem(s, "thumbsup"));
            }
        }
        thumbsupList.sort((a, b) -> {
            String t1 = a.get("createdAt") != null ? a.get("createdAt").toString() : "";
            String t2 = b.get("createdAt") != null ? b.get("createdAt").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("thumbsupList", thumbsupList);

        // 3. ????????? / ?? ????type=1?
        List<Map<String, Object>> userInteractionsList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw3 = new EntityWrapper<>();
            storeEw3.eq("interaction_type", "1").in("resource_id", myZhongshiIds);
            storeEw3.orderBy("created_at", false);
            for (UserInteractionsEntity s : userInteractionsService.selectList(storeEw3)) {
                userInteractionsList.add(buildUserInteractionsItem(s, "user-interactions"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEw4 = new EntityWrapper<>();
            storeEw4.eq("interaction_type", "1").in("resource_id", myWaiguoIds);
            storeEw4.orderBy("created_at", false);
            for (UserInteractionsEntity s : userInteractionsService.selectList(storeEw4)) {
                userInteractionsList.add(buildUserInteractionsItem(s, "user-interactions"));
            }
        }
        if (!myForumPostIdsForLike.isEmpty()) {
            EntityWrapper<UserInteractionsEntity> storeEwForumPost2 = new EntityWrapper<>();
            storeEwForumPost2.eq("interaction_type", "1").in("resource_id", myForumPostIdsForLike);
            storeEwForumPost2.orderBy("created_at", false);
            for (UserInteractionsEntity s : userInteractionsService.selectList(storeEwForumPost2)) {
                userInteractionsList.add(buildUserInteractionsItem(s, "user-interactions"));
            }
        }
        userInteractionsList.sort((a, b) -> {
            String t1 = a.get("createdAt") != null ? a.get("createdAt").toString() : "";
            String t2 = b.get("createdAt") != null ? b.get("createdAt").toString() : "";
            return t2.compareTo(t1);
        });
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
        result.put("followMeList", followMeList);

        return R.ok().put("data", result);
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
        ChineseRecipeEntity zhongshi = chinese_recipeService.selectById(refId);
        if (zhongshi != null) {
            item.put("tablename", "chinese_recipe");
            item.put("name", zhongshi.getCaipinmingcheng());
            item.put("picture", zhongshi.getTupian());
            return;
        }
        ForeignRecipeEntity waiguo = foreign_recipeService.selectById(refId);
        if (waiguo != null) {
            item.put("tablename", "foreign_recipe");
            item.put("name", waiguo.getCaipinmingcheng());
            item.put("picture", waiguo.getTupian());
        }
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

