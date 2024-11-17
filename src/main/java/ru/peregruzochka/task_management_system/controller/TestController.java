package ru.peregruzochka.task_management_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.JwtAuthResponse;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JwtAuthResponse test() {
        return new JwtAuthResponse("success");
    }
}