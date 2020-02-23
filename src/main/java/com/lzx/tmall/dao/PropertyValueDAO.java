package com.lzx.tmall.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.Property;
import com.lzx.tmall.pojo.PropertyValue;



public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
	List<PropertyValue> findByProductOrderByIdDesc(Product product);
	PropertyValue getByPropertyAndProduct(Property property,Product product);
}
