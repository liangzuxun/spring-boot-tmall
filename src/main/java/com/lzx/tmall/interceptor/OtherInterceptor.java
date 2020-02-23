package com.lzx.tmall.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lzx.tmall.pojo.Category;
import com.lzx.tmall.pojo.OrderItem;
import com.lzx.tmall.pojo.User;
import com.lzx.tmall.service.CategoryService;
import com.lzx.tmall.service.OrderItemService;

public class OtherInterceptor implements HandlerInterceptor{
	@Autowired CategoryService categoryService;
	@Autowired OrderItemService orderItemService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int cartTotalItemNumber = 0;
		if(null != user) {
			List<OrderItem> ois = orderItemService.listByUser(user);
			for(OrderItem oi:ois) {
				cartTotalItemNumber+=oi.getNumber();
			}
		}
		List<Category> cs = categoryService.list();
		String contextPath = request.getServletContext().getContextPath();
		request.getServletContext().setAttribute("categories_below_search", cs);
		session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
		request.getServletContext().setAttribute("contextPath", contextPath);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
