package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ForumPostEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ForumPostVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ForumPostView;


/**
 * 缇庨璁哄潧
 *
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public interface ForumPostService extends IService<ForumPostEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ForumPostVO> selectListVO(Wrapper<ForumPostEntity> wrapper);
   	
   	ForumPostVO selectVO(@Param("ew") Wrapper<ForumPostEntity> wrapper);
   	
   	List<ForumPostView> selectListView(Wrapper<ForumPostEntity> wrapper);
   	
   	ForumPostView selectView(@Param("ew") Wrapper<ForumPostEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ForumPostEntity> wrapper);
   	

}
