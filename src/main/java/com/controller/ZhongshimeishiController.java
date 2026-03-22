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

import com.entity.ZhongshimeishiEntity;
import com.entity.view.ZhongshimeishiView;

import com.service.ZhongshimeishiService;
import com.service.UserService;
import com.entity.UserEntity;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.service.StoreupService;
import com.entity.StoreupEntity;

/**
 * 中式美食
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/zhongshimeishi")
public class ZhongshimeishiController {
    @Autowired
    private ZhongshimeishiService zhongshimeishiService;

    @Autowired
    private StoreupService storeupService;

    @Autowired
    private UserService userService;

    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ZhongshimeishiEntity zhongshimeishi,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("user")) {
			zhongshimeishi.setUserid((Long)request.getSession().getAttribute("userId"));
		}
        EntityWrapper<ZhongshimeishiEntity> ew = new EntityWrapper<ZhongshimeishiEntity>();
		PageUtils page = zhongshimeishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhongshimeishi), params), params));

        // 根据用户id回填当前账号和昵称
        for(Object obj : page.getList()) {
            if(obj instanceof ZhongshimeishiEntity) {
                ZhongshimeishiEntity entity = (ZhongshimeishiEntity)obj;
                if(entity.getUserid() != null) {
                    UserEntity user = userService.selectById(entity.getUserid());
                    if(user != null) {
                        entity.setYonghuzhanghao(user.getYonghuzhanghao());
                        entity.setYonghuxingming(user.getYonghuxingming());
                    }
                }
            }
        }

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表（支持 sort=addtime/thumbsupnum/storeupnum，order=desc）
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ZhongshimeishiEntity zhongshimeishi,
		HttpServletRequest request){
        String sort = params.get("sort") != null ? params.get("sort").toString() : "";
        if ("storeupnum".equals(sort)) {
            EntityWrapper<ZhongshimeishiEntity> ew = new EntityWrapper<>();
            if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
                ew.like("caipinmingcheng", params.get("caipinmingcheng").toString().replace("%", "").trim());
                zhongshimeishi.setCaipinmingcheng(null);
            }
            if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
                ew.like("cailiao", params.get("cailiao").toString().replace("%", "").trim());
                zhongshimeishi.setCailiao(null);
            }
            if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
                ew.like("caixi", params.get("caixi").toString().replace("%", "").trim());
                zhongshimeishi.setCaixi(null);
            }
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<ZhongshimeishiEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhongshimeishi), paramsNoSort), paramsNoSort);
            List<ZhongshimeishiEntity> all = zhongshimeishiService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(ZhongshimeishiEntity::getId).collect(Collectors.toList());
                EntityWrapper<StoreupEntity> suEw = new EntityWrapper<>();
                suEw.eq("tablename", "zhongshimeishi").in("refid", ids).eq("type", "1");
                List<StoreupEntity> suList = storeupService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (StoreupEntity su : suList) {
                    countMap.put(su.getRefid(), countMap.getOrDefault(su.getRefid(), 0) + 1);
                }
                final Map<Long, Integer> storeupCount = countMap;
                all.sort(Comparator.comparing((ZhongshimeishiEntity d) -> storeupCount.getOrDefault(d.getId(), 0)).reversed());
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<ZhongshimeishiEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        }
        EntityWrapper<ZhongshimeishiEntity> ew = new EntityWrapper<ZhongshimeishiEntity>();
        // 显式按 菜品名称/材料/菜系 模糊筛选，确保三种搜索都生效
        if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
            ew.like("caipinmingcheng", params.get("caipinmingcheng").toString().replace("%", "").trim());
            zhongshimeishi.setCaipinmingcheng(null);
        }
        if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
            ew.like("cailiao", params.get("cailiao").toString().replace("%", "").trim());
            zhongshimeishi.setCailiao(null);
        }
        if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
            ew.like("caixi", params.get("caixi").toString().replace("%", "").trim());
            zhongshimeishi.setCaixi(null);
        }
		PageUtils page = zhongshimeishiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhongshimeishi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ZhongshimeishiEntity zhongshimeishi){
       	EntityWrapper<ZhongshimeishiEntity> ew = new EntityWrapper<ZhongshimeishiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( zhongshimeishi, "zhongshimeishi")); 
        return R.ok().put("data", zhongshimeishiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ZhongshimeishiEntity zhongshimeishi){
        EntityWrapper< ZhongshimeishiEntity> ew = new EntityWrapper< ZhongshimeishiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( zhongshimeishi, "zhongshimeishi")); 
		ZhongshimeishiView zhongshimeishiView =  zhongshimeishiService.selectView(ew);
		return R.ok("查询中式美食成功").put("data", zhongshimeishiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ZhongshimeishiEntity zhongshimeishi = zhongshimeishiService.selectById(id);
        if(zhongshimeishi.getUserid() != null) {
            UserEntity user = userService.selectById(zhongshimeishi.getUserid());
            if(user != null) {
                zhongshimeishi.setYonghuzhanghao(user.getYonghuzhanghao());
                zhongshimeishi.setYonghuxingming(user.getYonghuxingming());
                zhongshimeishi.setTouxiang(user.getTouxiang());
            }
        }
		zhongshimeishi.setClicknum(zhongshimeishi.getClicknum()+1);
		zhongshimeishiService.updateById(zhongshimeishi);
        return R.ok().put("data", zhongshimeishi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ZhongshimeishiEntity zhongshimeishi = zhongshimeishiService.selectById(id);
        if(zhongshimeishi.getUserid() != null) {
            UserEntity user = userService.selectById(zhongshimeishi.getUserid());
            if(user != null) {
                zhongshimeishi.setYonghuzhanghao(user.getYonghuzhanghao());
                zhongshimeishi.setYonghuxingming(user.getYonghuxingming());
                zhongshimeishi.setTouxiang(user.getTouxiang());
            }
        }
		zhongshimeishi.setClicknum(zhongshimeishi.getClicknum()+1);
		zhongshimeishiService.updateById(zhongshimeishi);
        return R.ok().put("data", zhongshimeishi);
    }
    


    /**
     * 赞
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id,String type){
        ZhongshimeishiEntity zhongshimeishi = zhongshimeishiService.selectById(id);
        if(type.equals("1")) {
        	zhongshimeishi.setThumbsupnum(zhongshimeishi.getThumbsupnum()+1);
        }
        zhongshimeishiService.updateById(zhongshimeishi);
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ZhongshimeishiEntity zhongshimeishi, HttpServletRequest request){
    	zhongshimeishi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Long userId = (Long)request.getSession().getAttribute("userId");
        if(userId != null) {
            zhongshimeishi.setUserid(userId);
        }
    	//ValidatorUtils.validateEntity(zhongshimeishi);
        zhongshimeishiService.insert(zhongshimeishi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
	@IgnoreAuth
    @RequestMapping("/add")
    public R add(@RequestBody ZhongshimeishiEntity zhongshimeishi, HttpServletRequest request){
    	zhongshimeishi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Long userId = (Long)request.getSession().getAttribute("userId");
        if(userId != null) {
            zhongshimeishi.setUserid(userId);
        }
    	//ValidatorUtils.validateEntity(zhongshimeishi);
        zhongshimeishiService.insert(zhongshimeishi);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ZhongshimeishiEntity zhongshimeishi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(zhongshimeishi);
        zhongshimeishiService.updateById(zhongshimeishi);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        zhongshimeishiService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<ZhongshimeishiEntity> wrapper = new EntityWrapper<ZhongshimeishiEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("user")) {
			wrapper.eq("yonghuzhanghao", (String)request.getSession().getAttribute("username"));
		}

		int count = zhongshimeishiService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}
