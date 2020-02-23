package com.lzx.tmall.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.lzx.tmall.pojo.User;
import com.lzx.tmall.service.UserService;

public class JPARealm extends AuthorizingRealm{
	@Autowired UserService userService;
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
		return s;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// TODO Auto-generated method stub
		String userName = token.getPrincipal().toString();
		User user = userService.getByName(userName);
		String passwordInDB = user.getPassword();
		String salt = user.getSalt();
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,passwordInDB,
				ByteSource.Util.bytes(salt),
				getName()
				);
		return authenticationInfo;
	}

}
