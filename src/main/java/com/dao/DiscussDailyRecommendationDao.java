package com.dao;

import com.entity.DiscussDailyRecommendationEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.DiscussDailyRecommendationVO;
import com.entity.view.DiscussDailyRecommendationView;


/**
 * 姣忔棩鎺ㄨ崘璇勮琛?
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface DiscussDailyRecommendationDao extends BaseMapper<DiscussDailyRecommendationEntity> {
	
	List<DiscussDailyRecommendationVO> selectListVO(@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);
	
	DiscussDailyRecommendationVO selectVO(@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);
	
	List<DiscussDailyRecommendationView> selectListView(@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);

	List<DiscussDailyRecommendationView> selectListView(Pagination page,@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);
	
	DiscussDailyRecommendationView selectView(@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);
	

}

