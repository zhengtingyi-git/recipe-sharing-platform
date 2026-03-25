package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
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

import com.entity.WaiguomeishiEntity;
import com.entity.view.WaiguomeishiView;

import com.service.WaiguomeishiService;
import com.service.UserService;
import com.entity.UserEntity;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.service.UserInteractionsService;
import com.entity.UserInteractionsEntity;

/**
 * 外国美食
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/waiguomeishi")
public class WaiguomeishiController {
    @Autowired
    private WaiguomeishiService waiguomeishiService;

    @Autowired
    private UserInteractionsService userInteractionsService;

    @Autowired
    private UserService userService;

    private int countAction(Long refid, String type) {
        EntityWrapper<UserInteractionsEntity> ew = new EntityWrapper<>();
        ew.eq("resource_id", refid).eq("interaction_type", type);
        return userInteractionsService.selectCount(ew);
    }

    private void fillRecipeUser(WaiguomeishiEntity entity) {
        if (entity == null || entity.getUserid() == null) {
            return;
        }
        UserEntity user = userService.selectById(entity.getUserid());
        if (user != null) {
            entity.setYonghuzhanghao(user.getYonghuzhanghao());
            entity.setYonghuxingming(user.getYonghuxingming());
            entity.setTouxiang(user.getTouxiang());
        }
    }

    private void fillRecipeUser(Iterable<?> list) {
        for (Object obj : list) {
            if (obj instanceof WaiguomeishiEntity) {
                fillRecipeUser((WaiguomeishiEntity) obj);
            }
        }
    }



    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,WaiguomeishiEntity waiguomeishi,
		HttpServletRequest request){
		waiguomeishi.setRecipetype("waiguomeishi");
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("user")) {
			waiguomeishi.setUserid((Long)request.getSession().getAttribute("userId"));
		}
        EntityWrapper<WaiguomeishiEntity> ew = new EntityWrapper<WaiguomeishiEntity>();
		PageUtils page = waiguomeishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, waiguomeishi), params), params));

        for(Object obj : page.getList()) {
            if(obj instanceof WaiguomeishiEntity) {
                WaiguomeishiEntity entity = (WaiguomeishiEntity)obj;
                fillRecipeUser(entity);
                if (entity.getId() != null) {
                    entity.setThumbsupnum(countAction(entity.getId(), "21"));
                    entity.setUserInteractionsNum(countAction(entity.getId(), "1"));
                }
            }
        }

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表（支持 sort=addtime/thumbsupnum/userInteractionsNum，order=desc）
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, WaiguomeishiEntity waiguomeishi,
		HttpServletRequest request){
		waiguomeishi.setRecipetype("waiguomeishi");
        String sort = params.get("sort") != null ? params.get("sort").toString() : "";
        if ("userInteractionsNum".equals(sort)) {
            EntityWrapper<WaiguomeishiEntity> ew = new EntityWrapper<>();
            if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
                ew.like("caipinmingcheng", params.get("caipinmingcheng").toString().replace("%", "").trim());
                waiguomeishi.setCaipinmingcheng(null);
            }
            if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
                ew.like("cailiao", params.get("cailiao").toString().replace("%", "").trim());
                waiguomeishi.setCailiao(null);
            }
            if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
                ew.like("caixi", params.get("caixi").toString().replace("%", "").trim());
                waiguomeishi.setCaixi(null);
            }
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<WaiguomeishiEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, waiguomeishi), paramsNoSort), paramsNoSort);
            List<WaiguomeishiEntity> all = waiguomeishiService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(WaiguomeishiEntity::getId).collect(Collectors.toList());
                EntityWrapper<UserInteractionsEntity> suEw = new EntityWrapper<>();
                suEw.in("resource_id", ids).eq("interaction_type", "1");
                List<UserInteractionsEntity> suList = userInteractionsService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (UserInteractionsEntity su : suList) {
                    countMap.put(su.getRefid(), countMap.getOrDefault(su.getRefid(), 0) + 1);
                }
                final Map<Long, Integer> userInteractionsCount = countMap;
                all.sort(Comparator.comparing((WaiguomeishiEntity d) -> userInteractionsCount.getOrDefault(d.getId(), 0)).reversed());
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<WaiguomeishiEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            fillRecipeUser(pageList);
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        } else if ("thumbsupnum".equals(sort)) {
            EntityWrapper<WaiguomeishiEntity> ew = new EntityWrapper<>();
            if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
                ew.like("caipinmingcheng", params.get("caipinmingcheng").toString().replace("%", "").trim());
                waiguomeishi.setCaipinmingcheng(null);
            }
            if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
                ew.like("cailiao", params.get("cailiao").toString().replace("%", "").trim());
                waiguomeishi.setCailiao(null);
            }
            if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
                ew.like("caixi", params.get("caixi").toString().replace("%", "").trim());
                waiguomeishi.setCaixi(null);
            }
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<WaiguomeishiEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, waiguomeishi), paramsNoSort), paramsNoSort);
            List<WaiguomeishiEntity> all = waiguomeishiService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(WaiguomeishiEntity::getId).collect(Collectors.toList());
                EntityWrapper<UserInteractionsEntity> suEw = new EntityWrapper<>();
                suEw.in("resource_id", ids).eq("interaction_type", "21");
                List<UserInteractionsEntity> suList = userInteractionsService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (UserInteractionsEntity su : suList) {
                    countMap.put(su.getRefid(), countMap.getOrDefault(su.getRefid(), 0) + 1);
                }
                final Map<Long, Integer> thumbsCount = countMap;
                all.sort(Comparator.comparing((WaiguomeishiEntity d) -> thumbsCount.getOrDefault(d.getId(), 0)).reversed());
                for (WaiguomeishiEntity d : all) {
                    d.setThumbsupnum(thumbsCount.getOrDefault(d.getId(), 0));
                }
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<WaiguomeishiEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            fillRecipeUser(pageList);
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        }
        EntityWrapper<WaiguomeishiEntity> ew = new EntityWrapper<WaiguomeishiEntity>();
        // 显式按 菜品名称/材料/菜系 模糊筛选，确保三种搜索都生效
        if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
            ew.like("caipinmingcheng", params.get("caipinmingcheng").toString().replace("%", "").trim());
            waiguomeishi.setCaipinmingcheng(null);
        }
        if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
            ew.like("cailiao", params.get("cailiao").toString().replace("%", "").trim());
            waiguomeishi.setCailiao(null);
        }
        if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
            ew.like("caixi", params.get("caixi").toString().replace("%", "").trim());
            waiguomeishi.setCaixi(null);
        }
		PageUtils page = waiguomeishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, waiguomeishi), params), params));
        for(Object obj : page.getList()) {
            if(obj instanceof WaiguomeishiEntity) {
                WaiguomeishiEntity entity = (WaiguomeishiEntity)obj;
                fillRecipeUser(entity);
                if (entity.getId() != null) {
                    entity.setThumbsupnum(countAction(entity.getId(), "21"));
                    entity.setUserInteractionsNum(countAction(entity.getId(), "1"));
                }
            }
        }
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( WaiguomeishiEntity waiguomeishi){
    	waiguomeishi.setRecipetype("waiguomeishi");
       	EntityWrapper<WaiguomeishiEntity> ew = new EntityWrapper<WaiguomeishiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( waiguomeishi, "waiguomeishi"));
        List<WaiguomeishiView> vlist = waiguomeishiService.selectListView(ew);
        fillRecipeUser(vlist);
        return R.ok().put("data", vlist);
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(WaiguomeishiEntity waiguomeishi){
    	waiguomeishi.setRecipetype("waiguomeishi");
        EntityWrapper< WaiguomeishiEntity> ew = new EntityWrapper< WaiguomeishiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( waiguomeishi, "waiguomeishi")); 
		WaiguomeishiView waiguomeishiView =  waiguomeishiService.selectView(ew);
		fillRecipeUser(waiguomeishiView);
		return R.ok("查询外国美食成功").put("data", waiguomeishiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WaiguomeishiEntity waiguomeishi = waiguomeishiService.selectById(id);
        fillRecipeUser(waiguomeishi);
		waiguomeishi.setClicknum(waiguomeishi.getClicknum()+1);
		waiguomeishiService.updateById(waiguomeishi);
        waiguomeishi.setThumbsupnum(countAction(id, "21"));
        waiguomeishi.setUserInteractionsNum(countAction(id, "1"));
        return R.ok().put("data", waiguomeishi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        WaiguomeishiEntity waiguomeishi = waiguomeishiService.selectById(id);
        fillRecipeUser(waiguomeishi);
		waiguomeishi.setClicknum(waiguomeishi.getClicknum()+1);
		waiguomeishiService.updateById(waiguomeishi);
        waiguomeishi.setThumbsupnum(countAction(id, "21"));
        waiguomeishi.setUserInteractionsNum(countAction(id, "1"));
        return R.ok().put("data", waiguomeishi);
    }
    


    /**
     * 赞
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id,String type){
        // 点赞行为统一以 user_interactions(type=21) 为唯一事实源，不再写入主表 thumbsupnum
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WaiguomeishiEntity waiguomeishi, HttpServletRequest request){
    	waiguomeishi.setRecipetype("waiguomeishi");
    	waiguomeishi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        // 记录发布用户id
        Long userId = (Long)request.getSession().getAttribute("userId");
        if(userId != null) {
            waiguomeishi.setUserid(userId);
        }
        // 兼容：如果前端没传 addtime，则用当前时间兜底
        if (waiguomeishi.getAddtime() == null) {
            waiguomeishi.setAddtime(new Date());
        }
    	//ValidatorUtils.validateEntity(waiguomeishi);
        waiguomeishiService.insert(waiguomeishi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
	@IgnoreAuth
    @RequestMapping("/add")
    public R add(@RequestBody WaiguomeishiEntity waiguomeishi, HttpServletRequest request){
    	waiguomeishi.setRecipetype("waiguomeishi");
    	waiguomeishi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Long userId = (Long)request.getSession().getAttribute("userId");
        if(userId != null) {
            waiguomeishi.setUserid(userId);
        }
        // 兼容：如果前端没传 addtime，则用当前时间兜底
        if (waiguomeishi.getAddtime() == null) {
            waiguomeishi.setAddtime(new Date());
        }
    	//ValidatorUtils.validateEntity(waiguomeishi);
        waiguomeishiService.insert(waiguomeishi);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WaiguomeishiEntity waiguomeishi, HttpServletRequest request){
    	waiguomeishi.setRecipetype("waiguomeishi");
        //ValidatorUtils.validateEntity(waiguomeishi);
        waiguomeishiService.updateById(waiguomeishi);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        waiguomeishiService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<WaiguomeishiEntity> wrapper = new EntityWrapper<WaiguomeishiEntity>();
		wrapper.eq("recipetype", "waiguomeishi");
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("user")) {
			wrapper.eq("userid", (Long)request.getSession().getAttribute("userId"));
		}

		int count = waiguomeishiService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}
