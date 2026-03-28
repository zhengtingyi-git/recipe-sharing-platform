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

import com.entity.UserInteractionsEntity;
import com.entity.ForumPostEntity;
import com.entity.ChineseRecipeEntity;
import com.entity.ForeignRecipeEntity;
import com.entity.view.UserInteractionsView;

import com.service.UserInteractionsService;
import com.service.ForumPostService;
import com.service.ChineseRecipeService;
import com.service.ForeignRecipeService;
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
@RequestMapping("/user-interactions")
public class UserInteractionsController {
    @Autowired
    private UserInteractionsService userInteractionsService;
    @Autowired
    private ForumPostService forumPostService;
    @Autowired
    private ChineseRecipeService chinese_recipeService;
    @Autowired
    private ForeignRecipeService foreign_recipeService;


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,UserInteractionsEntity userInteractions,
		HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		userInteractions.setUserId((Long)request.getSession().getAttribute("userId"));
    	}
        // 注意：user_interactions 表已经删除了 tablename/name/picture 等列，
        // 这些字段在实体里仍然存在（exist=false），如果直接参与 likeOrEq，会生成不存在的 where 条件。
        userInteractions.setTablename(null);
        userInteractions.setName(null);
        userInteractions.setPicture(null);
        String categoryTablename = params.get("tablename") != null ? params.get("tablename").toString().trim() : "";
        params.remove("name");
        params.remove("tablename");
        normalizeUserInteractionsParams(params);
        coerceResourceIdAndTypeFromParams(userInteractions, params);

        EntityWrapper<UserInteractionsEntity> ew = new EntityWrapper<UserInteractionsEntity>();
        // 仅对物理列过滤：避免 MPUtil 依据旧字段名生成不存在 where 条件
        if (userInteractions.getId() != null) {
            ew.eq("id", userInteractions.getId());
        }
        if (userInteractions.getUserId() != null) {
            ew.eq("user_id", userInteractions.getUserId());
        }
        if (userInteractions.getResourceId() != null) {
            ew.eq("resource_id", userInteractions.getResourceId());
        }
        if (userInteractions.getType() != null) {
            ew.eq("interaction_type", userInteractions.getType());
        }
        if (userInteractions.getCreatedAt() != null) {
            ew.eq("created_at", userInteractions.getCreatedAt());
        }

        // Between 条件（_start/_end）+ 排序（sort）
        ew = (EntityWrapper<UserInteractionsEntity>) MPUtil.between(ew, params);
		applyUserInteractionsCategoryFilterIfBulkList(ew, categoryTablename, userInteractions.getResourceId());
		ew = (EntityWrapper<UserInteractionsEntity>) MPUtil.sort(ew, params);
		PageUtils page = userInteractionsService.queryPage(params, ew);
        enrichUserInteractionsPage(page);

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,UserInteractionsEntity userInteractions, 
		HttpServletRequest request){
    	if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		userInteractions.setUserId((Long)request.getSession().getAttribute("userId"));
    	}
        // 同 page：避免生成不存在的 where(tablename/name/picture)
        userInteractions.setTablename(null);
        userInteractions.setName(null);
        userInteractions.setPicture(null);
        String categoryTablename = params.get("tablename") != null ? params.get("tablename").toString().trim() : "";
        params.remove("name");
        params.remove("tablename");
        normalizeUserInteractionsParams(params);
        coerceResourceIdAndTypeFromParams(userInteractions, params);

        EntityWrapper<UserInteractionsEntity> ew = new EntityWrapper<UserInteractionsEntity>();
        if (userInteractions.getId() != null) {
            ew.eq("id", userInteractions.getId());
        }
        if (userInteractions.getUserId() != null) {
            ew.eq("user_id", userInteractions.getUserId());
        }
        if (userInteractions.getResourceId() != null) {
            ew.eq("resource_id", userInteractions.getResourceId());
        }
        if (userInteractions.getType() != null) {
            ew.eq("interaction_type", userInteractions.getType());
        }
        if (userInteractions.getCreatedAt() != null) {
            ew.eq("created_at", userInteractions.getCreatedAt());
        }

        ew = (EntityWrapper<UserInteractionsEntity>) MPUtil.between(ew, params);
		applyUserInteractionsCategoryFilterIfBulkList(ew, categoryTablename, userInteractions.getResourceId());
		ew = (EntityWrapper<UserInteractionsEntity>) MPUtil.sort(ew, params);
		PageUtils page = userInteractionsService.queryPage(params, ew);
        enrichUserInteractionsPage(page);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(UserInteractionsEntity userInteractions){
       	EntityWrapper<UserInteractionsEntity> ew = new EntityWrapper<UserInteractionsEntity>();
        if (userInteractions.getId() != null) {
            ew.eq("id", userInteractions.getId());
        }
        if (userInteractions.getUserId() != null) {
            ew.eq("user_id", userInteractions.getUserId());
        }
        if (userInteractions.getResourceId() != null) {
            ew.eq("resource_id", userInteractions.getResourceId());
        }
        if (userInteractions.getType() != null) {
            ew.eq("interaction_type", userInteractions.getType());
        }
        if (userInteractions.getCreatedAt() != null) {
            ew.eq("created_at", userInteractions.getCreatedAt());
        }
        return R.ok().put("data", userInteractionsService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(UserInteractionsEntity userInteractions){
        EntityWrapper< UserInteractionsEntity> ew = new EntityWrapper< UserInteractionsEntity>();
        if (userInteractions.getId() != null) {
            ew.eq("id", userInteractions.getId());
        }
        if (userInteractions.getUserId() != null) {
            ew.eq("user_id", userInteractions.getUserId());
        }
        if (userInteractions.getResourceId() != null) {
            ew.eq("resource_id", userInteractions.getResourceId());
        }
        if (userInteractions.getType() != null) {
            ew.eq("interaction_type", userInteractions.getType());
        }
        if (userInteractions.getCreatedAt() != null) {
            ew.eq("created_at", userInteractions.getCreatedAt());
        }
		UserInteractionsView userInteractionsView = userInteractionsService.selectView(ew);
		return R.ok("查询收藏表成功").put("data", userInteractionsView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        UserInteractionsEntity userInteractions = userInteractionsService.selectById(id);
        return R.ok().put("data", userInteractions);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        UserInteractionsEntity userInteractions = userInteractionsService.selectById(id);
        if (userInteractions != null) {
            fillTargetFields(userInteractions);
        }
        return R.ok().put("data", userInteractions);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UserInteractionsEntity userInteractions, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(userInteractions);
    	Long uid = (Long) request.getSession().getAttribute("userId");
    	if (uid == null) {
    		return R.error("请先登录");
    	}
    	userInteractions.setUserId(uid);
    	normalizeInteractionType(userInteractions);
    	if (userInteractions.getResourceId() == null) {
    		return R.error("缺少 resourceId");
    	}
    	if (userInteractions.getType() != null && !("1".equals(userInteractions.getType()) || "0".equals(userInteractions.getType()))) {
    		return R.error("user-interactions.type 只允许 1(收藏) 或 0(赞)");
    	}
    	if (interactionRowExists(uid, userInteractions.getResourceId(), userInteractions.getType())) {
    		return R.ok();
    	}
    	userInteractions.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        userInteractionsService.insert(userInteractions);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody UserInteractionsEntity userInteractions, HttpServletRequest request){
    	Long uid = (Long) request.getSession().getAttribute("userId");
    	if (uid == null) {
    		return R.error("请先登录");
    	}
    	userInteractions.setUserId(uid);
    	normalizeInteractionType(userInteractions);
    	if (userInteractions.getResourceId() == null) {
    		return R.error("缺少 resourceId");
    	}
    	if (userInteractions.getType() != null && !("1".equals(userInteractions.getType()) || "0".equals(userInteractions.getType()))) {
    		return R.error("user-interactions.type 只允许 1(收藏) 或 0(赞)");
    	}
    	if (interactionRowExists(uid, userInteractions.getResourceId(), userInteractions.getType())) {
    		return R.ok();
    	}
    	userInteractions.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        userInteractionsService.insert(userInteractions);
        return R.ok();
    }

    /** JSON 里 type 可能是数字；赞由 0 表示，历史 21 归一为 0 */
    private static void normalizeInteractionType(UserInteractionsEntity e) {
    	if (e == null || e.getType() == null) {
    		return;
    	}
    	String t = String.valueOf(e.getType()).trim();
    	if ("21".equals(t)) {
    		t = "0";
    	}
    	e.setType(t);
    }

    private boolean interactionRowExists(Long userId, Long resourceId, String interactionType) {
    	if (userId == null || resourceId == null || interactionType == null) {
    		return false;
    	}
    	EntityWrapper<UserInteractionsEntity> w = new EntityWrapper<>();
    	w.eq("user_id", userId);
    	w.eq("resource_id", resourceId);
    	if ("0".equals(interactionType)) {
    		w.in("interaction_type", Arrays.asList("0", "21"));
    	} else {
    		w.eq("interaction_type", interactionType);
    	}
    	return userInteractionsService.selectCount(w) > 0;
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UserInteractionsEntity userInteractions, HttpServletRequest request){
        //ValidatorUtils.validateEntity(userInteractions);
    	normalizeInteractionType(userInteractions);
    	if (userInteractions.getType() != null && !("1".equals(userInteractions.getType()) || "0".equals(userInteractions.getType()))) {
    		return R.error("user-interactions.type 只允许 1(收藏) 或 0(赞)");
    	}
        userInteractionsService.updateById(userInteractions);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        userInteractionsService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		String normalizedColumn = normalizeUserInteractionsColumn(columnName);
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
		
		Wrapper<UserInteractionsEntity> wrapper = new EntityWrapper<UserInteractionsEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(normalizedColumn, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(normalizedColumn, map.get("remindend"));
		}
		if(!request.getSession().getAttribute("role").toString().equals("管理员")) {
    		wrapper.eq("user_id", (Long)request.getSession().getAttribute("userId"));
    	}


		int count = userInteractionsService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







    /**
     * 详情页「是否已赞/已藏」传了 resourceId：再按 tablename 做菜谱子查询可能把当前 id 排除掉（source_type 与库不一致），
     * 因此仅在没有指定具体 resourceId 时（个人中心列表）才做分类筛选。
     */
    private void applyUserInteractionsCategoryFilterIfBulkList(EntityWrapper<UserInteractionsEntity> ew,
                                                               String tablename,
                                                               Long resourceId) {
        if (resourceId != null) {
            return;
        }
        applyUserInteractionsCategoryFilter(ew, tablename);
    }

    /** GET 参数名与实体绑定偶发失败时，从 params 补 resourceId、interaction_type */
    private void coerceResourceIdAndTypeFromParams(UserInteractionsEntity userInteractions, Map<String, Object> params) {
        if (userInteractions == null || params == null) {
            return;
        }
        if (userInteractions.getResourceId() == null && params.get("resourceId") != null) {
            try {
                userInteractions.setResourceId(Long.parseLong(String.valueOf(params.get("resourceId")).trim()));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (userInteractions.getType() == null && params.get("type") != null) {
            userInteractions.setType(String.valueOf(params.get("type")).trim());
        }
    }

    /**
     * 个人中心「我的收藏/我的点赞」按分类筛选：库表无 tablename 列，通过 refid 与 recipe / forum_post 关联。
     * 中式/外国美食共用 recipe 表，以 recipetype 区分。
     */
    private void applyUserInteractionsCategoryFilter(EntityWrapper<UserInteractionsEntity> ew, String tablename) {
        if (ew == null || StringUtils.isBlank(tablename)) {
            return;
        }
        switch (tablename) {
            case "foreign_recipe":
                ew.andNew().where("resource_id IN (SELECT id FROM recipe WHERE source_type = {0})", "foreign_recipe");
                break;
            case "chinese_recipe":
                // 与业务侧一致：中式菜品历史上 source_type 可能为 NULL/空，否则会筛掉记录导致前端误判未收藏再走 save → 唯一键冲突
                ew.andNew().where("resource_id IN (SELECT id FROM recipe WHERE source_type = 'chinese_recipe' OR source_type IS NULL OR source_type = '')");
                break;
            case "forum_post":
                ew.andNew().where("resource_id IN (SELECT id FROM forum_post)");
                break;
            default:
                break;
        }
    }

    private void enrichUserInteractionsPage(PageUtils page) {
        if (page == null || page.getList() == null) {
            return;
        }
        List<?> records = page.getList();
        List<Object> enriched = records.stream().map(item -> {
            if (item instanceof UserInteractionsEntity) {
                UserInteractionsEntity userInteractions = (UserInteractionsEntity) item;
                fillTargetFields(userInteractions);
                return userInteractions;
            }
            return item;
        }).collect(Collectors.toList());
        page.setList(enriched);
    }

    private void fillTargetFields(UserInteractionsEntity userInteractions) {
        if (userInteractions == null || userInteractions.getResourceId() == null) {
            return;
        }
        Long refId = userInteractions.getResourceId();

        ForumPostEntity post = forumPostService.selectById(refId);
        if (post != null) {
            userInteractions.setTablename("forum_post");
            userInteractions.setName(post.getTitle());
            userInteractions.setPicture(post.getPicture());
            return;
        }

        ChineseRecipeEntity zhongshi = chinese_recipeService.selectById(refId);
        if (zhongshi != null) {
            userInteractions.setTablename("chinese_recipe");
            userInteractions.setName(zhongshi.getCaipinmingcheng());
            userInteractions.setPicture(zhongshi.getTupian());
            return;
        }

        ForeignRecipeEntity waiguo = foreign_recipeService.selectById(refId);
        if (waiguo != null) {
            userInteractions.setTablename("foreign_recipe");
            userInteractions.setName(waiguo.getCaipinmingcheng());
            userInteractions.setPicture(waiguo.getTupian());
        }
    }

    /**
     * 规范化 Storeup（物理列：user_id/resource_id/interaction_type/created_at）相关参数。
     * 目的：让 MPUtil.between / sort 使用正确的“物理列名”。
     */
    private void normalizeUserInteractionsParams(Map<String, Object> params) {
        if (params == null) {
            return;
        }

        // sort 是“列名”，直接规范列名
        Object sortObj = params.get("sort");
        if (sortObj != null) {
            String sort = String.valueOf(sortObj).trim();
            if (!sort.isEmpty()) {
                params.put("sort", normalizeUserInteractionsColumn(sort));
            }
        }

        // between 范围用 xxx_start / xxx_end 作为 key
        Map<String, Object> normalized = new HashMap<>();
        for (Map.Entry<String, Object> e : params.entrySet()) {
            String key = e.getKey();
            Object val = e.getValue();
            if (key == null) continue;

            if (key.endsWith("_start") || key.endsWith("_end")) {
                String suffix = key.endsWith("_start") ? "_start" : "_end";
                String prefix = key.substring(0, key.length() - suffix.length());
                String newPrefix = normalizeUserInteractionsColumn(prefix);
                normalized.put(newPrefix + suffix, val);
            } else {
                normalized.put(key, val);
            }
        }
        params.clear();
        params.putAll(normalized);
    }

    private String normalizeUserInteractionsColumn(String columnName) {
        if (columnName == null) {
            return "";
        }
        String c = columnName.trim();
        switch (c) {
            case "userid":
            case "userId":
                return "user_id";
            case "refid":
            case "resourceId":
                return "resource_id";
            case "type":
                return "interaction_type";
            case "addtime":
            case "createdAt":
                return "created_at";
            default:
                return c;
        }
    }



}

