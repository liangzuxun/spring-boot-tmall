package com.lzx.tmall.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lzx.tmall.pojo.User;

public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		String contextPath = session.getServletContext().getContextPath();
		String[] requireAuthPages = new String[] {
				"buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",
                 
                "forebuyone",
                "forebuy",
                "foreaddCart",
                "forecart",
                "forechangeOrderItem",
                "foredeleteOrderItem",
                "forecreateOrder",
                "forepayed",
                "forebought",
                "foreconfirmPay",
                "foreorderConfirmed",
                "foredeleteOrder",
                "forereview",
                "foredoreview"
		};
		String uri = request.getRequestURI();
		
		uri = StringUtils.remove(uri, contextPath+"/");
		
		
		String page = uri;
		System.out.println(page);
		//如果是需要拦截的
		if(beginWith(page,requireAuthPages)) {
			//检查是否登录
			User user = (User)session.getAttribute("user");
			
			if(user == null) {
				response.sendRedirect("login");
				return false;
			}
		}
		// TODO Auto-generated method stub
		return true;
	}
	private boolean beginWith(String page,String[] requireAuthPages) {
		boolean result = false;
		for(String requireAuthPage:requireAuthPages) {
			
			if(StringUtils.startsWith(page,requireAuthPage)) {
			
				result = true;
				break;
			}
		}
		return result;
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
