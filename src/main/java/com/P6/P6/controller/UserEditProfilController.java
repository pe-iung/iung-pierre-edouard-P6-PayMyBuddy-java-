package com.P6.P6.controller;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.DTO.UserEditProfilRequest;
import com.P6.P6.DTO.UserProfilDisplay;
import com.P6.P6.model.UserEntity;
import com.P6.P6.service.AccountServiceImpl;
import com.P6.P6.service.SecurityHelper;
import com.P6.P6.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserEditProfilController {
    private final UserService userService;

    @GetMapping("/user-edit-profil")
    public String userEditProfilForm(Model model) {

        if (!model.containsAttribute("userEditProfilRequest")) {
            model.addAttribute("userEditProfilRequest", new UserEditProfilRequest());
        }

        UserEntity connectedUser = SecurityHelper.getConnectedUser();
        UserProfilDisplay userProfilDisplay = new UserProfilDisplay(connectedUser);
        model.addAttribute("userProfilDisplay", userProfilDisplay);
        return "user-edit-profil";
    }

    @PostMapping("/user-edit-profil")
    public String userEditProfil(
            @Valid @ModelAttribute("userEditProfilRequest") UserEditProfilRequest userEditProfilRequest,
            BindingResult bindingResult,
            Errors errors,
            Model model
    ) {

        log.debug("Validation errors: {}", bindingResult.getAllErrors());

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream().map(ObjectError::toString).toList();

            model.addAttribute("errorMessages" , errorMessages);
            return "redirect:/user-edit-profil";
        }

        try {
            Integer currentUserId = SecurityHelper.getConnectedUser().getId();
            Integer userId= userService.editUser(currentUserId, userEditProfilRequest);
            model.addAttribute(
                    "successMessage",
                    "user edited succesfully !"
            );

            log.info("the user edit profil request {} has been saved with id {}",userEditProfilRequest, userId );

            return "redirect:/user-edit-profil";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userEditProfilRequest", userEditProfilRequest);
            return "redirect:/user-edit-profil";
        }
    }
}