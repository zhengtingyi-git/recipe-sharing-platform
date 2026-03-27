package com.dao;

import com.entity.ForeignRecipeEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ForeignRecipeVO;
import com.entity.view.ForeignRecipeView;


/**
 * 外国美食
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ForeignRecipeDao extends BaseMapper<ForeignRecipeEntity> {
	
	List<ForeignRecipeVO> selectListVO(@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);
	
	ForeignRecipeVO selectVO(@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);
	
	List<ForeignRecipeView> selectListView(@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);

	List<ForeignRecipeView> selectListView(Pagination page,@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);
	
	ForeignRecipeView selectView(@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);
	

}

