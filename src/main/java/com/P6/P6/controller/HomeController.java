package com.P6.P6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        //return "home";
        return "redirect:/account";
   }


}