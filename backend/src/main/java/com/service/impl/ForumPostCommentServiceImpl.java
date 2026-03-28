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

import com.dao.ForumPostCommentDao;
import com.entity.ForumPostCommentEntity;
import com.service.ForumPostCommentService;
import com.entity.vo.ForumPostCommentVO;
import com.entity.view.ForumPostCommentView;

@Service("forumPostCommentService")
public class ForumPostCommentServiceImpl extends ServiceImpl<ForumPostCommentDao, ForumPostCommentEntity> implements ForumPostCommentService {
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ForumPostCommentEntity> page = this.selectPage(
                new Query<ForumPostCommentEntity>(params).getPage(),
                new EntityWrapper<ForumPostCommentEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ForumPostCommentEntity> wrapper) {
		  Page<ForumPostCommentView> page =new Query<ForumPostCommentView>(params).getPage();
	      page.setRecords(baseMapper.selectListView(page,wrapper));
	      PageUtils pageUtil = new PageUtils(page);
	      return pageUtil;
 	}
    
    @Override
	public List<ForumPostCommentVO> selectListVO(Wrapper<ForumPostCommentEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ForumPostCommentVO selectVO(Wrapper<ForumPostCommentEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ForumPostCommentView> selectListView(Wrapper<ForumPostCommentEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ForumPostCommentView selectView(Wrapper<ForumPostCommentEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
}

