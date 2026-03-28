package com.entity.view;

import com.entity.ForumPostCommentEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * ?
 * ?  
 * 
 */
@TableName("forum_post_comment")
public class ForumPostCommentView extends ForumPostCommentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ForumPostCommentView(){
	}
 
 	public ForumPostCommentView(ForumPostCommentEntity ForumPostCommentEntity){
 	try {
			BeanUtils.copyProperties(this, ForumPostCommentEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
 		
	}
}

