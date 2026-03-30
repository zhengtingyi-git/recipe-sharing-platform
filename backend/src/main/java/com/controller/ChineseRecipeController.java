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

import com.entity.ChineseRecipeEntity;
import com.entity.view.ChineseRecipeView;

import com.service.ChineseRecipeService;
import com.service.UserService;
import com.service.TokenService;
import com.entity.UserEntity;
import com.entity.TokenEntity;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.utils.RecipeAuditStatus;
import com.service.UserInteractionsService;
import com.entity.UserInteractionsEntity;

/**
 * 中式美食
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@RestController
@RequestMapping("/chinese_recipe")
public class ChineseRecipeController {
    @Autowired
    private ChineseRecipeService chinese_recipeService;

    @Autowired
    private UserInteractionsService userInteractionsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 后台多用 session；前台发布接口带 @IgnoreAuth 时拦截器不写 session，需从 Token 头解析 JWT。
     */
    private Long resolveRequestUserId(HttpServletRequest request) {
        Object uidObj = request.getSession().getAttribute("userId");
        if (uidObj != null) {
            if (uidObj instanceof Long) {
                return (Long) uidObj;
            }
            if (uidObj instanceof Integer) {
                return ((Integer) uidObj).longValue();
            }
            try {
                return Long.valueOf(uidObj.toString());
            } catch (Exception e) {
                return null;
            }
        }
        String token = request.getHeader("Token");
        if (token != null && !token.trim().isEmpty()) {
            TokenEntity te = tokenService.getTokenEntity(token.trim());
            if (te != null && te.getUserId() != null) {
                return te.getUserId();
            }
        }
        return null;
    }

    private int countAction(Long resourceId, String type) {
        EntityWrapper<UserInteractionsEntity> ew = new EntityWrapper<>();
        ew.eq("resource_id", resourceId);
        if ("0".equals(type)) {
            ew.in("interaction_type", Arrays.asList("0", "21"));
        } else {
            ew.eq("interaction_type", type);
        }
        return userInteractionsService.selectCount(ew);
    }

    /** recipe 表不再存用户账号/昵称，按 userid 回填供接口展示 */
    private void fillRecipeUser(ChineseRecipeEntity entity) {
        if (entity == null || entity.getUserId() == null) {
            return;
        }
        UserEntity user = userService.selectById(entity.getUserId());
        if (user != null) {
            entity.setYonghuzhanghao(user.getYonghuzhanghao());
            entity.setYonghuxingming(user.getYonghuxingming());
            entity.setTouxiang(user.getTouxiang());
        }
    }

    private void fillRecipeUser(Iterable<?> list) {
        for (Object obj : list) {
            if (obj instanceof ChineseRecipeEntity) {
                fillRecipeUser((ChineseRecipeEntity) obj);
            }
        }
    }



    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ChineseRecipeEntity chinese_recipe,
		HttpServletRequest request){
		chinese_recipe.setSourceType("chinese_recipe");
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("user")) {
			chinese_recipe.setUserId((Long)request.getSession().getAttribute("userId"));
		}
		// 排序列名映射（兼容旧前端键）
		if (params.get("sort") != null && "addtime".equals(params.get("sort").toString())) {
			params.put("sort", "created_at");
		}
		if (params.get("sort") != null && "createdAt".equals(params.get("sort").toString())) {
			params.put("sort", "created_at");
		}
		if (params.get("sort") != null && "clicknum".equals(params.get("sort").toString())) {
			params.put("sort", "view_count");
		}
        EntityWrapper<ChineseRecipeEntity> ew = new EntityWrapper<ChineseRecipeEntity>();
		PageUtils page = chinese_recipeService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chinese_recipe), params), params));

        // 根据用户id回填当前账号和昵称
        for(Object obj : page.getList()) {
            if(obj instanceof ChineseRecipeEntity) {
                ChineseRecipeEntity entity = (ChineseRecipeEntity)obj;
                fillRecipeUser(entity);
                if (entity.getId() != null) {
                    entity.setThumbsupnum(countAction(entity.getId(), "0"));
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
    public R list(@RequestParam Map<String, Object> params, ChineseRecipeEntity chinese_recipe,
		HttpServletRequest request){
		chinese_recipe.setSourceType("chinese_recipe");
		// 语义化查询参数：authorId / approved
		// - authorId: 发布者用户ID（等价于 userId/userid）
		// - approved: true 表示仅返回审核通过（audit_status=1）
		Object authorId = params.get("authorId");
		if (authorId != null && chinese_recipe.getUserId() == null) {
			try {
				chinese_recipe.setUserId(Long.valueOf(authorId.toString()));
			} catch (Exception ignore) {}
		}
		Object approved = params.get("approved");
		if (approved != null && chinese_recipe.getAuditStatus() == null) {
			String v = approved.toString().trim().toLowerCase();
			if ("true".equals(v) || "1".equals(v) || "yes".equals(v) || "y".equals(v)) {
				chinese_recipe.setAuditStatus(RecipeAuditStatus.APPROVED);
			}
		}
		// 防止 MPUtil 将语义化参数误当作列名参与构造
		params.remove("authorId");
		params.remove("approved");
		// 公开列表仅展示审核通过：不依赖前端是否传 auditStatus（旧版传 sfsh 无法绑定到实体导致未审核数据全部露出）
		chinese_recipe.setAuditStatus(RecipeAuditStatus.APPROVED);
        String sort = params.get("sort") != null ? params.get("sort").toString() : "";
        // 排序列名映射（兼容旧前端键）
        if ("addtime".equals(sort)) {
            params.put("sort", "created_at");
            sort = "created_at";
        }
        if ("createdAt".equals(sort)) {
            params.put("sort", "created_at");
            sort = "created_at";
        }
        if ("clicknum".equals(sort)) {
            params.put("sort", "view_count");
            sort = "view_count";
        }
        if ("userInteractionsNum".equals(sort)) {
            EntityWrapper<ChineseRecipeEntity> ew = new EntityWrapper<>();
            if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
                ew.like("dish_name", params.get("caipinmingcheng").toString().replace("%", "").trim());
                chinese_recipe.setCaipinmingcheng(null);
            }
            if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
                ew.like("ingredients", params.get("cailiao").toString().replace("%", "").trim());
                chinese_recipe.setCailiao(null);
            }
            if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
                ew.like("cuisine", params.get("caixi").toString().replace("%", "").trim());
                chinese_recipe.setCaixi(null);
            }
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<ChineseRecipeEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chinese_recipe), paramsNoSort), paramsNoSort);
            List<ChineseRecipeEntity> all = chinese_recipeService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(ChineseRecipeEntity::getId).collect(Collectors.toList());
                EntityWrapper<UserInteractionsEntity> suEw = new EntityWrapper<>();
                suEw.in("resource_id", ids).eq("interaction_type", "1");
                List<UserInteractionsEntity> suList = userInteractionsService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (UserInteractionsEntity su : suList) {
                    countMap.put(su.getResourceId(), countMap.getOrDefault(su.getResourceId(), 0) + 1);
                }
                final Map<Long, Integer> userInteractionsCount = countMap;
                all.sort(Comparator.comparing((ChineseRecipeEntity d) -> userInteractionsCount.getOrDefault(d.getId(), 0)).reversed());
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<ChineseRecipeEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            fillRecipeUser(pageList);
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        } else if ("thumbsupnum".equals(sort)) {
            EntityWrapper<ChineseRecipeEntity> ew = new EntityWrapper<>();
            if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
                ew.like("dish_name", params.get("caipinmingcheng").toString().replace("%", "").trim());
                chinese_recipe.setCaipinmingcheng(null);
            }
            if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
                ew.like("ingredients", params.get("cailiao").toString().replace("%", "").trim());
                chinese_recipe.setCailiao(null);
            }
            if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
                ew.like("cuisine", params.get("caixi").toString().replace("%", "").trim());
                chinese_recipe.setCaixi(null);
            }
            Map<String, Object> paramsNoSort = new HashMap<>(params);
            paramsNoSort.remove("sort");
            paramsNoSort.remove("order");
            Wrapper<ChineseRecipeEntity> wrapper = MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chinese_recipe), paramsNoSort), paramsNoSort);
            List<ChineseRecipeEntity> all = chinese_recipeService.selectList(wrapper);
            if (!all.isEmpty()) {
                List<Long> ids = all.stream().map(ChineseRecipeEntity::getId).collect(Collectors.toList());
                EntityWrapper<UserInteractionsEntity> suEw = new EntityWrapper<>();
                suEw.in("resource_id", ids).in("interaction_type", Arrays.asList("0", "21"));
                List<UserInteractionsEntity> suList = userInteractionsService.selectList(suEw);
                Map<Long, Integer> countMap = new HashMap<>();
                for (UserInteractionsEntity su : suList) {
                    countMap.put(su.getResourceId(), countMap.getOrDefault(su.getResourceId(), 0) + 1);
                }
                final Map<Long, Integer> thumbsCount = countMap;
                all.sort(Comparator.comparing((ChineseRecipeEntity d) -> thumbsCount.getOrDefault(d.getId(), 0)).reversed());
                for (ChineseRecipeEntity d : all) {
                    d.setThumbsupnum(thumbsCount.getOrDefault(d.getId(), 0));
                }
            }
            int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
            int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
            int from = (pageNum - 1) * limitNum;
            int to = Math.min(from + limitNum, all.size());
            List<ChineseRecipeEntity> pageList = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            fillRecipeUser(pageList);
            PageUtils page = new PageUtils(pageList, all.size(), limitNum, pageNum);
            return R.ok().put("data", page);
        }
        EntityWrapper<ChineseRecipeEntity> ew = new EntityWrapper<ChineseRecipeEntity>();
        // 显式按 菜品名称/材料/菜系 模糊筛选，确保三种搜索都生效
        if (params.get("caipinmingcheng") != null && !params.get("caipinmingcheng").toString().trim().isEmpty()) {
            ew.like("dish_name", params.get("caipinmingcheng").toString().replace("%", "").trim());
            chinese_recipe.setCaipinmingcheng(null);
        }
        if (params.get("cailiao") != null && !params.get("cailiao").toString().trim().isEmpty()) {
            ew.like("ingredients", params.get("cailiao").toString().replace("%", "").trim());
            chinese_recipe.setCailiao(null);
        }
        if (params.get("caixi") != null && !params.get("caixi").toString().trim().isEmpty()) {
            ew.like("cuisine", params.get("caixi").toString().replace("%", "").trim());
            chinese_recipe.setCaixi(null);
        }
		PageUtils page = chinese_recipeService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chinese_recipe), params), params));
        for(Object obj : page.getList()) {
            if(obj instanceof ChineseRecipeEntity) {
                ChineseRecipeEntity entity = (ChineseRecipeEntity)obj;
                fillRecipeUser(entity);
                if (entity.getId() != null) {
                    entity.setThumbsupnum(countAction(entity.getId(), "0"));
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
    public R list( ChineseRecipeEntity chinese_recipe){
    	chinese_recipe.setSourceType("chinese_recipe");
       	EntityWrapper<ChineseRecipeEntity> ew = new EntityWrapper<ChineseRecipeEntity>();
      	ew.allEq(MPUtil.allEQMapPre( chinese_recipe, "chinese_recipe"));
        List<ChineseRecipeView> vlist = chinese_recipeService.selectListView(ew);
        fillRecipeUser(vlist);
        return R.ok().put("data", vlist);
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ChineseRecipeEntity chinese_recipe){
    	chinese_recipe.setSourceType("chinese_recipe");
        EntityWrapper< ChineseRecipeEntity> ew = new EntityWrapper< ChineseRecipeEntity>();
 		ew.allEq(MPUtil.allEQMapPre( chinese_recipe, "chinese_recipe")); 
		ChineseRecipeView chinese_recipeView =  chinese_recipeService.selectView(ew);
		fillRecipeUser(chinese_recipeView);
		return R.ok("查询中式美食成功").put("data", chinese_recipeView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ChineseRecipeEntity chinese_recipe = chinese_recipeService.selectById(id);
        fillRecipeUser(chinese_recipe);
		chinese_recipe.setViewCount(chinese_recipe.getViewCount()+1);
		chinese_recipeService.updateById(chinese_recipe);
        chinese_recipe.setThumbsupnum(countAction(id, "0"));
        chinese_recipe.setUserInteractionsNum(countAction(id, "1"));
        return R.ok().put("data", chinese_recipe);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        ChineseRecipeEntity chinese_recipe = chinese_recipeService.selectById(id);
        if (chinese_recipe == null) {
        	return R.error("记录不存在");
        }
        if (!"chinese_recipe".equals(chinese_recipe.getSourceType())) {
        	return R.error("记录不存在");
        }
        if (!RecipeAuditStatus.isApproved(chinese_recipe.getAuditStatus())) {
        	Object uid = request.getSession().getAttribute("userId");
        	Long ownerId = chinese_recipe.getUserId();
        	boolean owner = uid != null && ownerId != null && uid.toString().equals(String.valueOf(ownerId));
        	if (!owner) {
        		// 不用 403：前台 http 模块会把 403 当作需登录并跳转登录页
        		return R.error("内容未通过审核或不存在");
        	}
        }
        fillRecipeUser(chinese_recipe);
        int vc = chinese_recipe.getViewCount() != null ? chinese_recipe.getViewCount() : 0;
		chinese_recipe.setViewCount(vc + 1);
		chinese_recipeService.updateById(chinese_recipe);
        chinese_recipe.setThumbsupnum(countAction(id, "0"));
        chinese_recipe.setUserInteractionsNum(countAction(id, "1"));
        return R.ok().put("data", chinese_recipe);
    }
    


    /**
     * 赞
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id,String type){
        // 点赞行为统一以 user_interactions(type=0) 为唯一事实源，不再写入主表 thumbsupnum
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ChineseRecipeEntity chinese_recipe, HttpServletRequest request){
    	chinese_recipe.setSourceType("chinese_recipe");
    	chinese_recipe.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Long userId = resolveRequestUserId(request);
        if (userId == null) {
            return R.error("请先登录");
        }
        chinese_recipe.setUserId(userId);
        // 兼容：如果前端没传 addtime，则用当前时间兜底
        if (chinese_recipe.getCreatedAt() == null) {
            chinese_recipe.setCreatedAt(new Date());
        }
        if (chinese_recipe.getAuditStatus() == null || chinese_recipe.getAuditStatus().trim().isEmpty()) {
            chinese_recipe.setAuditStatus(RecipeAuditStatus.PENDING);
        }
    	//ValidatorUtils.validateEntity(chinese_recipe);
        chinese_recipeService.insert(chinese_recipe);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
	@IgnoreAuth
    @RequestMapping("/add")
    public R add(@RequestBody ChineseRecipeEntity chinese_recipe, HttpServletRequest request){
    	chinese_recipe.setSourceType("chinese_recipe");
    	chinese_recipe.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        Long userId = resolveRequestUserId(request);
        if (userId == null) {
            return R.error("请先登录");
        }
        chinese_recipe.setUserId(userId);
        // 兼容：如果前端没传 addtime，则用当前时间兜底
        if (chinese_recipe.getCreatedAt() == null) {
            chinese_recipe.setCreatedAt(new Date());
        }
        chinese_recipe.setAuditStatus(RecipeAuditStatus.PENDING);
    	//ValidatorUtils.validateEntity(chinese_recipe);
        chinese_recipeService.insert(chinese_recipe);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ChineseRecipeEntity chinese_recipe, HttpServletRequest request){
    	chinese_recipe.setSourceType("chinese_recipe");
        //ValidatorUtils.validateEntity(chinese_recipe);
        chinese_recipeService.updateById(chinese_recipe);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        chinese_recipeService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<ChineseRecipeEntity> wrapper = new EntityWrapper<ChineseRecipeEntity>();
		wrapper.eq("source_type", "chinese_recipe");
		String mappedColumn = mapRecipeColumnName(columnName);
		if(map.get("remindstart")!=null) {
			wrapper.ge(mappedColumn, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(mappedColumn, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("user")) {
			wrapper.eq("user_id", (Long)request.getSession().getAttribute("userId"));
		}

		int count = chinese_recipeService.selectCount(wrapper);
		return R.ok().put("count", count);
	}

	private String mapRecipeColumnName(String columnName) {
		if (columnName == null) return null;
		switch (columnName) {
			case "addtime":
				return "created_at";
			case "userid":
				return "user_id";
			case "auditStatus":
				return "audit_status";
			case "auditReply":
				return "audit_reply";
			case "recipetype":
				return "source_type";
			case "clicknum":
				return "view_count";
			case "caipinmingcheng":
				return "dish_name";
			case "caixi":
				return "cuisine";
			case "tupian":
				return "image";
			case "caipinleixing":
				return "dish_type";
			case "cailiao":
				return "ingredients";
			case "pengrenfangfa":
				return "cooking_method";
			default:
				return columnName;
		}
	}
	







}

