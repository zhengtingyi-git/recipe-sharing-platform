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


import com.dao.UserInteractionsDao;
import com.entity.UserInteractionsEntity;
import com.service.UserInteractionsService;
import com.entity.vo.UserInteractionsVO;
import com.entity.view.UserInteractionsView;

@Service("userInteractionsService")
public class UserInteractionsServiceImpl extends ServiceImpl<UserInteractionsDao, UserInteractionsEntity> implements UserInteractionsService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<UserInteractionsEntity> page = this.selectPage(
                new Query<UserInteractionsEntity>(params).getPage(),
                new EntityWrapper<UserInteractionsEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<UserInteractionsEntity> wrapper) {
		  Page<UserInteractionsView> page =new Query<UserInteractionsView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<UserInteractionsVO> selectListVO(Wrapper<UserInteractionsEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public UserInteractionsVO selectVO(Wrapper<UserInteractionsEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<UserInteractionsView> selectListView(Wrapper<UserInteractionsEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public UserInteractionsView selectView(Wrapper<UserInteractionsEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
