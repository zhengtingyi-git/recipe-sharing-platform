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

import com.entity.StoreupEntity;
import com.entity.ForumPostEntity;
import com.entity.ZhongshimeishiEntity;
import com.entity.WaiguomeishiEntity;
import com.entity.view.StoreupView;

import com.service.StoreupService;
import com.service.ForumPostService;
import com.service.ZhongshimeishiService;
import com.service.WaiguomeishiService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 收藏表
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/storeup")
public class StoreupController {
    @Autowired
    private StoreupService storeupService;
    @Autowired
    private ForumPostService forumPostService;
    @Autowired
    private ZhongshimeishiService zhongshimeishiService;
    @Autowired
    private WaiguomeishiService waiguomeishiService;


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,StoreupEntity storeup,
		HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		storeup.setUserid((Long)request.getSession().getAttribute("userId"));
    	}
        // 注意：storeup 表已经删除了 tablename/name/picture 等列，
        // 这些字段在实体里仍然存在（exist=false），如果直接参与 likeOrEq，会生成不存在的 where 条件。
        storeup.setTablename(null);
        storeup.setName(null);
        storeup.setPicture(null);
        String categoryTablename = params.get("tablename") != null ? params.get("tablename").toString().trim() : "";
        params.remove("name");
        params.remove("tablename");
        EntityWrapper<StoreupEntity> ew = new EntityWrapper<StoreupEntity>();
		ew = (EntityWrapper<StoreupEntity>) MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, storeup), params), params);
		applyStoreupCategoryFilter(ew, categoryTablename);
		PageUtils page = storeupService.queryPage(params, ew);
        enrichStoreupPage(page);

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,StoreupEntity storeup, 
		HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		storeup.setUserid((Long)request.getSession().getAttribute("userId"));
    	}
        // 同 page：避免生成不存在的 where(tablename/name/picture)
        storeup.setTablename(null);
        storeup.setName(null);
        storeup.setPicture(null);
        String categoryTablename = params.get("tablename") != null ? params.get("tablename").toString().trim() : "";
        params.remove("name");
        params.remove("tablename");
        EntityWrapper<StoreupEntity> ew = new EntityWrapper<StoreupEntity>();
		ew = (EntityWrapper<StoreupEntity>) MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, storeup), params), params);
		applyStoreupCategoryFilter(ew, categoryTablename);
		PageUtils page = storeupService.queryPage(params, ew);
        enrichStoreupPage(page);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( StoreupEntity storeup){
       	EntityWrapper<StoreupEntity> ew = new EntityWrapper<StoreupEntity>();
      	ew.allEq(MPUtil.allEQMapPre( storeup, "storeup")); 
        return R.ok().put("data", storeupService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(StoreupEntity storeup){
        EntityWrapper< StoreupEntity> ew = new EntityWrapper< StoreupEntity>();
 		ew.allEq(MPUtil.allEQMapPre( storeup, "storeup")); 
		StoreupView storeupView =  storeupService.selectView(ew);
		return R.ok("查询收藏表成功").put("data", storeupView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        StoreupEntity storeup = storeupService.selectById(id);
        return R.ok().put("data", storeup);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        StoreupEntity storeup = storeupService.selectById(id);
        if (storeup != null) {
            fillTargetFields(storeup);
        }
        return R.ok().put("data", storeup);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody StoreupEntity storeup, HttpServletRequest request){
    	storeup.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(storeup);
    	storeup.setUserid((Long)request.getSession().getAttribute("userId"));
        storeupService.insert(storeup);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody StoreupEntity storeup, HttpServletRequest request){
    	storeup.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(storeup);
    	storeup.setUserid((Long)request.getSession().getAttribute("userId"));
        storeupService.insert(storeup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody StoreupEntity storeup, HttpServletRequest request){
        //ValidatorUtils.validateEntity(storeup);
        storeupService.updateById(storeup);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        storeupService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<StoreupEntity> wrapper = new EntityWrapper<StoreupEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}
		if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		wrapper.eq("userid", (Long)request.getSession().getAttribute("userId"));
    	}


		int count = storeupService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







    /**
     * 个人中心「我的收藏/我的点赞」按分类筛选：库表无 tablename 列，通过 refid 与 recipe / forum_post 关联。
     * 中式/外国美食共用 recipe 表，以 recipetype 区分。
     */
    private void applyStoreupCategoryFilter(EntityWrapper<StoreupEntity> ew, String tablename) {
        if (ew == null || StringUtils.isBlank(tablename)) {
            return;
        }
        switch (tablename) {
            case "waiguomeishi":
                ew.andNew().where("refid IN (SELECT id FROM recipe WHERE recipetype = {0})", "waiguomeishi");
                break;
            case "zhongshimeishi":
                ew.andNew().where("refid IN (SELECT id FROM recipe WHERE recipetype = {0})", "zhongshimeishi");
                break;
            case "forum_post":
                ew.andNew().where("refid IN (SELECT id FROM forum_post)");
                break;
            default:
                break;
        }
    }

    private void enrichStoreupPage(PageUtils page) {
        if (page == null || page.getList() == null) {
            return;
        }
        List<?> records = page.getList();
        List<Object> enriched = records.stream().map(item -> {
            if (item instanceof StoreupEntity) {
                StoreupEntity storeup = (StoreupEntity) item;
                fillTargetFields(storeup);
                return storeup;
            }
            return item;
        }).collect(Collectors.toList());
        page.setList(enriched);
    }

    private void fillTargetFields(StoreupEntity storeup) {
        if (storeup == null || storeup.getRefid() == null) {
            return;
        }
        Long refId = storeup.getRefid();

        ForumPostEntity post = forumPostService.selectById(refId);
        if (post != null) {
            storeup.setTablename("forum_post");
            storeup.setName(post.getTitle());
            storeup.setPicture(post.getPicture());
            return;
        }

        ZhongshimeishiEntity zhongshi = zhongshimeishiService.selectById(refId);
        if (zhongshi != null) {
            storeup.setTablename("zhongshimeishi");
            storeup.setName(zhongshi.getCaipinmingcheng());
            storeup.setPicture(zhongshi.getTupian());
            return;
        }

        WaiguomeishiEntity waiguo = waiguomeishiService.selectById(refId);
        if (waiguo != null) {
            storeup.setTablename("waiguomeishi");
            storeup.setName(waiguo.getCaipinmingcheng());
            storeup.setPicture(waiguo.getTupian());
        }
    }



}
