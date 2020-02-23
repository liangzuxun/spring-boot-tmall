package com.lzx.tmall.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lzx.tmall.pojo.Category;
import com.lzx.tmall.service.CategoryService;
import com.lzx.tmall.util.Page4Navigator;

@RestController
public class CategoryController {
	@Autowired CategoryService categoryService;
	//获取
	@GetMapping("/categories")
	public Page4Navigator<Category> list(@RequestParam(value="start",defaultValue="0") int start
			,@RequestParam(value="size",defaultValue="5") int size) throws Exception{
		start = start<0?0:start;
		Page4Navigator<Category> page = categoryService.list(start, size, 5); 
		return page;
	}
	//删除
	@DeleteMapping("/categories/{id}")
	public String delete(@PathVariable("id")int id,HttpServletRequest req)throws Exception{
		File imageFolder = new File(req.getServletContext().getRealPath("img/category"));
		File file = new File(imageFolder,id+".jpg");
		file.delete();
		categoryService.delete(id);
		return null;
	}
	//查
	@GetMapping("/categories/{id}")
	public Category get(@PathVariable("id")int id)throws Exception{
		return categoryService.get(id);
	}
	
	//axios + new FormData()方式上传文件
	//增加
	@PostMapping("/categories")
	public Object add(Category bean,MultipartFile image,HttpServletRequest request) throws Exception{
		categoryService.add(bean);
		saveOrUpdateImageFile(bean,image,request);
		return bean;
	}
	public void saveOrUpdateImageFile(Category bean,MultipartFile image,HttpServletRequest request) throws Exception{
		File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
		File file = new File(imageFolder,bean.getId()+".jpg");
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		image.transferTo(file);
	}
	//修改
	@PutMapping("/categories/{id}")
	public Object update(Category bean,MultipartFile image,HttpServletRequest request) throws Exception{
		String name = request.getParameter("name");
		System.out.println(bean.getId());
		bean.setName(name);
		categoryService.update(bean);
		if(!image.isEmpty()) {
			saveOrUpdateImageFile(bean,image,request);
		}
		return bean;
	}
	
	
	
	//古老的方式
	//submit + enctype="multipart/form-data"
	@PostMapping("/uploadImageTwo")
	public void uploadImage2(@RequestParam(value="myfile") MultipartFile image) throws Exception{
		System.out.println(image.getOriginalFilename());
	}
	//-> create page -> pageController-> get-> getController -> service-> update-> updateController-> update form
}
