package com.lzx.tmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.PropertyValue;
import com.lzx.tmall.service.ProductService;
import com.lzx.tmall.service.PropertyValueService;

@RestController
public class PropertyValueController {
	@Autowired PropertyValueService propertyValueService;
	@Autowired ProductService productService;
	
	@GetMapping("/products/{pid}/propertyValues")
	public List<PropertyValue> list(@PathVariable("pid")int pid) throws Exception{
		Product product = productService.get(pid);
		//初始化这个产品 把这个产品所属类别的属性名和属性值初始化 因为只有修改 没有增加功能
		propertyValueService.init(product);
		List<PropertyValue> propertyValues = propertyValueService.list(product);
		return propertyValues;
	}
	@PutMapping("/propertyValues")
	public Object update(@RequestBody PropertyValue bean) {
		propertyValueService.update(bean);
		return bean;
	}
}
