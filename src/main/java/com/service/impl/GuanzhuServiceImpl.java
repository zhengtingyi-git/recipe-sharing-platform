package com.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.GuanzhuDao;
import com.entity.GuanzhuEntity;
import com.service.GuanzhuService;
import org.springframework.stereotype.Service;

@Service("guanzhuService")
public class GuanzhuServiceImpl extends ServiceImpl<GuanzhuDao, GuanzhuEntity> implements GuanzhuService {
}
