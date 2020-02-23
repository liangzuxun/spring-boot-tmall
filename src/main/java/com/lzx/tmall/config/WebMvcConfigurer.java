package com.lzx.tmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.lzx.tmall.interceptor.LoginInterceptor;
import com.lzx.tmall.interceptor.OtherInterceptor;
import com.lzx.tmall.interceptor.TestInterceptor;
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter{
	@Bean
	public LoginInterceptor getLoginInterceptor() {
		return new LoginInterceptor();
	}
	@Bean
	public OtherInterceptor getOtherInterceptor() {
		return new OtherInterceptor();
	}
	@Bean
	public TestInterceptor getTestInterceptor() {
		return new TestInterceptor();
	}
	public void  addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getLoginInterceptor())
		.addPathPatterns("/**");
		registry.addInterceptor(getOtherInterceptor())
		.addPathPatterns("/**");
		registry.addInterceptor(getTestInterceptor())
		.addPathPatterns("/**");
		
	}
}
