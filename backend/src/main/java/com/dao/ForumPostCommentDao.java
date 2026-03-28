package com.dao;

import com.entity.ForumPostCommentEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ForumPostCommentVO;
import com.entity.view.ForumPostCommentView;

/**
 * ?
 */
public interface ForumPostCommentDao extends BaseMapper<ForumPostCommentEntity> {
	
	List<ForumPostCommentVO> selectListVO(@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);
	
	ForumPostCommentVO selectVO(@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);
	
	List<ForumPostCommentView> selectListView(@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);

	List<ForumPostCommentView> selectListView(Pagination page,@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);
	
	ForumPostCommentView selectView(@Param("ew") Wrapper<ForumPostCommentEntity> wrapper);
}

