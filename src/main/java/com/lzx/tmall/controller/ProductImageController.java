package com.lzx.tmall.controller;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.ProductImage;
import com.lzx.tmall.service.CategoryService;
import com.lzx.tmall.service.ProductImageService;
import com.lzx.tmall.service.ProductService;
import com.lzx.tmall.util.ImageUtil;

@RestController
public class ProductImageController {
	@Autowired ProductService productService;
	@Autowired ProductImageService productImageService;
	@Autowired CategoryService categoryService;
	
	@GetMapping("/products/{pid}/productImages")
	public List<ProductImage> list(@PathVariable("pid")int pid,@RequestParam("type")String type) throws Exception{
		Product product = productService.get(pid);
		
		if(ProductImageService.type_detail.equals(type)) {
			List<ProductImage> details = productImageService.listDetailProductImages(product);
			return details;
		}else if(ProductImageService.type_single.equals(type)){
			
			List<ProductImage> singles = productImageService.listSingleProductImages(product);
			return singles;
		}
		return new ArrayList<>();
	}
	@PostMapping("/productImages")
	public Object add(@RequestParam("pid")int pid,@RequestParam("type")String type,MultipartFile image,HttpServletRequest request) throws Exception{
		//数据库部分
		ProductImage bean = new ProductImage();
		Product product = productService.get(pid);
		bean.setProduct(product);
		bean.setType(type);
		productImageService.add(bean);
		//图片文件处理
		String folder;
		if(ProductImageService.type_detail.equals(type)) {
			folder = "img/productDetail";
		}else {
			folder = "img/productSingle";
		}
		//获取路径 创建文件 二进制文件流输入输出
		File imageFolder = new File(request.getServletContext().getRealPath(folder));
		File file = new File(imageFolder,bean.getId()+".jpg");
		String fileName = file.getName();
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		image.transferTo(file);
		if(ProductImageService.type_single.equals(bean.getType())) {
			String imageFolder_small = request.getServletContext().getRealPath("img/productSingle_small");
			String imageFolder_middle = request.getServletContext().getRealPath("img/productSingle_middle");
			File f_small = new File(imageFolder_small,fileName);
			File f_middle = new File(imageFolder_middle,fileName);
			f_small.getParentFile().mkdirs();
			f_middle.getParentFile().mkdirs();
			ImageUtil.resizeImage(file, 56, 56,f_small);
			ImageUtil.resizeImage(file, 217, 190,f_middle);
		}
		return bean;
	}
	@DeleteMapping("/productImages/{id}")
	public String delete(@PathVariable("id")int id,HttpServletRequest request) throws Exception{
		ProductImage bean = productImageService.get(id);
		productImageService.delete(id);
		String folder;
		if(ProductImageService.type_single.equals(bean.getType())) {
			folder = "img/productSingle";
		}else {
			folder = "img/productDetail";
		}
		File imageFolder = new File(request.getServletContext().getRealPath(folder));
		File file = new File(imageFolder,bean.getId()+".jpg");
		String fileName = file.getName();
		file.delete();
		if(ProductImageService.type_single.equals(bean.getType())) {
			String imageFolder_small = request.getServletContext().getRealPath("img/productSingle_small");
			String imageFolder_middle = request.getServletContext().getRealPath("img/productSingle_middle");
			File f_small = new File(imageFolder_small,fileName);
			File f_middle = new File(imageFolder_middle,fileName);
			f_small.delete();
			f_middle.delete();
		}
		
		return null;
	}
}
