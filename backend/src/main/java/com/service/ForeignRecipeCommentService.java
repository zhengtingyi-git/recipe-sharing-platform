package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ForeignRecipeCommentEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ForeignRecipeCommentVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ForeignRecipeCommentView;


/**
 * 外国美食评论表
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ForeignRecipeCommentService extends IService<ForeignRecipeCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ForeignRecipeCommentVO> selectListVO(Wrapper<ForeignRecipeCommentEntity> wrapper);
   	
   	ForeignRecipeCommentVO selectVO(@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);
   	
   	List<ForeignRecipeCommentView> selectListView(Wrapper<ForeignRecipeCommentEntity> wrapper);
   	
   	ForeignRecipeCommentView selectView(@Param("ew") Wrapper<ForeignRecipeCommentEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ForeignRecipeCommentEntity> wrapper);
   	

}


