package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.service.IConsultingService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(path="/consulting")
public class ConsultingController {

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    private final IUserService userService;

    private final IConsultingService consultingService;

    public ConsultingController(IUserService userService, IConsultingService consultingService) {
        this.userService = userService;
        this.consultingService = consultingService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String showConsultingPage(Model model) {
        Customer customer = userService.getCurrentCustomer();

        Consulting consulting = customer.getOpenConsulting();

        if (consulting != null) {
            model.addAttribute("hasConsulting", true);
            model.addAttribute("consulting", consulting);
            model.addAttribute("consultingUrl", vociUrl + "/invitation?accessToken=" + consulting.getAccessToken());
        } else {
            model.addAttribute("hasConsulting", false);
            model.addAttribute("consulting", new Consulting());
        }
        return "consulting";
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public String createConsulting(@ModelAttribute Consulting consulting) {
        consultingService.createConsulting(consulting);
        return "redirect:/consulting";
    }

    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    public String cancelConsulting() {
        consultingService.cancelConsulting();
        return "redirect:/consulting";
    }
}
