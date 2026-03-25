package com.entity.view;

import com.entity.ForumPostEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 
 * ?  
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("forum_post")
public class ForumPostView  extends ForumPostEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ForumPostView(){
	}
 
 	public ForumPostView(ForumPostEntity forumPostEntity){
 	try {
			BeanUtils.copyProperties(this, forumPostEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}