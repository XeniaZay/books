package com.example.mvc.currentuser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
class MeController {
    @GetMapping
    CurrentUser me(@CurrentUserParam CurrentUser user) {
        return user;
    }
}