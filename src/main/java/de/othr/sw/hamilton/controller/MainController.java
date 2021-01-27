package de.othr.sw.hamilton.controller;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class MainController {

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String showStartPage() {
        return "index";
    }

    @RequestMapping(path="/swagger-ui")
    public String showSwagger() {
        return "redirect:/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/";
    }
}
