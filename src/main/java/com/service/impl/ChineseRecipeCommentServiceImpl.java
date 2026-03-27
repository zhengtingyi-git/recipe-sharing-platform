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


import com.dao.ChineseRecipeCommentDao;
import com.entity.ChineseRecipeCommentEntity;
import com.service.ChineseRecipeCommentService;
import com.entity.vo.ChineseRecipeCommentVO;
import com.entity.view.ChineseRecipeCommentView;

@Service("chinese_recipe_commentService")
public class ChineseRecipeCommentServiceImpl extends ServiceImpl<ChineseRecipeCommentDao, ChineseRecipeCommentEntity> implements ChineseRecipeCommentService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ChineseRecipeCommentEntity> page = this.selectPage(
                new Query<ChineseRecipeCommentEntity>(params).getPage(),
                new EntityWrapper<ChineseRecipeCommentEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ChineseRecipeCommentEntity> wrapper) {
		  Page<ChineseRecipeCommentView> page =new Query<ChineseRecipeCommentView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ChineseRecipeCommentVO> selectListVO(Wrapper<ChineseRecipeCommentEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ChineseRecipeCommentVO selectVO(Wrapper<ChineseRecipeCommentEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ChineseRecipeCommentView> selectListView(Wrapper<ChineseRecipeCommentEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ChineseRecipeCommentView selectView(Wrapper<ChineseRecipeCommentEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}

