package com.chffy.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/demo")
@Controller
public class DemoController {
    @RequestMapping("/hello")
    public String Hello(Model model) {
        model.addAttribute("name", "hello");
        return "hello";
    }
}
