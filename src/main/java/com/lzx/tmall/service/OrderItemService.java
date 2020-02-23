package com.lzx.tmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lzx.tmall.dao.OrderItemDAO;
import com.lzx.tmall.pojo.Order;
import com.lzx.tmall.pojo.OrderItem;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.User;
import com.lzx.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames="orderItems")
public class OrderItemService {
	@Autowired ProductImageService productImageService;
	@Autowired OrderItemDAO orderItemDAO;
	@Cacheable(key="'orderItems-oid-'+#p0.id")
	public List<OrderItem> listByOrder(Order order){
		return orderItemDAO.findByOrderOrderByIdDesc(order);
	}
	@Cacheable(key="'orderItems-pid-'+#p0.id")
	public List<OrderItem> listByProduct(Product product){
		return orderItemDAO.findByProduct(product);
	}
	//给Order填充orderItems
	public void fill(Order order) {
		OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
		List<OrderItem> ois = orderItemService.listByOrder(order);
		float total = 0;
		int totalNumber = 0;
		for(OrderItem oi:ois) {
			total+= oi.getNumber() * oi.getProduct().getPromotePrice();
			totalNumber+=oi.getNumber();
			productImageService.setFirstProductImage(oi.getProduct());
		}
		order.setTotal(total);
		order.setTotalNumber(totalNumber);
		order.setOrderItems(ois);
	}
	public void fill(List<Order> orders) {
		for(Order order:orders) {
			fill(order);
		}
	}
	public int getSaleCount(Product product) {
		OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
		List<OrderItem> ois = orderItemService.listByProduct(product);
		int result = 0;
		for(OrderItem o:ois) {
			//是否已经付款
			if(null != o.getOrder()) {
				if(null != o.getOrder() && null != o.getOrder().getPayDate()) {
					result+=o.getNumber();
				}
			}
		}
		return result;
	}
	//crud
	@CacheEvict(allEntries=true)
	public void add(OrderItem orderItem) {
		orderItemDAO.save(orderItem);
	}
	@CacheEvict(allEntries=true)
	public void delete(int id) {
		orderItemDAO.delete(id);
	}
	@CacheEvict(allEntries=true)
	public void update(OrderItem orderItem) {
		orderItemDAO.save(orderItem);
	}
	@Cacheable(key="'orderItems-one-'+#p0")
	public OrderItem get(int id) {
		return orderItemDAO.findOne(id);
	}
	@Cacheable(key="'orderItems-uid-'+#p0.id")
	public List<OrderItem> listByUser(User user){
		return orderItemDAO.findByUserAndOrderIsNull(user);
	}
}
