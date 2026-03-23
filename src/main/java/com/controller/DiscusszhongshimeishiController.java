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

import com.entity.DiscusszhongshimeishiEntity;
import com.entity.view.DiscusszhongshimeishiView;
import com.entity.UserEntity;
import com.service.UserService;
import com.service.DiscusszhongshimeishiService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 中式美食评论表
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/discusszhongshimeishi")
public class DiscusszhongshimeishiController {
    private static final String SOURCE_TYPE = "zhongshimeishi";

    @Autowired
    private DiscusszhongshimeishiService discusszhongshimeishiService;
    @Autowired
    private UserService userService;


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DiscusszhongshimeishiEntity discusszhongshimeishi,
		HttpServletRequest request){
        EntityWrapper<DiscusszhongshimeishiEntity> ew = new EntityWrapper<DiscusszhongshimeishiEntity>();
		ew.eq("sourceType", SOURCE_TYPE);
		PageUtils page = discusszhongshimeishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discusszhongshimeishi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DiscusszhongshimeishiEntity discusszhongshimeishi, 
		HttpServletRequest request){
        EntityWrapper<DiscusszhongshimeishiEntity> ew = new EntityWrapper<DiscusszhongshimeishiEntity>();
		ew.eq("sourceType", SOURCE_TYPE);
		PageUtils page = discusszhongshimeishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discusszhongshimeishi), params), params));
        List<DiscusszhongshimeishiEntity> records = (List<DiscusszhongshimeishiEntity>) page.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (DiscusszhongshimeishiEntity e : records) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("refid", e.getRefid());
            m.put("userid", e.getUserid());
            m.put("nickname", e.getNickname());
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
    public R list( DiscusszhongshimeishiEntity discusszhongshimeishi){
       	EntityWrapper<DiscusszhongshimeishiEntity> ew = new EntityWrapper<DiscusszhongshimeishiEntity>();
      	ew.eq("sourceType", SOURCE_TYPE);
      	ew.allEq(MPUtil.allEQMapPre( discusszhongshimeishi, "discusszhongshimeishi")); 
        return R.ok().put("data", discusszhongshimeishiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscusszhongshimeishiEntity discusszhongshimeishi){
        EntityWrapper< DiscusszhongshimeishiEntity> ew = new EntityWrapper< DiscusszhongshimeishiEntity>();
		ew.eq("sourceType", SOURCE_TYPE);
 		ew.allEq(MPUtil.allEQMapPre( discusszhongshimeishi, "discusszhongshimeishi")); 
		DiscusszhongshimeishiView discusszhongshimeishiView =  discusszhongshimeishiService.selectView(ew);
		return R.ok("查询中式美食评论表成功").put("data", discusszhongshimeishiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DiscusszhongshimeishiEntity discusszhongshimeishi = discusszhongshimeishiService.selectById(id);
        return R.ok().put("data", discusszhongshimeishi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DiscusszhongshimeishiEntity discusszhongshimeishi = discusszhongshimeishiService.selectById(id);
        return R.ok().put("data", discusszhongshimeishi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscusszhongshimeishiEntity discusszhongshimeishi, HttpServletRequest request){
    	discusszhongshimeishi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	discusszhongshimeishi.setSourceType(SOURCE_TYPE);
    	if (discusszhongshimeishi.getUserid() != null) {
            UserEntity y = userService.selectById(discusszhongshimeishi.getUserid());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                discusszhongshimeishi.setNickname(y.getYonghuxingming());
            }
        }
        discusszhongshimeishiService.insert(discusszhongshimeishi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscusszhongshimeishiEntity discusszhongshimeishi, HttpServletRequest request){
    	discusszhongshimeishi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        discusszhongshimeishi.setSourceType(SOURCE_TYPE);
    	if (discusszhongshimeishi.getUserid() != null) {
            UserEntity y = userService.selectById(discusszhongshimeishi.getUserid());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                discusszhongshimeishi.setNickname(y.getYonghuxingming());
            }
        }
        discusszhongshimeishiService.insert(discusszhongshimeishi);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DiscusszhongshimeishiEntity discusszhongshimeishi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(discusszhongshimeishi);
        discusszhongshimeishi.setSourceType(SOURCE_TYPE);
        discusszhongshimeishiService.updateById(discusszhongshimeishi);//全部更新
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
            discusszhongshimeishiService.deleteBatchIds(Arrays.asList(ids));
            return R.ok();
        }

        // 普通用户：只能删除自己的评论
        if (userId == null) {
            return R.error(401, "请先登录");
        }

        List<Long> canDeleteIds = new ArrayList<>();
        for (Long id : ids) {
            DiscusszhongshimeishiEntity entity = discusszhongshimeishiService.selectById(id);
            if (entity != null && entity.getUserid() != null && entity.getUserid().longValue() == userId.longValue()) {
                canDeleteIds.add(id);
            }
        }

        if (canDeleteIds.isEmpty()) {
            return R.error("没有权限删除这些评论");
        }

        discusszhongshimeishiService.deleteBatchIds(canDeleteIds);
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<DiscusszhongshimeishiEntity> wrapper = new EntityWrapper<DiscusszhongshimeishiEntity>();
		wrapper.eq("sourceType", SOURCE_TYPE);
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = discusszhongshimeishiService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}
