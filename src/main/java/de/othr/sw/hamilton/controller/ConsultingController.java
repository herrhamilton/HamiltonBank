package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.service.IConsultingService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(path="/consulting")
public class ConsultingController {

    private final IConsultingService consultingService;

    public ConsultingController(IConsultingService consultingService) {
        this.consultingService = consultingService;
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public String createConsulting(@ModelAttribute Consulting consulting) {
        consultingService.createConsulting(consulting);
        return "redirect:/overview";
    }

    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    public String cancelConsulting() {
        consultingService.cancelConsulting();
        return "redirect:/overview";
    }
}
