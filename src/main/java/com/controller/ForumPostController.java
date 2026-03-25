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
import java.util.Comparator;
import java.util.stream.Collectors;
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

import com.entity.ForumPostEntity;
import com.entity.view.ForumPostView;

import com.service.ForumPostService;
import com.service.UserService;
import com.entity.UserEntity;
import com.service.UserInteractionsService;
import com.entity.UserInteractionsEntity;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * ????
 * ????
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping({"/forum-post", "/forum_post", "/news"})
public class ForumPostController {
    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInteractionsService userInteractionsService;

    private int countAction(Long refid, String type) {
        EntityWrapper<UserInteractionsEntity> ew = new EntityWrapper<>();
        ew.eq("resource_id", refid).eq("interaction_type", type);
        return userInteractionsService.selectCount(ew);
    }


    


    /**
     * ????
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ForumPostEntity forumPost,
		HttpServletRequest request){
        EntityWrapper<ForumPostEntity> ew = new EntityWrapper<ForumPostEntity>();
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if(tableNameObj != null && "user".equals(tableNameObj.toString())) {
            Object userIdObj = request.getSession().getAttribute("userId");
            if(userIdObj instanceof Long) {
                forumPost.setUserid((Long)userIdObj);
            }
        }
		PageUtils page = forumPostService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forumPost), params), params));
        for (Object obj : page.getList()) {
            if (obj instanceof ForumPostEntity) {
                ForumPostEntity entity = (ForumPostEntity) obj;
                if (entity.getId() != null) {
                    entity.setThumbsupnum(countAction(entity.getId(), "21"));
                }
            }
        }

        return R.ok().put("data", page);
    }
    
    /**
     * ????
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ForumPostEntity forumPost, 
		HttpServletRequest request){
        String sort = params.get("sort") != null ? params.get("sort").toString() : "";
        // ?????? ??+?? ????????
        if ("hot".equalsIgnoreCase(sort)) {
            EntityWrapper<ForumPostEntity> ew = new EntityWrapper<ForumPostEntity>();
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<ForumPostEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forumPost), paramsNoSort), paramsNoSort);
            List<ForumPostEntity> all = forumPostService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(ForumPostEntity::getId).collect(Collectors.toList());
                EntityWrapper<UserInteractionsEntity> suEw = new EntityWrapper<>();
                suEw.in("resource_id", ids).in("interaction_type", Arrays.asList("1", "21"));
                List<UserInteractionsEntity> suList = userInteractionsService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (UserInteractionsEntity su : suList) {
                    Long refId = su.getRefid();
                    if (refId != null) {
                        countMap.put(refId, countMap.getOrDefault(refId, 0) + 1);
                    }
                }
                final Map<Long, Integer> totalCount = countMap;
                all.sort(Comparator
                        .comparing((ForumPostEntity n) -> totalCount.getOrDefault(n.getId(), 0))
                        .reversed()
                        .thenComparing((ForumPostEntity n) -> n.getCreatedAt() != null ? n.getCreatedAt().getTime() : 0L,
                                Comparator.reverseOrder()));
                EntityWrapper<UserInteractionsEntity> thumbsEw = new EntityWrapper<>();
                thumbsEw.in("resource_id", ids).eq("interaction_type", "21");
                List<UserInteractionsEntity> thumbsList = userInteractionsService.selectList(thumbsEw);
                Map<Long, Integer> thumbsMap = new HashMap<>();
                for (UserInteractionsEntity thumbs : thumbsList) {
                    Long refId = thumbs.getRefid();
                    if (refId != null) {
                        thumbsMap.put(refId, thumbsMap.getOrDefault(refId, 0) + 1);
                    }
                }
                for (ForumPostEntity n : all) {
                    n.setThumbsupnum(thumbsMap.getOrDefault(n.getId(), 0));
                }
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<ForumPostEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        }
        EntityWrapper<ForumPostEntity> ew = new EntityWrapper<ForumPostEntity>();
		PageUtils page = forumPostService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forumPost), params), params));
        for (Object obj : page.getList()) {
            if (obj instanceof ForumPostEntity) {
                ForumPostEntity entity = (ForumPostEntity) obj;
                if (entity.getId() != null) {
                    entity.setThumbsupnum(countAction(entity.getId(), "21"));
                }
            }
        }
        return R.ok().put("data", page);
    }

	/**
     * ??
     */
    @RequestMapping("/lists")
    public R list(ForumPostEntity forumPost){
       	EntityWrapper<ForumPostEntity> ew = new EntityWrapper<ForumPostEntity>();
      	ew.allEq(MPUtil.allEQMapPre(forumPost, "forum_post")); 
        return R.ok().put("data", forumPostService.selectListView(ew));
    }

	 /**
     * ??
     */
    @RequestMapping("/query")
    public R query(ForumPostEntity forumPost){
        EntityWrapper< ForumPostEntity> ew = new EntityWrapper< ForumPostEntity>();
 		ew.allEq(MPUtil.allEQMapPre(forumPost, "forum_post")); 
		ForumPostView forumPostView = forumPostService.selectView(ew);
		return R.ok("????????").put("data", forumPostView);
    }
	
    /**
     * ????
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ForumPostEntity forumPost = forumPostService.selectById(id);
        if(forumPost != null && forumPost.getUserid() != null) {
            UserEntity user = userService.selectById(forumPost.getUserid());
            if(user != null) {
                forumPost.setYonghuzhanghao(user.getYonghuzhanghao());
                forumPost.setYonghuxingming(user.getYonghuxingming());
                forumPost.setTouxiang(user.getTouxiang());
            }
        }
        if (forumPost != null && forumPost.getId() != null) {
            forumPost.setThumbsupnum(countAction(forumPost.getId(), "21"));
        }
        return R.ok().put("data", forumPost);
    }

    /**
     * ????
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ForumPostEntity forumPost = forumPostService.selectById(id);
        if(forumPost != null && forumPost.getUserid() != null) {
            UserEntity user = userService.selectById(forumPost.getUserid());
            if(user != null) {
                forumPost.setYonghuzhanghao(user.getYonghuzhanghao());
                forumPost.setYonghuxingming(user.getYonghuxingming());
                forumPost.setTouxiang(user.getTouxiang());
            }
        }
        if (forumPost != null && forumPost.getId() != null) {
            forumPost.setThumbsupnum(countAction(forumPost.getId(), "21"));
        }
        return R.ok().put("data", forumPost);
    }
    



    /**
     * ????
     */
    @RequestMapping("/save")
    public R save(@RequestBody ForumPostEntity forumPost, HttpServletRequest request){
    	forumPost.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(forumPost);
        forumPostService.insert(forumPost);
        return R.ok();
    }
    
    /**
     * ????
     */
    @RequestMapping("/add")
    public R add(@RequestBody ForumPostEntity forumPost, HttpServletRequest request){
    	forumPost.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Object userIdObj = request.getSession().getAttribute("userId");
        if(userIdObj instanceof Long) {
            forumPost.setUserid((Long)userIdObj);
        }
    	//ValidatorUtils.validateEntity(forumPost);
        forumPostService.insert(forumPost);
        return R.ok();
    }

    /**
     * ??
     */
    @RequestMapping("/update")
    public R update(@RequestBody ForumPostEntity forumPost, HttpServletRequest request){
        //ValidatorUtils.validateEntity(forumPost);
        forumPostService.updateById(forumPost);//????
        return R.ok();
    }
    

    /**
     * ??
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        forumPostService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * ????
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
		
		Wrapper<ForumPostEntity> wrapper = new EntityWrapper<ForumPostEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = forumPostService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}

