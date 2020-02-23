package com.lzx.tmall.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lzx.tmall.pojo.Category;
//jpa提供了crud和分页等常见功能
public interface CategoryDAO extends JpaRepository<Category, Integer>{

	List<Category> findAll(Sort sort);

}
