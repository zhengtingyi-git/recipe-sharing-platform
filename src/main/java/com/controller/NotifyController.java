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
import com.entity.GuanzhuEntity;
import com.entity.StoreupEntity;
import com.entity.WaiguomeishiEntity;
import com.entity.UserEntity;
import com.entity.ZhongshimeishiEntity;
import com.entity.NewsEntity;
import com.entity.DiscussnewsEntity;
import com.entity.DiscusszhongshimeishiEntity;
import com.entity.DiscusswaiguomeishiEntity;
import com.service.GuanzhuService;
import com.service.StoreupService;
import com.service.UserService;
import com.service.ZhongshimeishiService;
import com.service.WaiguomeishiService;
import com.service.NewsService;
import com.service.DiscussnewsService;
import com.service.DiscusszhongshimeishiService;
import com.service.DiscusswaiguomeishiService;
import com.utils.R;

/**
 * 前台消息中心：被点赞、被收藏、被回复的消息
 */
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Autowired
    private GuanzhuService guanzhuService;
    @Autowired
    private StoreupService storeupService;
    @Autowired
    private UserService userService;
    @Autowired
    private ZhongshimeishiService zhongshimeishiService;
    @Autowired
    private WaiguomeishiService waiguomeishiService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private DiscussnewsService discussnewsService;
    @Autowired
    private DiscusszhongshimeishiService discusszhongshimeishiService;
    @Autowired
    private DiscusswaiguomeishiService discusswaiguomeishiService;

    /**
     * 消息中心：返回 回复我的、点赞我的、收藏我的 列表（需登录）
     */
    @RequestMapping("/center")
    public R center(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long userId = Long.valueOf(uidObj.toString());
        UserEntity currentUser = userService.selectById(userId);
        String selfName = currentUser != null ? currentUser.getYonghuxingming() : null;
        String selfTouxiang = currentUser != null ? currentUser.getTouxiang() : null;

        Map<String, Object> result = new HashMap<>();

        // 1. 回复我的：别人评论了我发布的美食论坛帖子 / 我发布的菜品
        List<Map<String, Object>> replyList = new ArrayList<>();

        // 1.1 美食论坛帖子评论（别人给我发的帖子/论坛留言评论）
        EntityWrapper<NewsEntity> newsEw = new EntityWrapper<>();
        newsEw.eq("userid", userId);
        List<NewsEntity> myNews = newsService.selectList(newsEw);
        if (!myNews.isEmpty()) {
            List<Long> myNewsIds = myNews.stream().map(NewsEntity::getId).collect(Collectors.toList());
            EntityWrapper<DiscussnewsEntity> discussEw = new EntityWrapper<>();
            discussEw.in("refid", myNewsIds);
            discussEw.orderBy("addtime", false);
            List<DiscussnewsEntity> comments = discussnewsService.selectList(discussEw);
            for (DiscussnewsEntity c : comments) {
                // 排除我自己给自己帖子发的评论，避免“回复我的”里出现自己的评论
                if (c.getUserid() != null && c.getUserid().longValue() == userId.longValue()) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("id", c.getId());
                item.put("refid", c.getRefid());
                item.put("addtime", c.getAddtime());
                item.put("content", c.getContent());
                NewsEntity post = newsService.selectById(c.getRefid());
                if (post != null) {
                    item.put("messageContent", post.getTitle());
                    item.put("picture", post.getPicture());
                }
                UserEntity commenter = c.getUserid() != null ? userService.selectById(c.getUserid()) : null;
                item.put("username", commenter != null && commenter.getYonghuxingming() != null
                        ? commenter.getYonghuxingming()
                        : (c.getNickname() != null ? c.getNickname() : "用户"));
                item.put("touxiang", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.2 我发布的中式美食被评论
        EntityWrapper<ZhongshimeishiEntity> zhongshiEw = new EntityWrapper<>();
        zhongshiEw.eq("userid", userId);
        List<ZhongshimeishiEntity> myZhongshi = zhongshimeishiService.selectList(zhongshiEw);
        if (!myZhongshi.isEmpty()) {
            List<Long> myZhongshiIds = myZhongshi.stream().map(ZhongshimeishiEntity::getId).collect(Collectors.toList());
            EntityWrapper<DiscusszhongshimeishiEntity> discussZhongshiEw = new EntityWrapper<>();
            discussZhongshiEw.in("refid", myZhongshiIds);
            discussZhongshiEw.orderBy("addtime", false);
            List<DiscusszhongshimeishiEntity> commentsZhongshi = discusszhongshimeishiService.selectList(discussZhongshiEw);
            for (DiscusszhongshimeishiEntity c : commentsZhongshi) {
                // 排除我自己给自己菜品发的评论
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
                        : (c.getNickname() != null ? c.getNickname() : "用户"));
                item.put("touxiang", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.3 我发布的外国美食被评论
        EntityWrapper<WaiguomeishiEntity> waiguoEw = new EntityWrapper<>();
        waiguoEw.eq("userid", userId);
        List<WaiguomeishiEntity> myWaiguo = waiguomeishiService.selectList(waiguoEw);
        if (!myWaiguo.isEmpty()) {
            List<Long> myWaiguoIdsForReply = myWaiguo.stream().map(WaiguomeishiEntity::getId).collect(Collectors.toList());
            EntityWrapper<DiscusswaiguomeishiEntity> discussWaiguoEw = new EntityWrapper<>();
            discussWaiguoEw.in("refid", myWaiguoIdsForReply);
            discussWaiguoEw.orderBy("addtime", false);
            List<DiscusswaiguomeishiEntity> commentsWaiguo = discusswaiguomeishiService.selectList(discussWaiguoEw);
            for (DiscusswaiguomeishiEntity c : commentsWaiguo) {
                // 排除我自己给自己菜品发的评论
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
                        : (c.getNickname() != null ? c.getNickname() : "用户"));
                item.put("touxiang", commenter != null && commenter.getTouxiang() != null ? commenter.getTouxiang() : "");
                replyList.add(item);
            }
        }

        // 1.4 我的评论被回复（帖子、菜品详情里我发的评论被作者或管理员回复）
        // 1.4.1 我的美食论坛评论被回复
        EntityWrapper<DiscussnewsEntity> myDiscussNewsEw = new EntityWrapper<>();
        myDiscussNewsEw.eq("userid", userId)
                .isNotNull("reply")
                .ne("reply", "");
        myDiscussNewsEw.orderBy("addtime", false);
        List<DiscussnewsEntity> myDiscussNews = discussnewsService.selectList(myDiscussNewsEw);
        for (DiscussnewsEntity c : myDiscussNews) {
            String replyStr = c.getReply();
            if (replyStr == null || replyStr.trim().isEmpty()) {
                continue;
            }
            NewsEntity post = newsService.selectById(c.getRefid());
            String picture = post != null ? post.getPicture() : null;
            String defaultName = "用户";
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

        // 1.4.2 我的中式美食评论被回复
        EntityWrapper<DiscusszhongshimeishiEntity> myDiscussZhongshiEw = new EntityWrapper<>();
        myDiscussZhongshiEw.eq("userid", userId)
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
            String defaultName = "用户";
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

        // 1.4.3 我的外国美食评论被回复
        EntityWrapper<DiscusswaiguomeishiEntity> myDiscussWaiguoEw = new EntityWrapper<>();
        myDiscussWaiguoEw.eq("userid", userId)
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
            String defaultName = "用户";
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

        // 按时间倒序排序“回复我的”（包含：别人评论我发布的内容 + 别人回复我发表的评论）
        replyList.sort((a, b) -> {
            String t1 = a.get("addtime") != null ? a.get("addtime").toString() : "";
            String t2 = b.get("addtime") != null ? b.get("addtime").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("replyList", replyList);

        // 2. 点赞我的：我的美食 / 帖子 被点赞（type=21）
        EntityWrapper<ZhongshimeishiEntity> zew = new EntityWrapper<>();
        zew.eq("userid", userId);
        List<Long> myZhongshiIds = zhongshimeishiService.selectList(zew).stream()
                .map(ZhongshimeishiEntity::getId).collect(Collectors.toList());
        EntityWrapper<WaiguomeishiEntity> wew = new EntityWrapper<>();
        wew.eq("userid", userId);
        List<Long> myWaiguoIds = waiguomeishiService.selectList(wew).stream()
                .map(WaiguomeishiEntity::getId).collect(Collectors.toList());

        EntityWrapper<NewsEntity> newsThumbEw = new EntityWrapper<>();
        newsThumbEw.eq("userid", userId);
        List<Long> myNewsIdsForLike = newsService.selectList(newsThumbEw).stream()
                .map(NewsEntity::getId).collect(Collectors.toList());

        List<Map<String, Object>> thumbsupList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw = new EntityWrapper<>();
            storeEw.eq("type", "21").eq("tablename", "zhongshimeishi").in("refid", myZhongshiIds);
            storeEw.orderBy("addtime", false);
            List<StoreupEntity> thumbs = storeupService.selectList(storeEw);
            for (StoreupEntity s : thumbs) {
                thumbsupList.add(buildStoreupItem(s, "thumbsup"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw2 = new EntityWrapper<>();
            storeEw2.eq("type", "21").eq("tablename", "waiguomeishi").in("refid", myWaiguoIds);
            storeEw2.orderBy("addtime", false);
            List<StoreupEntity> thumbs = storeupService.selectList(storeEw2);
            for (StoreupEntity s : thumbs) {
                thumbsupList.add(buildStoreupItem(s, "thumbsup"));
            }
        }
        if (!myNewsIdsForLike.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEwNews = new EntityWrapper<>();
            storeEwNews.eq("type", "21").eq("tablename", "news").in("refid", myNewsIdsForLike);
            storeEwNews.orderBy("addtime", false);
            List<StoreupEntity> thumbsNews = storeupService.selectList(storeEwNews);
            for (StoreupEntity s : thumbsNews) {
                thumbsupList.add(buildStoreupItem(s, "thumbsup"));
            }
        }
        thumbsupList.sort((a, b) -> {
            String t1 = a.get("addtime") != null ? a.get("addtime").toString() : "";
            String t2 = b.get("addtime") != null ? b.get("addtime").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("thumbsupList", thumbsupList);

        // 3. 收藏我的：我的美食 / 帖子 被收藏（type=1）
        List<Map<String, Object>> storeupList = new ArrayList<>();
        if (!myZhongshiIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw3 = new EntityWrapper<>();
            storeEw3.eq("type", "1").eq("tablename", "zhongshimeishi").in("refid", myZhongshiIds);
            storeEw3.orderBy("addtime", false);
            for (StoreupEntity s : storeupService.selectList(storeEw3)) {
                storeupList.add(buildStoreupItem(s, "storeup"));
            }
        }
        if (!myWaiguoIds.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEw4 = new EntityWrapper<>();
            storeEw4.eq("type", "1").eq("tablename", "waiguomeishi").in("refid", myWaiguoIds);
            storeEw4.orderBy("addtime", false);
            for (StoreupEntity s : storeupService.selectList(storeEw4)) {
                storeupList.add(buildStoreupItem(s, "storeup"));
            }
        }
        if (!myNewsIdsForLike.isEmpty()) {
            EntityWrapper<StoreupEntity> storeEwNews2 = new EntityWrapper<>();
            storeEwNews2.eq("type", "1").eq("tablename", "news").in("refid", myNewsIdsForLike);
            storeEwNews2.orderBy("addtime", false);
            for (StoreupEntity s : storeupService.selectList(storeEwNews2)) {
                storeupList.add(buildStoreupItem(s, "storeup"));
            }
        }
        storeupList.sort((a, b) -> {
            String t1 = a.get("addtime") != null ? a.get("addtime").toString() : "";
            String t2 = b.get("addtime") != null ? b.get("addtime").toString() : "";
            return t2.compareTo(t1);
        });
        result.put("storeupList", storeupList);

        // 4. 关注我的：谁关注了我
        List<Map<String, Object>> followMeList = new ArrayList<>();
        EntityWrapper<GuanzhuEntity> guanzhuEw = new EntityWrapper<>();
        guanzhuEw.eq("followed_id", userId).orderBy("addtime", false);
        List<GuanzhuEntity> guanzhuList = guanzhuService.selectList(guanzhuEw);
        for (GuanzhuEntity g : guanzhuList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getAddtime());
            item.put("followerId", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
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
        item.put("tablename", s.getTablename());
        item.put("name", s.getName());
        item.put("picture", s.getPicture());
        item.put("addtime", s.getAddtime());
        item.put("type", type);
        Long uid = s.getUserid();
        if (uid != null) {
            UserEntity u = userService.selectById(uid);
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
            item.put("operatorTouxiang", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
        } else {
            item.put("operatorName", "用户");
            item.put("operatorTouxiang", "");
        }
        return item;
    }

    /**
     * 将评论中的回复字段（可能是 JSON 数组 / 对象 / 纯文本）拆分成多条“回复我的”消息
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
            // 数组：每个元素是一条独立的回复
            if (s.startsWith("[")) {
                JSONArray arr = JSONArray.parseArray(s);
                for (int i = 0; i < arr.size(); i++) {
                    Object obj = arr.get(i);
                    if (obj instanceof JSONObject) {
                        JSONObject o = (JSONObject) obj;
                        String content = o.getString("content");
                        String nickname = o.getString("nickname");
                        String touxiang = o.getString("touxiang");
                        // 自己回复自己：跳过
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

            // 单个对象
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
            // 解析失败则退回纯文本逻辑
        }

        // 非 JSON 或解析失败：当作一条普通回复
        // 如果没有昵称头像信息，无法精确判断是否自己回复自己，这里不过滤，直接展示
        addSingleReplyItem(replyList, commentId, refId, addtime, messageContent, picture,
                null, defaultTouxiang, s);
    }

    /**
     * 实际创建一条“回复我的”消息
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
        item.put("username", username != null && !username.isEmpty() ? username : "用户");
        item.put("touxiang", touxiang != null ? touxiang : "");
        replyList.add(item);
    }

    /**
     * 判断一条回复是否是“自己回复自己”
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
