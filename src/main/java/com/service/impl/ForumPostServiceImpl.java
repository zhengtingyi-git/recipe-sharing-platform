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


import com.dao.ForumPostDao;
import com.entity.ForumPostEntity;
import com.service.ForumPostService;
import com.entity.vo.ForumPostVO;
import com.entity.view.ForumPostView;

@Service("forumPostService")
public class ForumPostServiceImpl extends ServiceImpl<ForumPostDao, ForumPostEntity> implements ForumPostService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ForumPostEntity> page = this.selectPage(
                new Query<ForumPostEntity>(params).getPage(),
                new EntityWrapper<ForumPostEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ForumPostEntity> wrapper) {
		  Page<ForumPostView> page =new Query<ForumPostView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ForumPostVO> selectListVO(Wrapper<ForumPostEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ForumPostVO selectVO(Wrapper<ForumPostEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ForumPostView> selectListView(Wrapper<ForumPostEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ForumPostView selectView(Wrapper<ForumPostEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}