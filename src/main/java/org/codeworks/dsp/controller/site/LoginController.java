package org.codeworks.dsp.controller.site;

import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.DspConstant;
import org.codeworks.dsp.controller.AbstractController;
import org.codeworks.dsp.model.dto.Response;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.groups.Login;
import org.codeworks.dsp.model.views.advertiser.ListView;
import org.codeworks.dsp.model.views.advertiser.LoginView;
import org.codeworks.dsp.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 广告主/管理员鉴权登录接口
 * <p>
 * 提供用户名密码登录与注销登出功能，维护安全 Session 上下文。
 * </p>
 *
 * @author Luis
 */
@Api(tags = "用户认证与登录接口", description = "提供广告主与系统管理员账号的会话登录与登出认证服务")
@RequestMapping("/v2/login")
@RestController("/v2/LoginController")
public class LoginController extends AbstractController {

    @Autowired
    private LoginService loginService;

    /**
     * 用户登录认证
     *
     * @param adv 登录参数（包含用户名与密码）
     * @param result 参数校验结果
     * @param session 当前 HTTP 会话对象
     * @return 登录成功后的用户信息响应
     */
    @ApiOperation(value = "用户登录", notes = "校验广告主账号密码，验证通过后建立用户会话 Session 并返回广告主信息")
    @JsonView(LoginView.class)
    @PostMapping
    public Response login(@ApiParam(value = "登录参数（账号名与密码）", required = true) @Validated(Login.class) Advertiser adv,
                         BindingResult result,
                         @ApiParam(hidden = true) HttpSession session) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        Optional<Advertiser> advOpt = loginService.login(adv);
        if (advOpt.isPresent()) {
            session.setAttribute(DspConstant.LOGIN_USER_SESSION_KEY, advOpt.get());
            return Response.ok("advertiser", advOpt.get());
        } else
            return Response.badRequest(getMessage("login.not.found"));
    }

    /**
     * 用户注销登出
     *
     * @param session 当前 HTTP 会话对象
     * @return 注销成功响应
     */
    @ApiOperation(value = "用户登出", notes = "销毁当前用户的登录会话 Session")
    @DeleteMapping
    public Response logout(@ApiParam(hidden = true) HttpSession session) {
        session.invalidate();
        return Response.ok();
    }
}
