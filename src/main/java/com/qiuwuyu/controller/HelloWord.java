package com.qiuwuyu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author paralog
 * @date 2022/10/7 10:43
 */
@RestController
public class HelloWord {

    @RequestMapping("/helloWorld")
    public String helloWorld(){
        return "hello World";
    }

}
