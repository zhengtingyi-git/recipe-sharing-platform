package com.entity.view;

import com.entity.DiscussnewsEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 美食论坛评论表
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("discussnews")
public class DiscussnewsView extends DiscussnewsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public DiscussnewsView(){
	}
 
 	public DiscussnewsView(DiscussnewsEntity discussnewsEntity){
 	try {
			BeanUtils.copyProperties(this, discussnewsEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
 		
	}
}

