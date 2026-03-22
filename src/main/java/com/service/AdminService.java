
package com.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.AdminEntity;
import com.utils.PageUtils;


/**
 * 系统用户
 */
public interface AdminService extends IService<AdminEntity> {
 	PageUtils queryPage(Map<String, Object> params);
    
   	List<AdminEntity> selectListView(Wrapper<AdminEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<AdminEntity> wrapper);
	   	
}
