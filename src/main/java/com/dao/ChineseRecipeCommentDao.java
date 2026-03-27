package com.dao;

import com.entity.ChineseRecipeCommentEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ChineseRecipeCommentVO;
import com.entity.view.ChineseRecipeCommentView;


/**
 * 中式美食评论表
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ChineseRecipeCommentDao extends BaseMapper<ChineseRecipeCommentEntity> {
	
	List<ChineseRecipeCommentVO> selectListVO(@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);
	
	ChineseRecipeCommentVO selectVO(@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);
	
	List<ChineseRecipeCommentView> selectListView(@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);

	List<ChineseRecipeCommentView> selectListView(Pagination page,@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);
	
	ChineseRecipeCommentView selectView(@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);
	

}

