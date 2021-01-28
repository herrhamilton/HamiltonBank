package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.service.IConsultingService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.UUID;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(path="/advisor")
public class AdvisorController {

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    private final IUserService userService;

    private final IConsultingService consultingService;

    public AdvisorController(IUserService userService, IConsultingService consultingService) {
        this.userService = userService;
        this.consultingService = consultingService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String showAdvisorPage(Model model) {
        User currentUser = userService.getCurrentUser();
        if(currentUser instanceof Customer) {
            return "redirect:/overview";
        }
        Advisor advisor = (Advisor) currentUser;
        Consulting consulting = advisor.getRunningConsulting();

        if (consulting != null) {
            model.addAttribute("advisorUrl", vociUrl + "/call?accessToken=" + consulting.getAccessToken());
            model.addAttribute("consulting", consulting);
            return "accepted";
        } else {
            model.addAttribute("consultingRequests", consultingService.getOpenConsultings());
            model.addAttribute("acceptFailed", false);
            return "advisor";
        }
    }

    @RequestMapping(path = "/accept/{consultingId}", method = RequestMethod.POST)
    public String acceptConsulting(@PathVariable("consultingId") UUID consultingId, Model model) {
        try {
            Consulting consulting = consultingService.acceptConsulting(consultingId);
            model.addAttribute("consulting", consulting);
            model.addAttribute("advisorUrl", vociUrl + "/call?accessToken=" + consulting.getAccessToken());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            model.addAttribute("acceptFailed", true);
            model.addAttribute("consultingRequests", consultingService.getOpenConsultings());
            return "advisor";
        }
        return "accepted";
    }

    @RequestMapping(path = "/close/{consultingId}", method = RequestMethod.POST)
    public String closeConsulting(@ModelAttribute Consulting consulting, @PathVariable("consultingId") UUID consultingId) {
        consultingService.closeConsulting(consulting.getSummary(), consultingId);
        return "redirect:/advisor";
    }
}
