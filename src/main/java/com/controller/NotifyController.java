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
import com.entity.StoreupEntity;
import com.entity.WaiguomeishiEntity;
import com.entity.UserEntity;
import com.entity.ZhongshimeishiEntity;
import com.entity.ForumPostEntity;
import com.entity.ForumPostCommentEntity;
import com.entity.DiscusszhongshimeishiEntity;
import com.entity.DiscusswaiguomeishiEntity;
import com.service.UserFollowService;
import com.service.StoreupService;
import com.service.UserService;
import com.service.ZhongshimeishiService;
import com.service.WaiguomeishiService;
import com.service.ForumPostService;
import com.service.ForumPostCommentService;
import com.service.DiscusszhongshimeishiService;
import com.service.DiscusswaiguomeishiService;
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
    private StoreupService storeupService;
    @Autowired
    private UserService userService;
    @Autowired
    private ZhongshimeishiService zhongshimeishiService;
    @Autowired
    private WaiguomeishiService waiguomeishiService;
    @Autowired
    private ForumPostService forumPostService;
    @Autowired
    private ForumPostCommentService forumPostCommentService;
    @Autowired
    private DiscusszhongshimeishiService discusszhongshimeishiService;
    @Autowired
    private DiscusswaiguomeishiService discusswaiguomeishiService;

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
        forumPostEw.eq("userid", userId);
        List<ForumPostEntity> myForumPosts = forumPostService.selectList(forumPostEw);
        if (!myForumPosts.isEmpty()) {
            List<Long> myForumPostIds = myForumPosts.stream().map(ForumPostEntity::getId).collect(Collectors.toList());
            EntityWrapper<ForumPostCommentEntity> discussEw = new EntityWrapper<>();
            discussEw.in("post_id", myForumPostIds);
            discussEw.orderBy("created_at", false);
            List<ForumPostCommentEntity> comments = forumPostCommentService.selectList(discussEw);
            for (ForumPostCommentEntity c : comments) {
                // ???????????????????????????????
                if (c.getUserid() != null && c.getUserid().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("refid", c.getRefid());
                item.put("addtime", c.getAddtime());
                item.put("content", c.getContent());
                ForumPostEntity post = forumPostService.selectById(c.getRefid());
                if (post != null) {
                    item.put("messageContent", post.getTitle());
                    item.put("picture", post.getPicture());
                }
                UserEntity commenter = c.getUserid() != null ? userService.selectById(c.getUserid()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "??"));
                item.put("touxiang", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.2 ???????????
        EntityWrapper<ZhongshimeishiEntity> zhongshiEw = new EntityWrapper<>();
        zhongshiEw.eq("userid", userId);
        zhongshiEw.eq("recipetype", "zhongshimeishi");
        List<ZhongshimeishiEntity> myZhongshi = zhongshimeishiService.selectList(zhongshiEw);
        if (!myZhongshi.isEmpty()) {
            List<Long> myZhongshiIds = myZhongshi.stream().map(ZhongshimeishiEntity::getId).collect(Collectors.toList());
            EntityWrapper<DiscusszhongshimeishiEntity> discussZhongshiEw = new EntityWrapper<>();
            discussZhongshiEw.in("refid", myZhongshiIds);
            discussZhongshiEw.eq("recipetype", "zhongshimeishi");
            discussZhongshiEw.orderBy("addtime", false);
            List<DiscusszhongshimeishiEntity> commentsZhongshi = discusszhongshimeishiService.selectList(discussZhongshiEw);
            for (DiscusszhongshimeishiEntity c : commentsZhongshi) {
                // ??????????????
                if (c.getUserid() != null && c.getUserid().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("refid", c.getRefid());
                item.put("addtime", c.getAddtime());
                item.put("content", c.getContent());
                ZhongshimeishiEntity dish = zhongshimeishiService.selectById(c.getRefid());
                if (dish != null) {
                    item.put("messageContent", dish.getCaipinmingcheng());
                    item.put("picture", dish.getTupian());
                }
                UserEntity commenter = c.getUserid() != null ? userService.selectById(c.getUserid()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "??"));
                item.put("touxiang", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.3 ???????????
        EntityWrapper<WaiguomeishiEntity> waiguoEw = new EntityWrapper<>();
        waiguoEw.eq("userid", userId);
        waiguoEw.eq("recipetype", "waiguomeishi");
        List<WaiguomeishiEntity> myWaiguo = waiguomeishiService.selectList(waiguoEw);
        if (!myWaiguo.isEmpty()) {
            List<Long> myWaiguoIdsForReply = myWaiguo.stream().map(WaiguomeishiEntity::getId).collect(Collectors.toList());
            EntityWrapper<DiscusswaiguomeishiEntity> discussWaiguoEw = new EntityWrapper<>();
            discussWaiguoEw.in("refid", myWaiguoIdsForReply);
            discussWaiguoEw.eq("recipetype", "waiguomeishi");
            discussWaiguoEw.orderBy("addtime", false);
            List<DiscusswaiguomeishiEntity> commentsWaiguo = discusswaiguomeishiService.selectList(discussWaiguoEw);
            for (DiscusswaiguomeishiEntity c : commentsWaiguo) {
                // ??????????????
                if (c.getUserid() != null && c.getUserid().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("refid", c.getRefid());
                item.put("addtime", c.getAddtime());
                item.put("content", c.getContent());
                WaiguomeishiEntity dish = waiguomeishiService.selectById(c.getRefid());
                if (dish != null) {
                    item.put("messageContent", dish.getCaipinmingcheng());
                    item.put("picture", dish.getTupian());
                }
                UserEntity commenter = c.getUserid() != null ? userService.selectById(c.getUserid()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "??"));
                item.put("touxiang", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
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
            ForumPostEntity post = forumPostService.selectById(c.getRefid());
            String picture = post != null ? post.getPicture() : null;
            String defaultName = "??";
            String defaultTouxiang = "";
            if (post != null && post.getUserid() != null) {
                UserEntity author = userService.selectById(post.getUserid());
                if (author != null) {
                    if (author.getYonghuxingming() != null) {
                        defaultName = author.getYonghuxingming();
                    }
                    if (author.getTouxiang() != null) {
                        defaultTouxiang = author.getTouxiang();
                    }
                }
            }
            addReplyItemsFromJson(replyList, replyStr, c.getId(), c.getRefid(), c.getAddtime(),
                    c.getContent(), picture, defaultName, defaultTouxiang, selfName, selfTouxiang);
        }

        // 1.4.2 ???????????
        EntityWrapper<DiscusszhongshimeishiEntity> myDiscussZhongshiEw = new EntityWrapper<>();
        myDiscussZhongshiEw.eq("userid", userId)
                .eq("recipetype", "zhongshimeishi")
                .isNotNull("reply")
                .ne("reply", "");
        myDiscussZhongshiEw.orderBy("addtime", false);
        List<DiscusszhongshimeishiEntity> myDiscussZhongshi = discusszhongshimeishiService.selectList(myDiscussZhongshiEw);
        for (DiscusszhongshimeishiEntity c : myDiscussZhongshi) {
            String replyStr = c.getReply();
            if (replyStr == null || replyStr.trim().isEmpty()) {
                continue;
            }
            ZhongshimeishiEntity dish = zhongshimeishiService.selectById(c.getRefid());
            String picture = dish != null ? dish.getTupian() : null;
            String defaultName = "??";
            String defaultTouxiang = "";
            if (dish != null && dish.getUserid() != null) {
                UserEntity author = userService.selectById(dish.getUserid());
                if (author != null) {
                    if (author.getYonghuxingming() != null) {
                        defaultName = author.getYonghuxingming();
                    }
                    if (author.getTouxiang() != null) {
                        defaultTouxiang = author.getTouxiang();
                    }
                }
            }
            addReplyItemsFromJson(replyList, replyStr, c.getId(), c.getRefid(), c.getAddtime(),
                    c.getContent(), picture, defaultName, defaultTouxiang, selfName, selfTouxiang);
        }

        // 1.4.3 ???????????
        EntityWrapper<DiscusswaiguomeishiEntity> myDiscussWaiguoEw = new EntityWrapper<>();
        myDiscussWaiguoEw.eq("userid", userId)
                .eq("recipetype", "waiguomeishi")
                .isNotNull("reply")
                .ne("reply", "");
        myDiscussWaiguoEw.orderBy("addtime", false);
        List<DiscusswaiguomeishiEntity> myDiscussWaiguo = discusswaiguomeishiService.selectList(myDiscussWaiguoEw);
        for (DiscusswaiguomeishiEntity c : myDiscussWaiguo) {
            String replyStr = c.getReply();
            if (replyStr == null || replyStr.trim().isEmpty()) {
                continue;
            }
            WaiguomeishiEntity dish = waiguomeishiService.selectById(c.getRefid());
            String picture = dish != null ? dish.getTupian() : null;
            String defaultName = "??";
            String defaultTouxiang = "";
            if (dish != null && dish.getUserid() != null) {
                UserEntity author = userService.selectById(dish.getUserid());
                if (author != null) {
                    if (author.getYonghuxingming() != null) {
                        defaultName = author.getYonghuxingming();
                    }
                    if (author.getTouxiang() != null) {
                        defaultTouxiang = author.getTouxiang();
                    }
                }
            }
            addReplyItemsFromJson(replyList, replyStr, c.getId(), c.getRefid(), c.getAddtime(),
                    c.getContent(), picture, defaultName, defaultTouxiang, selfName, selfTouxiang);
        }

        // ??????????????????????????? + ???????????
        replyList.sort((a, b) -> {
            String t1 = a.get("addtime") != null ? a.get("addtime").toString() : "";
            String t2 = b.get("addtime") != null ? b.get("addtime").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("replyList", replyList);

        // 2. ????????? / ?? ????type=21?
        EntityWrapper<ZhongshimeishiEntity> zew = new EntityWrapper<>();
        zew.eq("userid", userId);
        zew.eq("recipetype", "zhongshimeishi");
        List<Long> myZhongshiIds = zhongshimeishiService.selectList(zew).stream()
                .map(ZhongshimeishiEntity::getId).collect(Collectors.toList());
        EntityWrapper<WaiguomeishiEntity> wew = new EntityWrapper<>();
        wew.eq("userid", userId);
        wew.eq("recipetype", "waiguomeishi");
        List<Long> myWaiguoIds = waiguomeishiService.selectList(wew).stream()
                .map(WaiguomeishiEntity::getId).collect(Collectors.toList());

        EntityWrapper<ForumPostEntity> forumPostThumbEw = new EntityWrapper<>();
        forumPostThumbEw.eq("userid", userId);
        List<Long> myForumPostIdsForLike = forumPostService.selectList(forumPostThumbEw).stream()
                .map(ForumPostEntity::getId).collect(Collectors.toList());

        List<Map<String, Object>> thumbsupList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw = new EntityWrapper<>();
            storeEw.eq("type", "21").in("refid", myZhongshiIds);
            storeEw.orderBy("addtime", false);
            List<StoreupEntity> thumbs = storeupService.selectList(storeEw);
            for (StoreupEntity s : thumbs) {
                thumbsupList.add(buildStoreupItem(s, "thumbsup"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw2 = new EntityWrapper<>();
            storeEw2.eq("type", "21").in("refid", myWaiguoIds);
            storeEw2.orderBy("addtime", false);
            List<StoreupEntity> thumbs = storeupService.selectList(storeEw2);
            for (StoreupEntity s : thumbs) {
                thumbsupList.add(buildStoreupItem(s, "thumbsup"));
            }
        }
        if (!myForumPostIdsForLike.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEwForumPost = new EntityWrapper<>();
            storeEwForumPost.eq("type", "21").in("refid", myForumPostIdsForLike);
            storeEwForumPost.orderBy("addtime", false);
            List<StoreupEntity> forumPostThumbs = storeupService.selectList(storeEwForumPost);
            for (StoreupEntity s : forumPostThumbs) {
                thumbsupList.add(buildStoreupItem(s, "thumbsup"));
            }
        }
        thumbsupList.sort((a, b) -> {
            String t1 = a.get("addtime") != null ? a.get("addtime").toString() : "";
            String t2 = b.get("addtime") != null ? b.get("addtime").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("thumbsupList", thumbsupList);

        // 3. ????????? / ?? ????type=1?
        List<Map<String, Object>> storeupList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw3 = new EntityWrapper<>();
            storeEw3.eq("type", "1").in("refid", myZhongshiIds);
            storeEw3.orderBy("addtime", false);
            for (StoreupEntity s : storeupService.selectList(storeEw3)) {
                storeupList.add(buildStoreupItem(s, "storeup"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw4 = new EntityWrapper<>();
            storeEw4.eq("type", "1").in("refid", myWaiguoIds);
            storeEw4.orderBy("addtime", false);
            for (StoreupEntity s : storeupService.selectList(storeEw4)) {
                storeupList.add(buildStoreupItem(s, "storeup"));
            }
        }
        if (!myForumPostIdsForLike.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEwForumPost2 = new EntityWrapper<>();
            storeEwForumPost2.eq("type", "1").in("refid", myForumPostIdsForLike);
            storeEwForumPost2.orderBy("addtime", false);
            for (StoreupEntity s : storeupService.selectList(storeEwForumPost2)) {
                storeupList.add(buildStoreupItem(s, "storeup"));
            }
        }
        storeupList.sort((a, b) -> {
            String t1 = a.get("addtime") != null ? a.get("addtime").toString() : "";
            String t2 = b.get("addtime") != null ? b.get("addtime").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("storeupList", storeupList);

        // 4. ??????????
        List<Map<String, Object>> followMeList = new ArrayList<>();
        EntityWrapper<UserFollowEntity> guanzhuEw = new EntityWrapper<>();
        guanzhuEw.eq("followed_id", userId).orderBy("created_at", false);
        List<UserFollowEntity> guanzhuList = userFollowService.selectList(guanzhuEw);
        for (UserFollowEntity g : guanzhuList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getAddtime());
            item.put("followerId", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "??");
            item.put("operatorTouxiang", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
            followMeList.add(item);
        }
        result.put("followMeList", followMeList);

        return R.ok().put("data", result);
    }

    private Map<String, Object> buildStoreupItem(StoreupEntity s, String type) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", s.getId());
        item.put("refid", s.getRefid());
        fillStoreupTargetInfo(item, s.getRefid());
        item.put("addtime", s.getAddtime());
        item.put("type", type);
        Long uid = s.getUserid();
        if (uid != null) {
            UserEntity u = userService.selectById(uid);
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "??");
            item.put("operatorTouxiang", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
        } else {
            item.put("operatorName", "??");
            item.put("operatorTouxiang", "");
        }
        return item;
    }

    private void fillStoreupTargetInfo(Map<String, Object> item, Long refId) {
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
        ZhongshimeishiEntity zhongshi = zhongshimeishiService.selectById(refId);
        if (zhongshi != null) {
            item.put("tablename", "zhongshimeishi");
            item.put("name", zhongshi.getCaipinmingcheng());
            item.put("picture", zhongshi.getTupian());
            return;
        }
        WaiguomeishiEntity waiguo = waiguomeishiService.selectById(refId);
        if (waiguo != null) {
            item.put("tablename", "waiguomeishi");
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
        item.put("refid", refId);
        item.put("addtime", addtime);
        item.put("messageContent", messageContent);
        item.put("content", content);
        if (picture != null) {
            item.put("picture", picture);
        }
        item.put("username", username != null && !username.isEmpty() ? username : "??");
        item.put("touxiang", touxiang != null ? touxiang : "");
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
