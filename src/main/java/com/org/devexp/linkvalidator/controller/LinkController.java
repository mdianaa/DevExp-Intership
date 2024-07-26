package com.org.devexp.linkvalidator.controller;

import com.org.devexp.linkvalidator.service.LinkValidationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/links")
public class LinkController {

    private final LinkValidationService linkValidationService;

    public LinkController(LinkValidationService linkValidationService) {
        this.linkValidationService = linkValidationService;
    }

    // validate one link
    @PostMapping("/validate-link")
    public String validateLink(@RequestBody String url) {
        return linkValidationService.validateLink(url);
    }

    // validate more than one links
    @PostMapping("/validate-links")
    public String validateSitemap(@RequestBody List<String> urls) {
        StringBuilder result = new StringBuilder();

        for (String url : urls) {
            result.append(linkValidationService.validateLink(url)).append(System.lineSeparator());
        }

        return result.toString();
    }
}
