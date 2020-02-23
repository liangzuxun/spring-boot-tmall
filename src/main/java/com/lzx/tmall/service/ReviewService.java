package com.lzx.tmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzx.tmall.dao.ReviewDAO;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.Review;

@Service
public class ReviewService {
	@Autowired ReviewDAO reviewDAO;
	
	public int getCount(Product product) {
		return reviewDAO.countByProduct(product);
	}
	public void add(Review review) {
		reviewDAO.save(review);
	}
	public List<Review> list(Product product){
		return reviewDAO.findByProductOrderByIdDesc(product);
	}
}
