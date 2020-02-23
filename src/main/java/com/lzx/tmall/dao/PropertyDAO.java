package com.lzx.tmall.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lzx.tmall.pojo.Category;
import com.lzx.tmall.pojo.Property;

public interface PropertyDAO extends JpaRepository<Property, Integer>{
	Page<Property> findByCategory(Category category,Pageable pageable);
	List<Property> findByCategory(Category category);
}
