package com.example.aihou.controller;

import com.example.aihou.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class test {
    @GetMapping("/test")
    public Result<String> test(){
       // Rusuit rusuit = new Rusuit();
        //rusuit.setCode("200");
       // System.out.println(rusuit.getCode());
        return Result.success();
    }
}
