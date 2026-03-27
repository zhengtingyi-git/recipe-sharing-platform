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


import com.dao.ForeignRecipeCommentDao;
import com.entity.ForeignRecipeCommentEntity;
import com.service.ForeignRecipeCommentService;
import com.entity.vo.ForeignRecipeCommentVO;
import com.entity.view.ForeignRecipeCommentView;

@Service("foreign_recipe_commentService")
public class ForeignRecipeCommentServiceImpl extends ServiceImpl<ForeignRecipeCommentDao, ForeignRecipeCommentEntity> implements ForeignRecipeCommentService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ForeignRecipeCommentEntity> page = this.selectPage(
                new Query<ForeignRecipeCommentEntity>(params).getPage(),
                new EntityWrapper<ForeignRecipeCommentEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ForeignRecipeCommentEntity> wrapper) {
		  Page<ForeignRecipeCommentView> page =new Query<ForeignRecipeCommentView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ForeignRecipeCommentVO> selectListVO(Wrapper<ForeignRecipeCommentEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ForeignRecipeCommentVO selectVO(Wrapper<ForeignRecipeCommentEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ForeignRecipeCommentView> selectListView(Wrapper<ForeignRecipeCommentEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ForeignRecipeCommentView selectView(Wrapper<ForeignRecipeCommentEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}

