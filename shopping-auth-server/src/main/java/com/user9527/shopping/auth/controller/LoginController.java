package com.user9527.shopping.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.classmate.MemberResolver;
import com.user9527.common.constant.AuthServiceConstant;
import com.user9527.common.utils.R;
import com.user9527.common.vo.MemberResVo;
import com.user9527.shopping.auth.feign.MemberFeignService;
import com.user9527.shopping.auth.vo.MemberRegistVo;
import com.user9527.shopping.auth.vo.UserLoginVo;
import com.user9527.shopping.auth.vo.UserRegistVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/17 20:10
 */
@Controller
public class LoginController {

    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * TODO 重定向携带数据 利用session原理 将数据放在session中 只要跳转到下一个页面取出数据后 session中的数据就会删除
     * 分布式session问题
     * RedirectAttributes携带数据
     *
     * @Valid BindingResult result 都是JSR303校验
     * RedirectAttributes 重定向携带数据 代替Model
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVO registVO, BindingResult result, RedirectAttributes redirectAttributes) {
        // 数据校验有问题
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://localhost:20000/reg.html";
        }

        //远程注册
        MemberRegistVo memberRegistVo = new MemberRegistVo();
        BeanUtils.copyProperties(registVO, memberRegistVo);
        R r = memberFeignService.regist(memberRegistVo);
        if (r.getCode() == 0) {
            //注册成功后回到首页，或者回到登录页
            return "redirect:http://localhost:20000/login.html";
        }else {
            //出现异常 或者 失败
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://localhost:20000/reg.html";
        }

    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {

        //远程登录
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            //远程登录成功，将远程服务返回的entity放入session中
            MemberResVo memberResVo = r.getData("data", new TypeReference<MemberResVo>(){});
            // TODO 1,默认发的令牌， session=xxxx 作用域：当前域（解决子域session共享问题）
            // TODO 2,使用JSON的序列化方式来序列化对象数据到redis中
            session.setAttribute(AuthServiceConstant.LOGIN_USER,memberResVo);
            return "redirect:http://localhost:10005";
        }else {
            //远程登录失败
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://localhost:20000/login.html";
        }
    }

    /**
     * 处理已经登录的用户，误操作到登录页面
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {

        //判断用户是否已经登录
        Object attribute = session.getAttribute(AuthServiceConstant.LOGIN_USER);
        if (attribute == null) {
            //没有登录过 可以跳转到登录页面
            return "login";
        }else {
            //已经登录，禁止跳转到登录页，跳转首页即可
            return "redirect:http://localhost:10005";
        }
    }
}
