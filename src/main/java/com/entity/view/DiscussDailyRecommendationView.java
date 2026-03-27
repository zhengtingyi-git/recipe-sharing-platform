package com.entity.view;

import com.entity.DiscussDailyRecommendationEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
 

/**
 * 姣忔棩鎺ㄨ崘璇勮琛?
 * 鍚庣杩斿洖瑙嗗浘瀹炰綋杈呭姪绫?  
 * 锛堥€氬父鍚庣鍏宠仈鐨勮〃鎴栬€呰嚜瀹氫箟鐨勫瓧娈甸渶瑕佽繑鍥炰娇鐢級
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("discussremencaipin")
public class DiscussDailyRecommendationView  extends DiscussDailyRecommendationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public DiscussDailyRecommendationView(){
	}
 
 	public DiscussDailyRecommendationView(DiscussDailyRecommendationEntity discussDailyRecommendationEntity){
 	try {
			BeanUtils.copyProperties(this, discussDailyRecommendationEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}
}

