package com.atguigu.gmall.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//测试index.html
@Controller
public class ManagerController {

    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
