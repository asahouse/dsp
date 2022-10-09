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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 登录接口
 * Created by Luis on 2016/8/29.
 */
@RequestMapping("/v2/login")
@RestController("/v2/LoginController")
public class LoginController extends AbstractController {

    @Autowired
    private LoginService loginService;

    @JsonView(LoginView.class)
    @GetMapping
    public Response login(@Validated(Login.class) Advertiser adv, BindingResult result, HttpSession session) {
        if (result.hasErrors())
            return Response.badRequest(getMessage(result.getFieldError()));

        Optional<Advertiser> advOpt = loginService.login(adv);
        if (advOpt.isPresent()) {
            session.setAttribute(DspConstant.LOGIN_USER_SESSION_KEY, advOpt.get());
            return Response.ok("advertiser", advOpt.get());
        } else
            return Response.badRequest(getMessage("login.not.found"));

    }

    @DeleteMapping
    public Response logout(HttpSession session) {
        session.invalidate();
        return Response.ok();
    }
}
