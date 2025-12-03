package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private EmailService emailService;

    @GetMapping("/send-email")
    public String sendEmail() {
        emailService.sendWelcomeEmail("ygahamanyi26@gmail.com", "Yvette");
        return "Welcome";
    }
}