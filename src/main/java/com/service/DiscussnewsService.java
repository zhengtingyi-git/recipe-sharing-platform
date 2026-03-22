package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DiscussnewsEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.DiscussnewsVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.DiscussnewsView;

/**
 * 美食论坛评论表
 */
public interface DiscussnewsService extends IService<DiscussnewsEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DiscussnewsVO> selectListVO(Wrapper<DiscussnewsEntity> wrapper);
   	
   	DiscussnewsVO selectVO(@Param("ew") Wrapper<DiscussnewsEntity> wrapper);
   	
   	List<DiscussnewsView> selectListView(Wrapper<DiscussnewsEntity> wrapper);
   	
   	DiscussnewsView selectView(@Param("ew") Wrapper<DiscussnewsEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DiscussnewsEntity> wrapper);
}

