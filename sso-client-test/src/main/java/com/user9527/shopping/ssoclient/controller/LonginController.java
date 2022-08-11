package com.user9527.shopping.ssoclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/22 9:48
 */
@Controller
public class LonginController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "你好";
    }

    /**
     * 需要登录才能访问
     *
     * @return
     */
    @GetMapping("/employee")
    public String getEmployee(Model model) {
        List<String> emps = new ArrayList<>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps", emps);
        return "list";
    }

}
