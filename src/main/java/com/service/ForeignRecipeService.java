package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ForeignRecipeEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ForeignRecipeVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ForeignRecipeView;


/**
 * 外国美食
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ForeignRecipeService extends IService<ForeignRecipeEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ForeignRecipeVO> selectListVO(Wrapper<ForeignRecipeEntity> wrapper);
   	
   	ForeignRecipeVO selectVO(@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);
   	
   	List<ForeignRecipeView> selectListView(Wrapper<ForeignRecipeEntity> wrapper);
   	
   	ForeignRecipeView selectView(@Param("ew") Wrapper<ForeignRecipeEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ForeignRecipeEntity> wrapper);
   	

}


