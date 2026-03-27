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


import com.dao.ChineseRecipeDao;
import com.entity.ChineseRecipeEntity;
import com.service.ChineseRecipeService;
import com.entity.vo.ChineseRecipeVO;
import com.entity.view.ChineseRecipeView;

@Service("chinese_recipeService")
public class ChineseRecipeServiceImpl extends ServiceImpl<ChineseRecipeDao, ChineseRecipeEntity> implements ChineseRecipeService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ChineseRecipeEntity> page = this.selectPage(
                new Query<ChineseRecipeEntity>(params).getPage(),
                new EntityWrapper<ChineseRecipeEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ChineseRecipeEntity> wrapper) {
		  Page<ChineseRecipeView> page =new Query<ChineseRecipeView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ChineseRecipeVO> selectListVO(Wrapper<ChineseRecipeEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ChineseRecipeVO selectVO(Wrapper<ChineseRecipeEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ChineseRecipeView> selectListView(Wrapper<ChineseRecipeEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ChineseRecipeView selectView(Wrapper<ChineseRecipeEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}

