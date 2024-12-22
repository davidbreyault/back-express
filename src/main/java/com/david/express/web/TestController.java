package com.david.express.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
public class TestController {

    @GetMapping("/read")
    public String allAccess() {
        return "Public content";
    }

    @GetMapping("/write")
    @PreAuthorize("hasRole('WRITER') or hasRole('ADMIN')")
    public String userAccess() {
        return "Writer resources";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin resources";
    }
}