package com.nix.video.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 11723
 */
@SpringBootApplication
@Controller
public class ConferenceWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConferenceWebApplication.class, args);
    }
    @GetMapping("/check")
    public String check() {
        return "SUCCESS";
    }
}
