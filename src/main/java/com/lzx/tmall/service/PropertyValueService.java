package com.lzx.tmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzx.tmall.dao.PropertyValueDAO;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.Property;
import com.lzx.tmall.pojo.PropertyValue;

@Service
public class PropertyValueService {
	@Autowired PropertyValueDAO propertyValueDAO;
	@Autowired PropertyService propertyService;
	public void update(PropertyValue bean) {
		propertyValueDAO.save(bean);
	}
	//获取某产品的所有属性值
	public List<PropertyValue> list(Product product){
		return propertyValueDAO.findByProductOrderByIdDesc(product);
	}
	//根据属性名和产品找到对应的属性值
	public PropertyValue getByPropertyAndProduct(Product product,Property property) {
		return propertyValueDAO.getByPropertyAndProduct(property, product);
	}
	//初始化 因为没有增加 只有修改 所以需要初始化值
	public void init(Product product) {
		//产品对应分类的所有属性名
		List<Property> propertys = propertyService.listByCategory(product.getCategory());
		//遍历属性名
		for(Property property:propertys) {
			//属性名+产品找到值
			PropertyValue propertyValue = propertyValueDAO.getByPropertyAndProduct(property, product);
			//找不到值
			if(null == propertyValue) {
				propertyValue = new PropertyValue();
				propertyValue.setProduct(product);
				propertyValue.setProperty(property);
				propertyValueDAO.save(propertyValue);
			}
		}
	}
}
