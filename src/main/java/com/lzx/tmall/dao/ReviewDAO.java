package com.lzx.tmall.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.Review;

public interface ReviewDAO extends JpaRepository<Review, Integer>{
	List<Review> findByProductOrderByIdDesc(Product product);
	int countByProduct(Product product);
}
