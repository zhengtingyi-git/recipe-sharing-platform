package com.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.annotation.IgnoreAuth;

import com.entity.ForumPostCommentEntity;
import com.entity.view.ForumPostCommentView;
import com.entity.UserEntity;
import com.service.UserService;
import com.service.ForumPostCommentService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

@RestController
@RequestMapping({"/forum-post-comment", "/forum_post_comment"})
public class ForumPostCommentController {
    @Autowired
    private ForumPostCommentService forumPostCommentService;
    @Autowired
    private UserService userService;

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ForumPostCommentEntity forumPostComment, HttpServletRequest request){
        EntityWrapper<ForumPostCommentEntity> ew = new EntityWrapper<ForumPostCommentEntity>();
        PageUtils page = forumPostCommentService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forumPostComment), params), params));
        return R.ok().put("data", page);
    }

    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ForumPostCommentEntity forumPostComment, HttpServletRequest request){
        EntityWrapper<ForumPostCommentEntity> ew = new EntityWrapper<ForumPostCommentEntity>();
        Object postIdParam = params.get("postId");
        if (postIdParam == null) {
            postIdParam = params.get("refid");
        }
        if (postIdParam != null && StringUtils.isNotBlank(postIdParam.toString())) {
            ew.eq("post_id", postIdParam);
        }
        // 注意：不要把 forumPostComment 直接传给 likeOrEq。
        // 它包含 legacy alias（如 refid），会生成数据库不存在的 where 条件，导致 SQL 报错。
        PageUtils page = forumPostCommentService.queryPage(params, MPUtil.sort(MPUtil.between(ew, params), params));
        List<ForumPostCommentEntity> records = (List<ForumPostCommentEntity>) page.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ForumPostCommentEntity e : records) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("postId", e.getPostId());
            m.put("userId", e.getUserId());
            m.put("nickname", e.getNickname());
            // displayName 统一由 user_id 查询获得，不依赖冗余列
            m.put("displayName", e.getDisplayName());
            m.put("cpicture", e.getCpicture());
            m.put("commentImage", e.getCommentImage());
            m.put("content", e.getContent());
            m.put("commentContent", e.getCommentContent());
            m.put("reply", e.getReply());
            m.put("replyContent", e.getReplyContent());
            m.put("createdAt", e.getCreatedAt());
            m.put("touxiang", "");
            if (e.getUserId() != null) {
                UserEntity y = userService.selectById(e.getUserId());
                if (y != null) {
                    if (y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                        m.put("nickname", y.getYonghuxingming());
                        m.put("displayName", y.getYonghuxingming());
                    }
                    if (y.getTouxiang() != null) {
                        m.put("touxiang", y.getTouxiang());
                    }
                }
            }
            list.add(m);
        }
        page.setList(list);
        return R.ok().put("data", page);
    }

    @RequestMapping("/lists")
    public R list(ForumPostCommentEntity forumPostComment){
        EntityWrapper<ForumPostCommentEntity> ew = new EntityWrapper<ForumPostCommentEntity>();
        ew.allEq(MPUtil.allEQMapPre(forumPostComment, "forum_post_comment"));
        return R.ok().put("data", forumPostCommentService.selectListView(ew));
    }

    @RequestMapping("/query")
    public R query(ForumPostCommentEntity forumPostComment){
        EntityWrapper<ForumPostCommentEntity> ew = new EntityWrapper<ForumPostCommentEntity>();
        ew.allEq(MPUtil.allEQMapPre(forumPostComment, "forum_post_comment"));
        ForumPostCommentView forumPostCommentView = forumPostCommentService.selectView(ew);
        return R.ok("查询成功").put("data", forumPostCommentView);
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ForumPostCommentEntity forumPostComment = forumPostCommentService.selectById(id);
        return R.ok().put("data", forumPostComment);
    }

    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ForumPostCommentEntity forumPostComment = forumPostCommentService.selectById(id);
        return R.ok().put("data", forumPostComment);
    }

    @RequestMapping("/save")
    public R save(@RequestBody ForumPostCommentEntity forumPostComment, HttpServletRequest request){
        forumPostComment.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        if (forumPostComment.getUserId() != null) {
            UserEntity y = userService.selectById(forumPostComment.getUserId());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                forumPostComment.setNickname(y.getYonghuxingming());
            }
        }
        forumPostCommentService.insert(forumPostComment);
        return R.ok();
    }

    @RequestMapping("/add")
    public R add(@RequestBody ForumPostCommentEntity forumPostComment, HttpServletRequest request){
        forumPostComment.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        if (forumPostComment.getUserId() != null) {
            UserEntity y = userService.selectById(forumPostComment.getUserId());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                forumPostComment.setNickname(y.getYonghuxingming());
            }
        }
        forumPostCommentService.insert(forumPostComment);
        return R.ok();
    }

    @RequestMapping("/update")
    public R update(@RequestBody ForumPostCommentEntity forumPostComment, HttpServletRequest request){
        forumPostCommentService.updateById(forumPostComment);
        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        Object userIdObj = request.getSession().getAttribute("userId");
        Object roleObj = request.getSession().getAttribute("role");
        Long userId = null;
        String role = null;
        if (userIdObj instanceof Long) userId = (Long) userIdObj;
        else if (userIdObj instanceof Integer) userId = ((Integer) userIdObj).longValue();
        if (roleObj instanceof String) role = (String) roleObj;

        if ("管理员".equals(role)) {
            forumPostCommentService.deleteBatchIds(Arrays.asList(ids));
            return R.ok();
        }
        if (userId == null) {
            return R.error(401, "请先登录");
        }

        List<Long> canDeleteIds = new ArrayList<>();
        for (Long id : ids) {
            ForumPostCommentEntity entity = forumPostCommentService.selectById(id);
            if (entity != null && entity.getUserId() != null && entity.getUserId().longValue() == userId.longValue()) {
                canDeleteIds.add(id);
            }
        }
        if (canDeleteIds.isEmpty()) {
            return R.error("没有权限删除这些评论");
        }
        forumPostCommentService.deleteBatchIds(canDeleteIds);
        return R.ok();
    }
}

