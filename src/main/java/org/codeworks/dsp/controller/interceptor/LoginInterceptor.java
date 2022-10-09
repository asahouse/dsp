package org.codeworks.dsp.controller.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.exception.ErrorCodes;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 * Created by Luis on 2016/6/3.
 */
public class LoginInterceptor extends AbstractController implements HandlerInterceptor {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object o) throws
            Exception {
        HttpSession session = req.getSession();
        Advertiser adv = (Advertiser) session.getAttribute(DspConstant.LOGIN_USER_SESSION_KEY);
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "application/json;charset=UTF-8");
        if (adv != null) {
            if (StringUtils.isEmpty(req.getParameter("advId")))
                return true;
            else {
                if (adv.getId().equals(Integer.parseInt(req.getParameter("advId"))))
                    return true;
                else
                    resp.getWriter().write(
                            objectMapper.writeValueAsString(
                                    Response.resp(HttpStatus.FORBIDDEN, getMessage(ErrorCodes.permission_denied.getCode()))));
            }
        } else
            resp.getWriter().write(
                    objectMapper.writeValueAsString(
                            Response.resp(HttpStatus.UNAUTHORIZED, getMessage(ErrorCodes.require_login.getCode()))));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object o, Exception e) throws Exception {
    }
}