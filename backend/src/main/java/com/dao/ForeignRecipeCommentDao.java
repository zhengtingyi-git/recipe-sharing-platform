package com.dao;

import com.entity.ForeignRecipeCommentEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ForeignRecipeCommentVO;
import com.entity.view.ForeignRecipeCommentView;


/**
 * 外国美食评论表
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ForeignRecipeCommentDao extends BaseMapper<ForeignRecipeCommentEntity> {
	
	List<ForeignRecipeCommentVO> selectListVO(@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);
	
	ForeignRecipeCommentVO selectVO(@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);
	
	List<ForeignRecipeCommentView> selectListView(@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);

	List<ForeignRecipeCommentView> selectListView(Pagination page,@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);
	
	ForeignRecipeCommentView selectView(@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);
	

}

