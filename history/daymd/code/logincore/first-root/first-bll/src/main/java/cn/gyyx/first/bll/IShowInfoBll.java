package cn.gyyx.first.bll;

import cn.gyyx.first.beans.User;
import cn.gyyx.first.beans.UserInfo;

public interface IShowInfoBll {
	public UserInfo findUserInfo(User user);
}
