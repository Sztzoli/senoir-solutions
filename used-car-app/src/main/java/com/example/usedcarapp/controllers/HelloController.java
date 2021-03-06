package com.example.usedcarapp.controllers;

import com.example.usedcarapp.services.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final HelloService helloService;


    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/")
    public String hello() {
        return helloService.sayHello();
    }
}
