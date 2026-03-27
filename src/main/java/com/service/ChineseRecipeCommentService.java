package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ChineseRecipeCommentEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ChineseRecipeCommentVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ChineseRecipeCommentView;


/**
 * 中式美食评论表
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ChineseRecipeCommentService extends IService<ChineseRecipeCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ChineseRecipeCommentVO> selectListVO(Wrapper<ChineseRecipeCommentEntity> wrapper);
   	
   	ChineseRecipeCommentVO selectVO(@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);
   	
   	List<ChineseRecipeCommentView> selectListView(Wrapper<ChineseRecipeCommentEntity> wrapper);
   	
   	ChineseRecipeCommentView selectView(@Param("ew") Wrapper<ChineseRecipeCommentEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ChineseRecipeCommentEntity> wrapper);
   	

}


