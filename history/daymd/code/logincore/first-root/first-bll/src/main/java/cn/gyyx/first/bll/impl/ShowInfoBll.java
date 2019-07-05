package cn.gyyx.first.bll.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.gyyx.first.beans.User;
import cn.gyyx.first.beans.UserInfo;
import cn.gyyx.first.bll.IShowInfoBll;
import cn.gyyx.first.dao.IUserInfoDao;

@Service
public class ShowInfoBll implements IShowInfoBll {
	@Autowired
	private IUserInfoDao iUserInfoDao;

	public UserInfo findUserInfo(User user) {
		return iUserInfoDao.findByUid(user.getId());
	}

}
