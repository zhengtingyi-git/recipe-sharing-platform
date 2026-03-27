package com.dao;

import com.entity.ChineseRecipeEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ChineseRecipeVO;
import com.entity.view.ChineseRecipeView;


/**
 * 中式美食
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ChineseRecipeDao extends BaseMapper<ChineseRecipeEntity> {
	
	List<ChineseRecipeVO> selectListVO(@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);
	
	ChineseRecipeVO selectVO(@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);
	
	List<ChineseRecipeView> selectListView(@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);

	List<ChineseRecipeView> selectListView(Pagination page,@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);
	
	ChineseRecipeView selectView(@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);
	

}

