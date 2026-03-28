package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.UserEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.UserVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.UserView;


/**
 * 用户
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<UserVO> selectListVO(Wrapper<UserEntity> wrapper);
   	
   	UserVO selectVO(@Param("ew") Wrapper<UserEntity> wrapper);
   	
   	List<UserView> selectListView(Wrapper<UserEntity> wrapper);
   	
   	UserView selectView(@Param("ew") Wrapper<UserEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<UserEntity> wrapper);
   	

}

