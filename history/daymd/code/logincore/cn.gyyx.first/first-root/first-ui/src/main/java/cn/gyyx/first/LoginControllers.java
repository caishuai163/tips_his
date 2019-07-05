package cn.gyyx.first;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.gyyx.first.beans.User;
import cn.gyyx.first.beans.UserInfo;
import cn.gyyx.first.bll.IShowInfoBll;
import cn.gyyx.first.service.IUserLoginService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class LoginControllers {

	private static final Logger logger = LoggerFactory.getLogger(LoginControllers.class);
	@Autowired
	private IUserLoginService iUserLoginService;
	@Autowired
	private IShowInfoBll iShowInfoBll;

	private User user;
	private UserInfo userInfo;

	@RequestMapping(value = "/ui.login.do", method = RequestMethod.POST)
	public ModelAndView login(User user, HttpServletRequest request, HttpServletResponse response) {
		try {
			userInfo = iUserLoginService.login(user);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (userInfo == null) {
			return new ModelAndView("/login.jsp", "message", "登录失败");
		} else {
			Cookie status = new Cookie("status", userInfo.getUid() + "");
			// 设置cookie过期时间为5min。
			status.setMaxAge(60 * 5);
			// 在响应头部添加cookie
			response.addCookie(status);
			return new ModelAndView("redirect:/index.jsp", "userInfo", userInfo);
		}

	}

	@RequestMapping(value = "/ui.getCookies.do", method = RequestMethod.GET)
	public ModelAndView showCookies(HttpServletRequest request, HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();// 这样便可以获取一个cookie数组
		if (null == cookies) {
			System.out.println("没有cookie=========");
		} else {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("status")) {
					userInfo = iShowInfoBll.findUserInfo(new User(Integer.parseInt(cookie.getValue()), "", ""));
					if (userInfo != null) {
						Cookie status = new Cookie("status", userInfo.getUid() + "");
						// 设置cookie过期时间为1小时。
						status.setMaxAge(3600);
						// 在响应头部添加cookie
						response.addCookie(status);
						return new ModelAndView("/index.jsp", "userInfo", userInfo);
					}
				}
			}
		}
		return new ModelAndView("/login.jsp", "message", "身份过期，请重新登陆");
	}

}
