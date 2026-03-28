package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ForumPostCommentEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ForumPostCommentVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ForumPostCommentView;

/**
 * ?
 */
public interface ForumPostCommentService extends IService<ForumPostCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ForumPostCommentVO> selectListVO(Wrapper<ForumPostCommentEntity> wrapper);
   	
   	ForumPostCommentVO selectVO(@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);
   	
   	List<ForumPostCommentView> selectListView(Wrapper<ForumPostCommentEntity> wrapper);
   	
   	ForumPostCommentView selectView(@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ForumPostCommentEntity> wrapper);
}

