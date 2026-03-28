package com.dao;

import com.entity.ForumPostEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ForumPostVO;
import com.entity.view.ForumPostView;


/**
 * 缇庨璁哄潧
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ForumPostDao extends BaseMapper<ForumPostEntity> {
	
	List<ForumPostVO> selectListVO(@Param("ew") Wrapper<ForumPostEntity> wrapper);
	
	ForumPostVO selectVO(@Param("ew") Wrapper<ForumPostEntity> wrapper);
	
	List<ForumPostView> selectListView(@Param("ew") Wrapper<ForumPostEntity> wrapper);

	List<ForumPostView> selectListView(Pagination page,@Param("ew") Wrapper<ForumPostEntity> wrapper);
	
	ForumPostView selectView(@Param("ew") Wrapper<ForumPostEntity> wrapper);
	

}