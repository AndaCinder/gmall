package com.lichen.gmall.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 李琛
 * 2019/6/27 - 11:05
 */
@Controller
public class LoginController {

    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
