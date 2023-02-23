package com.shoppingcart.admin.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //file cau hinh, doc cau hinh nhung method la bean va bien method thanh spring bean
public class MvcConfig implements WebMvcConfigurer{ //WebMvcConfigurer cau hinh project mvc
	//phải khai báo addResourceHandlers thì mới truy cập đến folder user-photos được
	//khai báo nhiều folder cùng 1 lúc

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) { //muon dung photo phai dung addResourceHandlers cap quyen truy cap cho user
		exposeDirectory("user-photos", registry); //user-photos ten folder
		exposeDirectory("category-photos", registry);
		exposeDirectory("brand-logos", registry);
		exposeDirectory("product-images", registry);

//		registry.addResourceHandler("/brand-logos/").addResourceLocations("/brand-logos/");
	}

	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path = Paths.get(pathPattern);

		String absolutePath = path.toFile().getAbsolutePath();

		//String logicalPath = pathPattern.replace("../","") + "/**";//user-photos/**
		String logicalPath = pathPattern + "/**";//user-photos/**, /** la nhung gi ben trong user-photos dc truy cap

		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");


	}
}
