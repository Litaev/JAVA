package com.example.sb.controllers;

import org.springframework.web.bind.annotation.*;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CarController  {

    @GetMapping("/cars")
    public Map<String, String> getQueryCar(@RequestParam("name") String name) {
        String validatedName = StringEscapeUtils.escapeHtml4(name);
        return Map.of("carName", validatedName);
    }

    @GetMapping("/cars/{carID}")
    public Map<String, Integer> getPathCar(@PathVariable("carID") Integer carID) {
        return Map.of("carID", carID);
    }


}
