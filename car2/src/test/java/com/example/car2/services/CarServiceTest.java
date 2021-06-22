package com.example.car2.services;

import com.example.car2.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CarServiceTest {

    CarService carService;


    @BeforeEach
    void setUp() {
        carService = new CarService();

    }

    @Test
    void getAll() {
        List<Car> result = carService.getAll();

        assertThat(result)
                .hasSize(2)
                .extracting(Car::getBrand)
                .contains("Suzuki","Opel");
    }

    @Test
    void getBrands() {
        List<String> result = carService.getBrands();

        assertThat(result)
                .hasSize(2)
                .contains("Suzuki","Opel");
    }
}