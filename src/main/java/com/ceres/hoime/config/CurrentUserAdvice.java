package com.ceres.hoime.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import com.ceres.hoime.service.impl.CustomUserDetails;

@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute
    public void addCurrentUser(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        if (user != null) {
            model.addAttribute("currentUserId", user.getId());
            model.addAttribute("currentUserName", user.getNickname());
            model.addAttribute("currentUserEmail", user.getUsername());
        }
    }
}
