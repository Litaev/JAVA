package com.example.sb.controllers;

import org.springframework.web.bind.annotation.*;
import org.apache.commons.text.StringEscapeUtils;

@RestController
@RequestMapping("/api")
public class CarController  {

    @GetMapping("/cars")
    public String getQueryCar(@RequestParam("name") String name) {
        String validatedName = StringEscapeUtils.escapeHtml4(name);
        return "Car name is " + validatedName;
    }

    @GetMapping("/cars/{carID}")
    public String getPathCar(@PathVariable("carID") Integer carID) {
        return "Car ID is " + carID.toString();
    }


}
