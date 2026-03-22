package com.controller;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.AdminEntity;
import com.service.TokenService;
import com.service.AdminService;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 后台管理员账号相关接口
 */
@RequestMapping("admin")
@RestController
public class AdminController{
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private TokenService tokenService;

	@IgnoreAuth
	@PostMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		AdminEntity user = adminService.selectOne(new EntityWrapper<AdminEntity>().eq("username", username));
		if(user==null || !user.getPassword().equals(password)) {
			return R.error("账号或密码不正确");
		}
		String token = tokenService.generateToken(user.getId(),username, "admin", user.getRole());
		return R.ok().put("token", token);
	}

	@IgnoreAuth
	@PostMapping(value = "/register")
	public R register(@RequestBody AdminEntity user){
    	if(adminService.selectOne(new EntityWrapper<AdminEntity>().eq("username", user.getUsername())) !=null) {
    		return R.error("用户已存在");
    	}
    	if(user.getRole() == null || user.getRole().trim().isEmpty()) {
    		user.setRole("管理员");
    	}
        adminService.insert(user);
        return R.ok();
    }

	@GetMapping(value = "logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}

    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	AdminEntity user = adminService.selectOne(new EntityWrapper<AdminEntity>().eq("username", username));
    	if(user==null) {
    		return R.error("账号不存在");
    	}
    	user.setPassword("123456");
        adminService.update(user,null);
        return R.ok("密码已重置为：123456");
    }

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, AdminEntity user){
        EntityWrapper<AdminEntity> ew = new EntityWrapper<AdminEntity>();
    	PageUtils page = adminService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.allLike(ew, user), params), params));
        return R.ok().put("data", page);
    }

    @RequestMapping("/list")
    public R list( AdminEntity user){
       	EntityWrapper<AdminEntity> ew = new EntityWrapper<AdminEntity>();
      	ew.allEq(MPUtil.allEQMapPre( user, "user")); 
        return R.ok().put("data", adminService.selectListView(ew));
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
        AdminEntity user = adminService.selectById(id);
        return R.ok().put("data", user);
    }

    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        AdminEntity user = adminService.selectById(id);
        return R.ok().put("data", user);
    }

    @PostMapping("/save")
    public R save(@RequestBody AdminEntity user){
    	if(adminService.selectOne(new EntityWrapper<AdminEntity>().eq("username", user.getUsername())) !=null) {
    		return R.error("用户已存在");
    	}
        adminService.insert(user);
        return R.ok();
    }

    @RequestMapping("/update")
    public R update(@RequestBody AdminEntity user){
    	AdminEntity u = adminService.selectOne(new EntityWrapper<AdminEntity>().eq("username", user.getUsername()));
    	if(u!=null && u.getId()!=user.getId() && u.getUsername().equals(user.getUsername())) {
    		return R.error("用户名已存在。");
    	}
        adminService.updateById(user);//全部更新
        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        adminService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}

