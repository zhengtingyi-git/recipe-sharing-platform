package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.DiscussDailyRecommendationDao;
import com.entity.DiscussDailyRecommendationEntity;
import com.service.DiscussDailyRecommendationService;
import com.entity.vo.DiscussDailyRecommendationVO;
import com.entity.view.DiscussDailyRecommendationView;

@Service("discussDailyRecommendationService")
public class DiscussDailyRecommendationServiceImpl extends ServiceImpl<DiscussDailyRecommendationDao, DiscussDailyRecommendationEntity> implements DiscussDailyRecommendationService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscussDailyRecommendationEntity> page = this.selectPage(
                new Query<DiscussDailyRecommendationEntity>(params).getPage(),
                new EntityWrapper<DiscussDailyRecommendationEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussDailyRecommendationEntity> wrapper) {
		  Page<DiscussDailyRecommendationView> page =new Query<DiscussDailyRecommendationView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<DiscussDailyRecommendationVO> selectListVO(Wrapper<DiscussDailyRecommendationEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public DiscussDailyRecommendationVO selectVO(Wrapper<DiscussDailyRecommendationEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<DiscussDailyRecommendationView> selectListView(Wrapper<DiscussDailyRecommendationEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DiscussDailyRecommendationView selectView(Wrapper<DiscussDailyRecommendationEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}

