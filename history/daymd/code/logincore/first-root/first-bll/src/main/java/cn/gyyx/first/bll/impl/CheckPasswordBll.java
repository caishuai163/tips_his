package cn.gyyx.first.bll.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.gyyx.first.beans.User;
import cn.gyyx.first.bll.ICheckPasswordBll;
import cn.gyyx.first.dao.IUserDao;

@Service
public class CheckPasswordBll implements ICheckPasswordBll {
	@Autowired
	private IUserDao iUserDao;

	public User checkUser(User user) {
		User user2=iUserDao.findByName(user.getUsername());
		System.out.println(MD5Util.MD5(MD5Util.MD5(user.getPassword())+user2.getSalt()));
		System.out.println(user2.getPassword());
		if (!user2.getPassword().equals(MD5Util.MD5(MD5Util.MD5(user.getPassword())+user2.getSalt()))) {
			user2=null;
		}
		
		
		return user2;
	}

}
