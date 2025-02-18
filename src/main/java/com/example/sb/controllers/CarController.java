package com.example.sb.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CarController  {

    @GetMapping("/cars")
    public String getQueryCar(@RequestParam("name") String name) {
        return "Car name is " + name;
    }

    @GetMapping("/cars/{carID}")
    public String getPathCar(@PathVariable("carID") Integer carID) {
        return "Car ID is " + carID.toString();
    }


}
