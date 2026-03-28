package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.UserInteractionsEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.UserInteractionsVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.UserInteractionsView;


/**
 * 收藏表
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface UserInteractionsService extends IService<UserInteractionsEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<UserInteractionsVO> selectListVO(Wrapper<UserInteractionsEntity> wrapper);
   	
   	UserInteractionsVO selectVO(@Param("ew") Wrapper<UserInteractionsEntity> wrapper);
   	
   	List<UserInteractionsView> selectListView(Wrapper<UserInteractionsEntity> wrapper);
   	
   	UserInteractionsView selectView(@Param("ew") Wrapper<UserInteractionsEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<UserInteractionsEntity> wrapper);
   	

}

