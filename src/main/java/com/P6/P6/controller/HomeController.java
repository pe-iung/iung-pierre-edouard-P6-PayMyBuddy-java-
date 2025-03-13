package com.P6.P6.controller;

import com.P6.P6.service.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        if(SecurityHelper.isConnected()) {
            return "redirect:/account";
        }
        return "redirect:/login";
   }

}