package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.interceptor.AuthorizationInterceptor;
import java.io.File;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport{
	
	@Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
        super.addInterceptors(registry);
	}
	
	/**
	 * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/resources/")
        .addResourceLocations("classpath:/static/")
        .addResourceLocations("classpath:/admin/")
        .addResourceLocations("classpath:/front/")
        .addResourceLocations("classpath:/public/");

		// 单独为 /upload/** 添加资源映射：
		// 1）优先从打包进项目的静态资源中找（兼容原始示例数据的图片）
		// 2）再从项目运行目录下的 upload 文件夹中找（兼容运行期间上传的图片）
		String uploadFileSystemPath = System.getProperty("user.dir") + File.separator + "upload" + File.separator;
		registry.addResourceHandler("/upload/**")
				.addResourceLocations(
						"classpath:/static/upload/",
						"classpath:/front/upload/",
						"classpath:/public/upload/",
						"file:" + uploadFileSystemPath
				);

		super.addResourceHandlers(registry);
    }
}
