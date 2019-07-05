package cn.gyyx.first.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.gyyx.first.beans.User;
import cn.gyyx.first.beans.UserInfo;
import cn.gyyx.first.bll.ICheckPasswordBll;
import cn.gyyx.first.bll.IShowInfoBll;
import cn.gyyx.first.service.IUserLoginService;

@Service
@Transactional

public class UserLoginService implements IUserLoginService {
	@Autowired
	private ICheckPasswordBll iCheckPasswordBll;
	@Autowired
	private IShowInfoBll iShowInfoBll;
	
	public UserInfo login(User user) {
		user = iCheckPasswordBll.checkUser(user);
		if (user == null) {
			return null;
		} else {
			return iShowInfoBll.findUserInfo(user);
		}

	}

}
