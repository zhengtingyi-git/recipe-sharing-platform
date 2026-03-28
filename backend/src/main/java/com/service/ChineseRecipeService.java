package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ChineseRecipeEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ChineseRecipeVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ChineseRecipeView;


/**
 * 中式美食
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ChineseRecipeService extends IService<ChineseRecipeEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ChineseRecipeVO> selectListVO(Wrapper<ChineseRecipeEntity> wrapper);
   	
   	ChineseRecipeVO selectVO(@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);
   	
   	List<ChineseRecipeView> selectListView(Wrapper<ChineseRecipeEntity> wrapper);
   	
   	ChineseRecipeView selectView(@Param("ew") Wrapper<ChineseRecipeEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ChineseRecipeEntity> wrapper);
   	

}


