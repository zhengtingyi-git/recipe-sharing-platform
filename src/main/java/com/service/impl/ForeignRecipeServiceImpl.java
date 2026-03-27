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


import com.dao.ForeignRecipeDao;
import com.entity.ForeignRecipeEntity;
import com.service.ForeignRecipeService;
import com.entity.vo.ForeignRecipeVO;
import com.entity.view.ForeignRecipeView;

@Service("foreign_recipeService")
public class ForeignRecipeServiceImpl extends ServiceImpl<ForeignRecipeDao, ForeignRecipeEntity> implements ForeignRecipeService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ForeignRecipeEntity> page = this.selectPage(
                new Query<ForeignRecipeEntity>(params).getPage(),
                new EntityWrapper<ForeignRecipeEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ForeignRecipeEntity> wrapper) {
		  Page<ForeignRecipeView> page =new Query<ForeignRecipeView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ForeignRecipeVO> selectListVO(Wrapper<ForeignRecipeEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ForeignRecipeVO selectVO(Wrapper<ForeignRecipeEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ForeignRecipeView> selectListView(Wrapper<ForeignRecipeEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ForeignRecipeView selectView(Wrapper<ForeignRecipeEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}

