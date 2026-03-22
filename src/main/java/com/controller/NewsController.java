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

import com.entity.NewsEntity;
import com.entity.view.NewsView;

import com.service.NewsService;
import com.service.UserService;
import com.entity.UserEntity;
import com.service.StoreupService;
import com.entity.StoreupEntity;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 美食论坛
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreupService storeupService;


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,NewsEntity news,
		HttpServletRequest request){
        EntityWrapper<NewsEntity> ew = new EntityWrapper<NewsEntity>();
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if(tableNameObj != null && "user".equals(tableNameObj.toString())) {
            Object userIdObj = request.getSession().getAttribute("userId");
            if(userIdObj instanceof Long) {
                news.setUserid((Long)userIdObj);
            }
        }
		PageUtils page = newsService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, news), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,NewsEntity news, 
		HttpServletRequest request){
        String sort = params.get("sort") != null ? params.get("sort").toString() : "";
        // 热门排序：按 点赞+收藏 总次数 从高到低
        if ("hot".equalsIgnoreCase(sort)) {
            EntityWrapper<NewsEntity> ew = new EntityWrapper<NewsEntity>();
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<NewsEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, news), paramsNoSort), paramsNoSort);
            List<NewsEntity> all = newsService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(NewsEntity::getId).collect(Collectors.toList());
                EntityWrapper<StoreupEntity> suEw = new EntityWrapper<>();
                suEw.eq("tablename", "news").in("refid", ids).in("type", Arrays.asList("1", "21"));
                List<StoreupEntity> suList = storeupService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (StoreupEntity su : suList) {
                    Long refId = su.getRefid();
                    if (refId != null) {
                        countMap.put(refId, countMap.getOrDefault(refId, 0) + 1);
                    }
                }
                final Map<Long, Integer> totalCount = countMap;
                all.sort(Comparator
                        .comparing((NewsEntity n) -> totalCount.getOrDefault(n.getId(), 0))
                        .reversed()
                        .thenComparing((NewsEntity n) -> n.getAddtime() != null ? n.getAddtime().getTime() : 0L,
                                Comparator.reverseOrder()));
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<NewsEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        }
        EntityWrapper<NewsEntity> ew = new EntityWrapper<NewsEntity>();
		PageUtils page = newsService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, news), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( NewsEntity news){
       	EntityWrapper<NewsEntity> ew = new EntityWrapper<NewsEntity>();
      	ew.allEq(MPUtil.allEQMapPre( news, "news")); 
        return R.ok().put("data", newsService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(NewsEntity news){
        EntityWrapper< NewsEntity> ew = new EntityWrapper< NewsEntity>();
 		ew.allEq(MPUtil.allEQMapPre( news, "news")); 
		NewsView newsView =  newsService.selectView(ew);
		return R.ok("查询美食论坛成功").put("data", newsView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        NewsEntity news = newsService.selectById(id);
        if(news != null && news.getUserid() != null) {
            UserEntity user = userService.selectById(news.getUserid());
            if(user != null) {
                news.setYonghuzhanghao(user.getYonghuzhanghao());
                news.setYonghuxingming(user.getYonghuxingming());
                news.setTouxiang(user.getTouxiang());
            }
        }
        return R.ok().put("data", news);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        NewsEntity news = newsService.selectById(id);
        if(news != null && news.getUserid() != null) {
            UserEntity user = userService.selectById(news.getUserid());
            if(user != null) {
                news.setYonghuzhanghao(user.getYonghuzhanghao());
                news.setYonghuxingming(user.getYonghuxingming());
                news.setTouxiang(user.getTouxiang());
            }
        }
        return R.ok().put("data", news);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody NewsEntity news, HttpServletRequest request){
    	news.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(news);
        newsService.insert(news);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody NewsEntity news, HttpServletRequest request){
    	news.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Object userIdObj = request.getSession().getAttribute("userId");
        if(userIdObj instanceof Long) {
            news.setUserid((Long)userIdObj);
        }
    	//ValidatorUtils.validateEntity(news);
        newsService.insert(news);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody NewsEntity news, HttpServletRequest request){
        //ValidatorUtils.validateEntity(news);
        newsService.updateById(news);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        newsService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<NewsEntity> wrapper = new EntityWrapper<NewsEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = newsService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}
