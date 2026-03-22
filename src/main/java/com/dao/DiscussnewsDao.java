package com.dao;

import com.entity.DiscussnewsEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.DiscussnewsVO;
import com.entity.view.DiscussnewsView;

/**
 * 美食论坛评论表
 */
public interface DiscussnewsDao extends BaseMapper<DiscussnewsEntity> {
	
	List<DiscussnewsVO> selectListVO(@Param("ew") Wrapper<DiscussnewsEntity> wrapper);
	
	DiscussnewsVO selectVO(@Param("ew") Wrapper<DiscussnewsEntity> wrapper);
	
	List<DiscussnewsView> selectListView(@Param("ew") Wrapper<DiscussnewsEntity> wrapper);

	List<DiscussnewsView> selectListView(Pagination page,@Param("ew") Wrapper<DiscussnewsEntity> wrapper);
	
	DiscussnewsView selectView(@Param("ew") Wrapper<DiscussnewsEntity> wrapper);
}

