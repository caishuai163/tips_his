package cn.gyyx.first.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.gyyx.first.beans.User;

@Repository
public interface IUserDao {
	public User findByName(String username);
}
