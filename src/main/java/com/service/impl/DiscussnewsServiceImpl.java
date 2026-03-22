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

import com.dao.DiscussnewsDao;
import com.entity.DiscussnewsEntity;
import com.service.DiscussnewsService;
import com.entity.vo.DiscussnewsVO;
import com.entity.view.DiscussnewsView;

@Service("discussnewsService")
public class DiscussnewsServiceImpl extends ServiceImpl<DiscussnewsDao, DiscussnewsEntity> implements DiscussnewsService {
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscussnewsEntity> page = this.selectPage(
                new Query<DiscussnewsEntity>(params).getPage(),
                new EntityWrapper<DiscussnewsEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussnewsEntity> wrapper) {
		  Page<DiscussnewsView> page =new Query<DiscussnewsView>(params).getPage();
	      page.setRecords(baseMapper.selectListView(page,wrapper));
	      PageUtils pageUtil = new PageUtils(page);
	      return pageUtil;
 	}
    
    @Override
	public List<DiscussnewsVO> selectListVO(Wrapper<DiscussnewsEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public DiscussnewsVO selectVO(Wrapper<DiscussnewsEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<DiscussnewsView> selectListView(Wrapper<DiscussnewsEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DiscussnewsView selectView(Wrapper<DiscussnewsEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
}

