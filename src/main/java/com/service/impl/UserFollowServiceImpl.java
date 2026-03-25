package com.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.UserFollowDao;
import com.entity.UserFollowEntity;
import com.service.UserFollowService;
import org.springframework.stereotype.Service;

@Service("userFollowService")
public class UserFollowServiceImpl extends ServiceImpl<UserFollowDao, UserFollowEntity> implements UserFollowService {
}
