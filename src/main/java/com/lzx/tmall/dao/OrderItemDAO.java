package com.lzx.tmall.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lzx.tmall.pojo.Order;
import com.lzx.tmall.pojo.OrderItem;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.User;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer>{
	List<OrderItem> findByOrderOrderByIdDesc(Order order);
	List<OrderItem> findByProduct(Product product);
	
	List<OrderItem> findByUserAndOrderIsNull(User user);
}
