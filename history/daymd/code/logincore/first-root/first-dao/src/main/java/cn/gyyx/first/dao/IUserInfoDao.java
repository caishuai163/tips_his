package cn.gyyx.first.dao;

import org.springframework.stereotype.Repository;

import cn.gyyx.first.beans.UserInfo;

@Repository
public interface IUserInfoDao {
	public UserInfo findByUid(int uid);
}
