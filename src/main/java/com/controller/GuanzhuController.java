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
import com.entity.GuanzhuEntity;
import com.entity.UserEntity;
import com.service.GuanzhuService;
import com.service.UserService;
import com.utils.R;

/**
 * 用户关注：关注/取关、关注状态、粉丝数、关注我的列表
 */
@RestController
@RequestMapping("/guanzhu")
public class GuanzhuController {

    @Autowired
    private GuanzhuService guanzhuService;
    @Autowired
    private UserService userService;

    /** 关注某用户（需登录） */
    @RequestMapping("/follow")
    public R follow(@RequestParam Long followedId, HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long followerId = Long.valueOf(uidObj.toString());
        if (followerId.equals(followedId)) {
            return R.error("不能关注自己");
        }
        EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
        ew.eq("follower_id", followerId).eq("followed_id", followedId);
        if (guanzhuService.selectCount(ew) > 0) {
            return R.ok("已经关注过了");
        }
        GuanzhuEntity g = new GuanzhuEntity();
        g.setFollowerId(followerId);
        g.setFollowedId(followedId);
        guanzhuService.insert(g);
        return R.ok("关注成功");
    }

    /** 取消关注（需登录） */
    @RequestMapping("/unfollow")
    public R unfollow(@RequestParam Long followedId, HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long followerId = Long.valueOf(uidObj.toString());
        EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
        ew.eq("follower_id", followerId).eq("followed_id", followedId);
        guanzhuService.delete(ew);
        return R.ok("已取消关注");
    }

    /** 当前用户是否已关注某用户（未登录返回 false） */
    @IgnoreAuth
    @RequestMapping("/status")
    public R status(@RequestParam Long followedId, HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        boolean followed = false;
        if (uidObj != null) {
            Long followerId = Long.valueOf(uidObj.toString());
            EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
            ew.eq("follower_id", followerId).eq("followed_id", followedId);
            followed = guanzhuService.selectCount(ew) > 0;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("followed", followed);
        return R.ok().put("data", data);
    }

    /** 某用户的粉丝数量 */
    @IgnoreAuth
    @RequestMapping("/fansCount")
    public R fansCount(@RequestParam Long userId) {
        EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
        ew.eq("followed_id", userId);
        int count = guanzhuService.selectCount(ew);
        return R.ok().put("fansCount", count);
    }

    /** 关注我的列表（谁关注了我）- 供消息中心使用，需登录 */
    @RequestMapping("/followMeList")
    public R followMeList(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long myId = Long.valueOf(uidObj.toString());
        EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
        ew.eq("followed_id", myId).orderBy("addtime", false);
        List<GuanzhuEntity> list = guanzhuService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (GuanzhuEntity g : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getAddtime());
            item.put("followerId", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            item.put("operatorName", u != null && u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
            item.put("operatorTouxiang", u != null && u.getTouxiang() != null ? u.getTouxiang() : "");
            result.add(item);
        }
        return R.ok().put("data", result);
    }

    /** 我关注的列表（我关注了谁）- 需登录 */
    @RequestMapping("/myFollowList")
    public R myFollowList(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long myId = Long.valueOf(uidObj.toString());
        EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
        ew.eq("follower_id", myId).orderBy("addtime", false);
        List<GuanzhuEntity> list = guanzhuService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (GuanzhuEntity g : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getAddtime());
            item.put("followedId", g.getFollowedId());
            UserEntity u = userService.selectById(g.getFollowedId());
            if (u != null) {
                item.put("userId", u.getId());
                item.put("yonghuzhanghao", u.getYonghuzhanghao());
                item.put("yonghuxingming", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("touxiang", u.getTouxiang() != null ? u.getTouxiang() : "");
                item.put("xingbie", u.getXingbie() != null ? u.getXingbie() : "");
                item.put("lianxifangshi", u.getLianxifangshi() != null ? u.getLianxifangshi() : "");
            }
            result.add(item);
        }
        return R.ok().put("data", result);
    }

    /** 我的粉丝列表（谁关注了我）- 需登录 */
    @RequestMapping("/myFansList")
    public R myFansList(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj == null) {
            return R.error("请先登录");
        }
        Long myId = Long.valueOf(uidObj.toString());
        EntityWrapper<GuanzhuEntity> ew = new EntityWrapper<>();
        ew.eq("followed_id", myId).orderBy("addtime", false);
        List<GuanzhuEntity> list = guanzhuService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (GuanzhuEntity g : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("addtime", g.getAddtime());
            item.put("followerId", g.getFollowerId());
            UserEntity u = userService.selectById(g.getFollowerId());
            if (u != null) {
                item.put("userId", u.getId());
                item.put("yonghuzhanghao", u.getYonghuzhanghao());
                item.put("yonghuxingming", u.getYonghuxingming() != null ? u.getYonghuxingming() : "用户");
                item.put("touxiang", u.getTouxiang() != null ? u.getTouxiang() : "");
                item.put("xingbie", u.getXingbie() != null ? u.getXingbie() : "");
                item.put("lianxifangshi", u.getLianxifangshi() != null ? u.getLianxifangshi() : "");
            }
            result.add(item);
        }
        return R.ok().put("data", result);
    }
}
