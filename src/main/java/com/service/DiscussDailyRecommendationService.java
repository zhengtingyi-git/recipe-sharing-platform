package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DiscussDailyRecommendationEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.DiscussDailyRecommendationVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.DiscussDailyRecommendationView;


/**
 * 姣忔棩鎺ㄨ崘璇勮琛?
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface DiscussDailyRecommendationService extends IService<DiscussDailyRecommendationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DiscussDailyRecommendationVO> selectListVO(Wrapper<DiscussDailyRecommendationEntity> wrapper);
   	
   	DiscussDailyRecommendationVO selectVO(@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);
   	
   	List<DiscussDailyRecommendationView> selectListView(Wrapper<DiscussDailyRecommendationEntity> wrapper);
   	
   	DiscussDailyRecommendationView selectView(@Param("ew") Wrapper<DiscussDailyRecommendationEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DiscussDailyRecommendationEntity> wrapper);
   	

}


