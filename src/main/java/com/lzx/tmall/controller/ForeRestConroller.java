package com.lzx.tmall.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.lzx.tmall.comparator.ProductAllComparator;
import com.lzx.tmall.comparator.ProductDateComparator;
import com.lzx.tmall.comparator.ProductPriceComparator;
import com.lzx.tmall.comparator.ProductReviewComparator;
import com.lzx.tmall.comparator.ProductSaleCountComparator;
import com.lzx.tmall.pojo.Category;
import com.lzx.tmall.pojo.Order;
import com.lzx.tmall.pojo.OrderItem;
import com.lzx.tmall.pojo.Product;
import com.lzx.tmall.pojo.ProductImage;
import com.lzx.tmall.pojo.PropertyValue;
import com.lzx.tmall.pojo.Review;
import com.lzx.tmall.pojo.User;
import com.lzx.tmall.service.CategoryService;
import com.lzx.tmall.service.OrderItemService;
import com.lzx.tmall.service.OrderService;
import com.lzx.tmall.service.ProductImageService;
import com.lzx.tmall.service.ProductService;
import com.lzx.tmall.service.PropertyValueService;
import com.lzx.tmall.service.ReviewService;
import com.lzx.tmall.service.UserService;
import com.lzx.tmall.util.Result;

@RestController
public class ForeRestConroller {
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;
	@Autowired
	ProductImageService productImageService;
	@Autowired
	ReviewService reviewService;
	@Autowired
	OrderItemService orderItemService;
	
	@Autowired
	PropertyValueService propertyValueService;
	
	@Autowired
	OrderService orderService;
	@GetMapping("/forehome")
	public Object home() {
		List<Category> cs = categoryService.list();
		productService.fill(cs);
		productService.fillByRow(cs);
		categoryService.removeCategoryFromProduct(cs);
		return cs;
	}
	@PostMapping("/foreregister")
	public Object register(@RequestBody User user) {
		String password = user.getPassword();
		String name = user.getName();
		boolean exist = userService.isExist(name);
		if(exist) {
			String message = "用户名已经被占用";
			return Result.fail(message);
		}
		String salt = new SecureRandomNumberGenerator().nextBytes().toString();
		System.out.println(salt);
		int times = 2;
		String algorithmName = "md5";
		String encodedPassword = new SimpleHash(algorithmName,password,salt,times).toString();
		user.setSalt(salt);
		user.setPassword(encodedPassword);
		userService.add(user);
		return Result.success();
	}
	@PostMapping("/forelogin")
	public Object login(@RequestBody User userParam,HttpSession session){
		String name = userParam.getName();
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(name,userParam.getPassword());
		try {
			subject.login(token);
			User user = userService.getByName(name);
			session.setAttribute("user", user);
			return Result.success();
		}catch(AuthenticationException e) {
			return Result.fail("账号密码错误");
		}
	}
	@GetMapping("/foreproduct/{pid}")
	public Object product(@PathVariable("pid")int pid) {
			Product product = productService.get(pid);
	
			List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
			List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
			int reviewCount = reviewService.getCount(product);
			int saleCount = orderItemService.getSaleCount(product);
			
			propertyValueService.init(product);
			List<PropertyValue> pvs = propertyValueService.list(product);
			List<Review> reviews = reviewService.list(product);
			Map<String, Object> map = new HashMap<String, Object>();
			
			product.setReviewCount(reviewCount);
			product.setSaleCount(saleCount);
			product.setProductDetailImages(productDetailImages);
			product.setProductSingleImages(productSingleImages);
			productImageService.setFirstProductImage(product);
			
			map.put("product", product);
			map.put("reviews",reviews);
			map.put("pvs",pvs);
			return Result.success(map);

		
	}
	
	@GetMapping("/forecheckLogin")
	public Object checkLogin() {
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()) {
			return Result.success();
		}else {
			return Result.fail("未登录");
		}
	}
	@GetMapping("forecategory/{cid}")
	public Object category(@PathVariable("cid")int cid,String sort) throws Exception{
		Category c = categoryService.get(cid);
		//填充
		productService.fill(c);
		productService.setSaleAndReviewNumber(c.getProducts());
		categoryService.removeCategoryFromProduct(c);
		if(null != sort) {
			switch(sort) {
			case "review":
				Collections.sort(c.getProducts(), new ProductReviewComparator());    break;
            case "date" :
                Collections.sort(c.getProducts(),new ProductDateComparator());
                break;
 
            case "saleCount" :
                Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                break;
 
            case "price":
                Collections.sort(c.getProducts(),new ProductPriceComparator());
                break;
 
            case "all":
                Collections.sort(c.getProducts(),new ProductAllComparator());
                break;
			}
		}
		return c;
	}
	@PostMapping("/foresearch")
	public Object search(String keyword) {
		System.out.println(keyword);
		if(null == keyword) {
			keyword="";
		}
		List<Product> ps = productService.search(keyword, 0, 20);
		productImageService.setFirstProductImages(ps);
		productService.setSaleAndReviewNumber(ps);
		return ps;
	}
	@GetMapping("/forebuyone")
	public Object buyone(int pid,int num,HttpSession session) {
		return buyoneAndAddCart(pid,num,session);
	}
	private int buyoneAndAddCart(int pid,int num,HttpSession session) {
		Product product = productService.get(pid);
		int oiid = 0;
		User user = (User) session.getAttribute("user");
		boolean found = false;
		List<OrderItem> ois = orderItemService.listByUser(user);
		for(OrderItem oi:ois) {
			if(oi.getProduct().getId() == product.getId()) {
				oi.setNumber(oi.getNumber()+num);
				orderItemService.update(oi);
				found=true;
				oiid = oi.getId();
				break;
			}
		}
		if(!found) {
			OrderItem oi = new OrderItem();
			oi.setUser(user);
			oi.setProduct(product);
			oi.setNumber(num);
			orderItemService.add(oi);
			oiid = oi.getId();
		}
		return oiid;
	}
	
	@GetMapping("/forebuy")
	public Object forebuy(String[] oiid,HttpSession session) throws Exception{

		//订单项id 查询 total 和 订单项 
		float total = 0;
		List<OrderItem> ois = new ArrayList<OrderItem>();
		for(String oi:oiid) {
			int id = Integer.parseInt(oi);
			OrderItem orderItem = orderItemService.get(id);
			total +=orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
			ois.add(orderItem);
		}
		productImageService.setFirstProductImagesOnOrderItems(ois);
		session.setAttribute("ois", ois);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderItems",ois);
		map.put("total",total);
		return Result.success(map);			
	}
	@PostMapping("/forecreateOrder")
	public Object createOrder(@RequestBody Order order,HttpSession session) throws Exception{
		User user = (User) session.getAttribute("user");
		if(user == null) {
			return Result.fail("未登录");
		}
		String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+RandomUtils.nextInt(10000);
		order.setOrderCode(orderCode);
		order.setCreateDate(new Date());
		order.setStatus(OrderService.waitPay);
		order.setUser(user);
		List<OrderItem> ois = (List<OrderItem>)session.getAttribute("ois");
		float total = orderService.add(order, ois);
		
		Map<String,Object> map = new HashMap<String, Object>();
		//支付页面只需要一个订单id就够了
		map.put("oid",order.getId());
		map.put("total",total);
		return Result.success(map);
	}
	@GetMapping("/foreaddCart")
	public Object addCart(int pid,int num,HttpSession session) {
		buyoneAndAddCart(pid,num,session);
		return Result.success();
	}
	@GetMapping("forecart")
	public Object cart(HttpSession session) {
		User user = (User) session.getAttribute("user");
		List<OrderItem> ois = orderItemService.listByUser(user);
		productImageService.setFirstProductImagesOnOrderItems(ois);
		return ois;
	}
	@GetMapping("forechangeOrderItem")
	public Object changeOrderItem(HttpSession session,int pid,int num) {
		User user = (User) session.getAttribute("user");
		if(null == user)
			return Result.fail("未登录");
		List<OrderItem> ois = orderItemService.listByUser(user);
		for(OrderItem oi:ois) {
			if(oi.getProduct().getId() == pid) {
				oi.setNumber(num);
				orderItemService.update(oi);
				break;
			}
		}
		return Result.success();
	}
	@GetMapping("foredeleteOrderItem")
	public Object deleteOrderItem(HttpSession session,int oiid) {
		User user = (User)session.getAttribute("user");
		if(null == user)
			return Result.fail("未登录");
		orderItemService.delete(oiid);
		return Result.success();
	}
	@GetMapping("/forepayed")
	public Object payed(int oid) {
		Order order = orderService.get(oid);
		order.setStatus(OrderService.waitDelivery);
		order.setPayDate(new Date());
		orderService.update(order);
		return order;
	}
	@GetMapping("/forebought")
	public Object bought(HttpSession session) {
		User user = (User)session.getAttribute("user");
		if(user == null) {
			Result.fail("未登录");
		}
		List<Order> os = orderService.listByUserWithoutDelete(user);
		orderService.removeOrderFromOrderItem(os);
		return os;
	}
	@PutMapping("/foredeleteOrder")
	public Object deleteOrder(int oid) {
		Order o = orderService.get(oid);
		o.setStatus(OrderService.delete);
		orderService.update(o);
		return Result.success();
	}
	@GetMapping("/foreorderConfirmed")
	public Object orderConfirmed(int oid) {
		Order o = orderService.get(oid);
		o.setStatus(OrderService.waitReview);
		o.setConfirmDate(new Date());
		orderService.update(o);
		return Result.success(o);
	}
	@GetMapping("/forereview")
	public Object review(int oid) {
		Order o = orderService.get(oid);
		orderItemService.fill(o);
		orderService.removeOrderFromOrderItem(o);
		Product product = o.getOrderItems().get(0).getProduct();
		List<Review> reviews = reviewService.list(product);
		productService.setSaleAndReviewNumber(product);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("o",o);
		map.put("p",product);
		map.put("reviews",reviews);
		return Result.success(map);
	}
	@PostMapping("foredoreview")
	public Object doreview(HttpSession session,int oid,int pid,String content) {
		//订单状态需要改变
		Order o = orderService.get(oid);
		o.setStatus(OrderService.finish);
		orderService.update(o);
		
		Product p = productService.get(pid);
		content = HtmlUtils.htmlEscape(content);
		User user = (User)session.getAttribute("user");
		Review review = new Review();
		review.setContent(content);
		review.setCreateDate(new Date());
		review.setUser(user);
		review.setProduct(p);
		reviewService.add(review);
		return Result.success();
	}
}
