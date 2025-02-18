package com.example.sb.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CarController  {

    @GetMapping("/cars")
    private String getQueryCar(@RequestParam("name") String name, @RequestParam("mileage") Integer mileage) {
        return "Car name is " + name + " and mileage is " + mileage.toString();
    }

    @GetMapping("/cars/{carID}")
    private String getPathCar(@PathVariable("carID") Integer carID) {
        return "Car ID is " + carID.toString();
    }


}
