package com.lzx.tmall.service;

import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lzx.tmall.dao.OrderDAO;
import com.lzx.tmall.pojo.Order;
import com.lzx.tmall.pojo.OrderItem;
import com.lzx.tmall.pojo.User;
import com.lzx.tmall.util.Page4Navigator;
import com.lzx.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames="orders")
public class OrderService {
	  public static final String waitPay = "waitPay";
	    public static final String waitDelivery = "waitDelivery";
	    public static final String waitConfirm = "waitConfirm";
	    public static final String waitReview = "waitReview";
	    public static final String finish = "finish";
	    public static final String delete = "delete";
	  @Autowired OrderDAO orderDAO;
	  @Autowired OrderItemService orderItemService;
	  @Cacheable(key="'orders-page-'+#p0+'-'+#p1")
	  public Page4Navigator<Order> list(int start,int size,int navigatePages){
		  Sort sort = new Sort(Sort.Direction.DESC,"id");
		  Pageable pageable = new PageRequest(start, size, sort);
		  Page pageFromJPA = orderDAO.findAll(pageable);
		  return new Page4Navigator<Order>(pageFromJPA,navigatePages);
	  }
	  public void removeOrderFromOrderItem(List<Order> orders) {
		  for(Order order:orders) {
			  removeOrderFromOrderItem(order);
		  }
	  }
	  //防止无限递归
	  public void removeOrderFromOrderItem(Order order) {
		  List<OrderItem> orderItems = order.getOrderItems();
		  for(OrderItem orderItem:orderItems) {
			  orderItem.setOrder(null);
		  }
	  }
	  @Cacheable(key="'orders-one-'+#p0")
	  public Order get(int oid) {
		  return orderDAO.findOne(oid);
	  }
	  @CacheEvict(allEntries=true)
	  public void update(Order order) {
		  orderDAO.save(order);
	  }
	  @CacheEvict(allEntries=true)
	  public void add(Order order) {
		  orderDAO.save(order);
	  }
	  //事务管理
	  @CacheEvict(allEntries=true)
	  @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName="Exception")
	  public float add(Order order,List<OrderItem> ois) {
		  float total = 0;
		  add(order);
		  for(OrderItem oi:ois) {
			  oi.setOrder(order);
			  orderItemService.update(oi);
			  total+=oi.getProduct().getPromotePrice() * oi.getNumber();
		  }
		  return total;
	  }
	  public List<Order> listByUserWithoutDelete(User user){
		 OrderService orderService=SpringContextUtil.getBean(OrderService.class);
		 List<Order> os = orderService.listByUserNotDelete(user);
		 orderItemService.fill(os);
		 return os;
	  }
	  @Cacheable(key="'orders-uid-'+#p0.id")
	  public List<Order> listByUserNotDelete(User user){
		  
		  return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
	  }
}
