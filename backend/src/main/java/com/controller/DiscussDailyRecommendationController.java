package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.DiscussDailyRecommendationEntity;
import com.entity.view.DiscussDailyRecommendationView;

import com.service.DiscussDailyRecommendationService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 姣忔棩鎺ㄨ崘璇勮琛?
 * 鍚庣鎺ュ彛
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/daily-recommendation-comments")
public class DiscussDailyRecommendationController {
    @Autowired
    private DiscussDailyRecommendationService discussDailyRecommendationService;


    


    /**
     * 鍚庣鍒楄〃
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DiscussDailyRecommendationEntity discussDailyRecommendation,
		HttpServletRequest request){
        EntityWrapper<DiscussDailyRecommendationEntity> ew = new EntityWrapper<DiscussDailyRecommendationEntity>();
		PageUtils page = discussDailyRecommendationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussDailyRecommendation), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 鍓嶇鍒楄〃
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DiscussDailyRecommendationEntity discussDailyRecommendation, 
		HttpServletRequest request){
        EntityWrapper<DiscussDailyRecommendationEntity> ew = new EntityWrapper<DiscussDailyRecommendationEntity>();
		PageUtils page = discussDailyRecommendationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussDailyRecommendation), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 鍒楄〃
     */
    @RequestMapping("/lists")
    public R list( DiscussDailyRecommendationEntity discussDailyRecommendation){
       	EntityWrapper<DiscussDailyRecommendationEntity> ew = new EntityWrapper<DiscussDailyRecommendationEntity>();
      	ew.allEq(MPUtil.allEQMapPre( discussDailyRecommendation, "dailyRecommendationComments")); 
        return R.ok().put("data", discussDailyRecommendationService.selectListView(ew));
    }

	 /**
     * 鏌ヨ
     */
    @RequestMapping("/query")
    public R query(DiscussDailyRecommendationEntity discussDailyRecommendation){
        EntityWrapper< DiscussDailyRecommendationEntity> ew = new EntityWrapper< DiscussDailyRecommendationEntity>();
 		ew.allEq(MPUtil.allEQMapPre( discussDailyRecommendation, "dailyRecommendationComments")); 
		DiscussDailyRecommendationView discussDailyRecommendationView =  discussDailyRecommendationService.selectView(ew);
		return R.ok("查询每日推荐评论表成功").put("data", discussDailyRecommendationView);
    }
	
    /**
     * 鍚庣璇︽儏
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DiscussDailyRecommendationEntity discussDailyRecommendation = discussDailyRecommendationService.selectById(id);
        return R.ok().put("data", discussDailyRecommendation);
    }

    /**
     * 鍓嶇璇︽儏
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DiscussDailyRecommendationEntity discussDailyRecommendation = discussDailyRecommendationService.selectById(id);
        return R.ok().put("data", discussDailyRecommendation);
    }
    



    /**
     * 鍚庣淇濆瓨
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscussDailyRecommendationEntity discussDailyRecommendation, HttpServletRequest request){
    	discussDailyRecommendation.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(discussDailyRecommendation);
        discussDailyRecommendationService.insert(discussDailyRecommendation);
        return R.ok();
    }
    
    /**
     * 鍓嶇淇濆瓨
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscussDailyRecommendationEntity discussDailyRecommendation, HttpServletRequest request){
    	discussDailyRecommendation.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(discussDailyRecommendation);
        discussDailyRecommendationService.insert(discussDailyRecommendation);
        return R.ok();
    }

    /**
     * 淇敼
     */
    @RequestMapping("/update")
    public R update(@RequestBody DiscussDailyRecommendationEntity discussDailyRecommendation, HttpServletRequest request){
        //ValidatorUtils.validateEntity(discussDailyRecommendation);
        discussDailyRecommendationService.updateById(discussDailyRecommendation);//鍏ㄩ儴鏇存柊
        return R.ok();
    }
    

    /**
     * 鍒犻櫎
     * 鏅€氱敤鎴蜂粎鍙垹闄よ嚜宸辩殑璇勮锛岀鐞嗗憳鍙垹闄や换鎰忚瘎璁?
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

        // 管理员可删除任意评论
        if ("管理员".equals(role)) {
            discussDailyRecommendationService.deleteBatchIds(Arrays.asList(ids));
            return R.ok();
        }

        // 普通用户仅可删除自己的评论
        if (userId == null) {
            return R.error(401, "请先登录");
        }

        List<Long> canDeleteIds = new ArrayList<>();
        for (Long id : ids) {
            DiscussDailyRecommendationEntity entity = discussDailyRecommendationService.selectById(id);
            if (entity != null && entity.getUserId() != null && entity.getUserId().longValue() == userId.longValue()) {
                canDeleteIds.add(id);
            }
        }

        if (canDeleteIds.isEmpty()) {
            return R.error("没有权限删除这些评论");
        }

        discussDailyRecommendationService.deleteBatchIds(canDeleteIds);
        return R.ok();
    }
    
    /**
     * 鎻愰啋鎺ュ彛
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
		
		Wrapper<DiscussDailyRecommendationEntity> wrapper = new EntityWrapper<DiscussDailyRecommendationEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = discussDailyRecommendationService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}

