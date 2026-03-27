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

import com.entity.ChineseRecipeCommentEntity;
import com.entity.view.ChineseRecipeCommentView;
import com.entity.UserEntity;
import com.service.UserService;
import com.service.ChineseRecipeCommentService;
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
@RequestMapping("/chinese_recipe_comment")
public class ChineseRecipeCommentController {
    @Autowired
    private ChineseRecipeCommentService chinese_recipe_commentService;
    @Autowired
    private UserService userService;


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ChineseRecipeCommentEntity chinese_recipe_comment,
		HttpServletRequest request){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
        EntityWrapper<ChineseRecipeCommentEntity> ew = new EntityWrapper<ChineseRecipeCommentEntity>();
		PageUtils page = chinese_recipe_commentService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chinese_recipe_comment), params), params));
        // 后台已关闭“回复”功能：管理员页不再展示回复内容
        if ("管理员".equals(String.valueOf(request.getSession().getAttribute("role")))) {
            List<?> rows = page.getList();
            if (rows != null) {
                for (Object row : rows) {
                    if (row instanceof ChineseRecipeCommentEntity) {
                        ((ChineseRecipeCommentEntity) row).setReply("");
                    }
                }
            }
        }

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ChineseRecipeCommentEntity chinese_recipe_comment, 
		HttpServletRequest request){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
        EntityWrapper<ChineseRecipeCommentEntity> ew = new EntityWrapper<ChineseRecipeCommentEntity>();
		PageUtils page = chinese_recipe_commentService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chinese_recipe_comment), params), params));
        List<ChineseRecipeCommentEntity> records = (List<ChineseRecipeCommentEntity>) page.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ChineseRecipeCommentEntity e : records) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("recipeId", e.getRecipeId());
            m.put("userId", e.getUserId());
            m.put("nickname", e.getNickname());
            m.put("content", e.getContent());
            m.put("reply", e.getReply());
            m.put("createdAt", e.getCreatedAt());
            m.put("avatar", "");
            if (e.getUserId() != null) {
                UserEntity y = userService.selectById(e.getUserId());
                if (y != null) {
                    if (y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                        m.put("nickname", y.getYonghuxingming());
                    }
                    if (y.getTouxiang() != null) {
                        m.put("avatar", y.getTouxiang());
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
    public R list( ChineseRecipeCommentEntity chinese_recipe_comment){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
       	EntityWrapper<ChineseRecipeCommentEntity> ew = new EntityWrapper<ChineseRecipeCommentEntity>();
      	ew.allEq(MPUtil.allEQMapPre( chinese_recipe_comment, "chinese_recipe_comment")); 
        return R.ok().put("data", chinese_recipe_commentService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ChineseRecipeCommentEntity chinese_recipe_comment){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
        EntityWrapper< ChineseRecipeCommentEntity> ew = new EntityWrapper< ChineseRecipeCommentEntity>();
 		ew.allEq(MPUtil.allEQMapPre( chinese_recipe_comment, "chinese_recipe_comment")); 
		ChineseRecipeCommentView chinese_recipe_commentView =  chinese_recipe_commentService.selectView(ew);
		return R.ok("查询中式美食评论表成功").put("data", chinese_recipe_commentView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ChineseRecipeCommentEntity chinese_recipe_comment = chinese_recipe_commentService.selectById(id);
        return R.ok().put("data", chinese_recipe_comment);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ChineseRecipeCommentEntity chinese_recipe_comment = chinese_recipe_commentService.selectById(id);
        return R.ok().put("data", chinese_recipe_comment);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ChineseRecipeCommentEntity chinese_recipe_comment, HttpServletRequest request){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
    	chinese_recipe_comment.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	if (chinese_recipe_comment.getUserId() != null) {
            UserEntity y = userService.selectById(chinese_recipe_comment.getUserId());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                chinese_recipe_comment.setNickname(y.getYonghuxingming());
            }
        }
        chinese_recipe_commentService.insert(chinese_recipe_comment);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ChineseRecipeCommentEntity chinese_recipe_comment, HttpServletRequest request){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
    	chinese_recipe_comment.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	if (chinese_recipe_comment.getUserId() != null) {
            UserEntity y = userService.selectById(chinese_recipe_comment.getUserId());
            if (y != null && y.getYonghuxingming() != null && !y.getYonghuxingming().isEmpty()) {
                chinese_recipe_comment.setNickname(y.getYonghuxingming());
            }
        }
        chinese_recipe_commentService.insert(chinese_recipe_comment);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ChineseRecipeCommentEntity chinese_recipe_comment, HttpServletRequest request){
    	chinese_recipe_comment.setSourceType("chinese_recipe");
        //ValidatorUtils.validateEntity(chinese_recipe_comment);
        chinese_recipe_commentService.updateById(chinese_recipe_comment);//全部更新
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
            chinese_recipe_commentService.deleteBatchIds(Arrays.asList(ids));
            return R.ok();
        }

        // 普通用户：只能删除自己的评论
        if (userId == null) {
            return R.error(401, "请先登录");
        }

        List<Long> canDeleteIds = new ArrayList<>();
        for (Long id : ids) {
            ChineseRecipeCommentEntity entity = chinese_recipe_commentService.selectById(id);
            if (entity != null && entity.getUserId() != null && entity.getUserId().longValue() == userId.longValue()) {
                canDeleteIds.add(id);
            }
        }

        if (canDeleteIds.isEmpty()) {
            return R.error("没有权限删除这些评论");
        }

        chinese_recipe_commentService.deleteBatchIds(canDeleteIds);
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
		
		Wrapper<ChineseRecipeCommentEntity> wrapper = new EntityWrapper<ChineseRecipeCommentEntity>();
		wrapper.eq("source_type", "chinese_recipe");
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = chinese_recipe_commentService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	







}

