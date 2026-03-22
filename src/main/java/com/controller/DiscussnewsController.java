package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.DiscussnewsEntity;
import com.entity.view.DiscussnewsView;
import com.entity.UserEntity;
import com.service.UserService;
import com.service.DiscussnewsService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 美食论坛评论表
 * 后端接口
 */
@RestController
@RequestMapping("/discussnews")
public class DiscussnewsController {
    @Autowired
    private DiscussnewsService discussnewsService;
    @Autowired
    private UserService userService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DiscussnewsEntity discussnews,
		HttpServletRequest request){
        EntityWrapper<DiscussnewsEntity> ew = new EntityWrapper<DiscussnewsEntity>();
		PageUtils page = discussnewsService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussnews), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DiscussnewsEntity discussnews, 
		HttpServletRequest request){
        EntityWrapper<DiscussnewsEntity> ew = new EntityWrapper<DiscussnewsEntity>();
		PageUtils page = discussnewsService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussnews), params), params));
        List<DiscussnewsEntity> records = (List<DiscussnewsEntity>) page.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (DiscussnewsEntity e : records) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("refid", e.getRefid());
            m.put("userid", e.getUserid());
            m.put("nickname", e.getNickname());
			m.put("cpicture", e.getCpicture());
            m.put("content", e.getContent());
            m.put("reply", e.getReply());
            m.put("addtime", e.getAddtime());
            m.put("touxiang", "");
            if (e.getUserid() != null) {
                UserEntity y = userService.selectById(e.getUserid());
                if (y != null) {
                    if (y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                        m.put("nickname", y.getYonghuxingming());
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

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( DiscussnewsEntity discussnews){
       	EntityWrapper<DiscussnewsEntity> ew = new EntityWrapper<DiscussnewsEntity>();
      	ew.allEq(MPUtil.allEQMapPre( discussnews, "discussnews")); 
        return R.ok().put("data", discussnewsService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscussnewsEntity discussnews){
        EntityWrapper< DiscussnewsEntity> ew = new EntityWrapper< DiscussnewsEntity>();
 		ew.allEq(MPUtil.allEQMapPre( discussnews, "discussnews")); 
		DiscussnewsView discussnewsView =  discussnewsService.selectView(ew);
		return R.ok("查询美食论坛评论表成功").put("data", discussnewsView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DiscussnewsEntity discussnews = discussnewsService.selectById(id);
        return R.ok().put("data", discussnews);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DiscussnewsEntity discussnews = discussnewsService.selectById(id);
        return R.ok().put("data", discussnews);
    }
    
    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscussnewsEntity discussnews, HttpServletRequest request){
    	discussnews.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	if (discussnews.getUserid() != null) {
            UserEntity y = userService.selectById(discussnews.getUserid());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                discussnews.setNickname(y.getYonghuxingming());
            }
        }
        discussnewsService.insert(discussnews);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscussnewsEntity discussnews, HttpServletRequest request){
    	discussnews.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	if (discussnews.getUserid() != null) {
            UserEntity y = userService.selectById(discussnews.getUserid());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                discussnews.setNickname(y.getYonghuxingming());
            }
        }
        discussnewsService.insert(discussnews);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DiscussnewsEntity discussnews, HttpServletRequest request){
        discussnewsService.updateById(discussnews);//全部更新
        return R.ok();
    }
    
    /**
     * 删除
     * 普通用户仅可删除自己的评论，管理员可删除任意评论
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        Object userIdObj = request.getSession().getAttribute("userId");
        Object roleObj = request.getSession().getAttribute("role");
        Long userId = null;
        String role = null;
        if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        } else if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        }
        if (roleObj instanceof String) {
            role = (String) roleObj;
        }

        // 管理员：不做限制，直接删除
        if ("管理员".equals(role)) {
            discussnewsService.deleteBatchIds(Arrays.asList(ids));
            return R.ok();
        }

        // 普通用户：只能删除自己的评论
        if (userId == null) {
            return R.error(401, "请先登录");
        }

        List<Long> canDeleteIds = new ArrayList<>();
        for (Long id : ids) {
            DiscussnewsEntity entity = discussnewsService.selectById(id);
            if (entity != null && entity.getUserid() != null && entity.getUserid().longValue() == userId.longValue()) {
                canDeleteIds.add(id);
            }
        }

        if (canDeleteIds.isEmpty()) {
            return R.error("没有权限删除这些评论");
        }

        discussnewsService.deleteBatchIds(canDeleteIds);
        return R.ok();
    }
}

