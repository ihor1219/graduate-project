package com.example.pes.controller;

import com.example.pes.sort.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class HelloWorld {

    @GetMapping("/hello")
    public List hello(){

        Sort sort = new Sort();
        //ArrayList result = sort.function1();
        List result =  Arrays.asList("DK961","DK961","DK961","DK961","DK961","DK961","Longdon","Longdon","Longdon","Longdon","Longdon","Longdon","Longdon");
        return result;
    }
}
