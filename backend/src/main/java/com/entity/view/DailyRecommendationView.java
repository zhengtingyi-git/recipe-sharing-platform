package com.entity.view;

import com.entity.DailyRecommendationEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 姣忔棩鎺ㄨ崘
 * 鍚庣杩斿洖瑙嗗浘瀹炰綋杈呭姪绫?  
 * 锛堥€氬父鍚庣鍏宠仈鐨勮〃鎴栬€呰嚜瀹氫箟鐨勫瓧娈甸渶瑕佽繑鍥炰娇鐢級
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("remencaipin")
public class DailyRecommendationView  extends DailyRecommendationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public DailyRecommendationView(){
	}
 
 	public DailyRecommendationView(DailyRecommendationEntity remencaipinEntity){
 	try {
			BeanUtils.copyProperties(this, remencaipinEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}

