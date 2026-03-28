package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.TokenEntity;
import com.entity.UserEntity;
import com.entity.UserFollowEntity;
import com.service.TokenService;
import com.service.UserFollowService;
import com.service.UserService;
import com.utils.R;

/**
 * 用户关注：关注/取关、关注状态、粉丝数、关注我的列表
 */
@RestController
@RequestMapping({"/guanzhu", "/user-follows"})
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    /** 关注某用户（需登录） */
    @RequestMapping({"/follow", "/create"})
    public R follow(@RequestParam Long followedId, HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long followerId = Long.valueOf(uidObj.toString());
        if (followerId.equals(followedId)) {
            return R.error("不能关注自己");
        }
        EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
        ew.eq("follower_id", followerId).eq("followed_id", followedId);
        if (userFollowService.selectCount(ew) > 0) {
            return R.ok("已经关注过了");
        }
        UserFollowEntity g = new UserFollowEntity();
        g.setFollowerId(followerId);
        g.setFollowedId(followedId);
        userFollowService.insert(g);
        return R.ok("关注成功");
    }

    /** 取消关注（需登录） */
    @RequestMapping({"/unfollow", "/delete"})
    public R unfollow(@RequestParam Long followedId, HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long followerId = Long.valueOf(uidObj.toString());
        EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
        ew.eq("follower_id", followerId).eq("followed_id", followedId);
        userFollowService.delete(ew);
        return R.ok("已取消关注");
    }

    /** 当前用户是否已关注某用户（未登录返回 false） */
    @IgnoreAuth
    @RequestMapping({"/status", "/relation-status"})
    public R status(@RequestParam Long followedId, HttpServletRequest request) {
        // @IgnoreAuth 时拦截器不会把 Token 写入 session，需从 Token 头解析当前用户
        Long followerId = null;
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj != null) {
            followerId = Long.valueOf(uidObj.toString());
        } else {
            String token = request.getHeader("Token");
            if (token != null && !token.trim().isEmpty()) {
                TokenEntity te = tokenService.getTokenEntity(token.trim());
                if (te != null && te.getUserId() != null) {
                    followerId = te.getUserId();
                }
            }
        }
        boolean followed = false;
        if (followerId != null) {
            EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
            ew.eq("follower_id", followerId).eq("followed_id", followedId);
            followed = userFollowService.selectCount(ew) > 0;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("followed", followed);
        return R.ok().put("data", data);
    }

    /** 某用户的粉丝数量 */
    @IgnoreAuth
    @RequestMapping({"/fansCount", "/followers-count"})
    public R fansCount(@RequestParam Long userId) {
        EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
        ew.eq("followed_id", userId);
        int count = userFollowService.selectCount(ew);
        return R.ok().put("fansCount", count).put("followersCount", count);
    }

    /** 关注我的列表（谁关注了我）- 供消息中心使用，需登录 */
    @RequestMapping({"/followMeList", "/followers"})
    public R followMeList(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long myId = Long.valueOf(uidObj.toString());
        EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
        ew.eq("followed_id", myId).orderBy("created_at", false);
        List<UserFollowEntity> list = userFollowService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (UserFollowEntity g : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getCreatedAt());
            item.put("createdAt", g.getCreatedAt());
            item.put("followerId", g.getFollowerId());
            item.put("follower_id", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
            item.put("operatorTouxiang", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
            if (u != null) {
                item.put("username", u.getYonghuzhanghao() != null ? u.getYonghuzhanghao() : "");
                item.put("displayName", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("avatarUrl", u.getTouxiang() != null ? u.getTouxiang() : "");
            }
            result.add(item);
        }
        return R.ok().put("data", result);
    }

    /** 我关注的列表（我关注了谁）- 需登录 */
    @RequestMapping({"/myFollowList", "/following"})
    public R myFollowList(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long myId = Long.valueOf(uidObj.toString());
        EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
        ew.eq("follower_id", myId).orderBy("created_at", false);
        List<UserFollowEntity> list = userFollowService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (UserFollowEntity g : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getCreatedAt());
            item.put("createdAt", g.getCreatedAt());
            item.put("followedId", g.getFollowedId());
            item.put("followed_id", g.getFollowedId());
            UserEntity u = userService.selectById(g.getFollowedId());
            if (u != null) {
                item.put("userId", u.getId());
                item.put("yonghuzhanghao", u.getYonghuzhanghao());
                item.put("yonghuxingming", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("touxiang", u.getTouxiang() != null ? u.getTouxiang() : "");
                item.put("xingbie", u.getXingbie() != null ? u.getXingbie() : "");
                item.put("phone", u.getPhone() != null ? u.getPhone() : "");
                item.put("username", u.getYonghuzhanghao() != null ? u.getYonghuzhanghao() : "");
                item.put("displayName", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("avatarUrl", u.getTouxiang() != null ? u.getTouxiang() : "");
                item.put("gender", u.getXingbie() != null ? u.getXingbie() : "");
            }
            result.add(item);
        }
        return R.ok().put("data", result);
    }

    /** 我的粉丝列表（谁关注了我）- 需登录 */
    @RequestMapping({"/myFansList", "/my-followers"})
    public R myFansList(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long myId = Long.valueOf(uidObj.toString());
        EntityWrapper<UserFollowEntity> ew = new EntityWrapper<>();
        ew.eq("followed_id", myId).orderBy("created_at", false);
        List<UserFollowEntity> list = userFollowService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (UserFollowEntity g : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getCreatedAt());
            item.put("createdAt", g.getCreatedAt());
            item.put("followerId", g.getFollowerId());
            item.put("follower_id", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            if (u != null) {
                item.put("userId", u.getId());
                item.put("yonghuzhanghao", u.getYonghuzhanghao());
                item.put("yonghuxingming", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("touxiang", u.getTouxiang() != null ? u.getTouxiang() : "");
                item.put("xingbie", u.getXingbie() != null ? u.getXingbie() : "");
                item.put("phone", u.getPhone() != null ? u.getPhone() : "");
                item.put("username", u.getYonghuzhanghao() != null ? u.getYonghuzhanghao() : "");
                item.put("displayName", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("avatarUrl", u.getTouxiang() != null ? u.getTouxiang() : "");
                item.put("gender", u.getXingbie() != null ? u.getXingbie() : "");
            }
            result.add(item);
        }
        return R.ok().put("data", result);
    }
}
