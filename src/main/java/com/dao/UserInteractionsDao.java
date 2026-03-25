package com.dao;

import com.entity.UserInteractionsEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.UserInteractionsVO;
import com.entity.view.UserInteractionsView;


/**
 * 收藏表
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface UserInteractionsDao extends BaseMapper<UserInteractionsEntity> {
	
	List<UserInteractionsVO> selectListVO(@Param("ew") Wrapper<UserInteractionsEntity> wrapper);
	
	UserInteractionsVO selectVO(@Param("ew") Wrapper<UserInteractionsEntity> wrapper);
	
	List<UserInteractionsView> selectListView(@Param("ew") Wrapper<UserInteractionsEntity> wrapper);

	List<UserInteractionsView> selectListView(Pagination page,@Param("ew") Wrapper<UserInteractionsEntity> wrapper);
	
	UserInteractionsView selectView(@Param("ew") Wrapper<UserInteractionsEntity> wrapper);
	

}
