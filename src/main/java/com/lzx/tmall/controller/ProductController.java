package com.lzx.tmall.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.service.ProductImageService;
import com.lzx.tmall.service.ProductService;
import com.lzx.tmall.util.Page4Navigator;

@RestController
public class ProductController {
	@Autowired ProductService productService;
	@Autowired ProductImageService productImageService;
	@GetMapping("/categories/{cid}/products")
	public Page4Navigator<Product> list(@PathVariable("cid")int cid,@RequestParam(value="start",defaultValue="0")int start,
			@RequestParam(value="size",defaultValue="5") int size
			){
		start = start<0?0:start;
		Page4Navigator<Product> page = productService.list(cid, start, size, 5);
		productImageService.setFirstProductImages(page.getContent());
		return page;
	}
	@PostMapping("/products")
	public Object add(@RequestBody Product bean) throws Exception{
		bean.setCreateDate(new Date());
		productService.add(bean);
		return bean;
	}
	@PutMapping("/products")
	public Object update(@RequestBody Product bean) throws Exception{
		productService.update(bean);
		return bean;
	}
	@DeleteMapping("/products/{id}")
	public String delete(@PathVariable("id")int id) throws Exception{
		productService.delete(id);
		return null;
	}
	@GetMapping("/products/{id}")
	public Product get(@PathVariable("id")int id) throws Exception{
		Product product = productService.get(id);
		return product;
	}
	
}
