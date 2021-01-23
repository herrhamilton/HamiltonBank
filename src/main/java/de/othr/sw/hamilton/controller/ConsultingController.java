package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.service.ConsultingService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class ConsultingController {

    //TODO userService da lassen oder hasAccepted.. Methode in cons Service?
    private final UserService userService;

    private final ConsultingService consultingService;

    public ConsultingController(UserService userService, ConsultingService consultingService) {
        this.userService = userService;
        this.consultingService = consultingService;
    }

    @RequestMapping(path ="/consulting", method = RequestMethod.GET)
    public String showConsultingPage(Model model) {
        Consulting consulting = consultingService.getRequestForCurrentCustomer();
        model.addAttribute("consulting", consulting);
        return "consulting";
    }

    @RequestMapping(path = "/consulting", method = RequestMethod.POST)
    public String createConsulting(@ModelAttribute Consulting consulting) {
        consultingService.createConsulting(consulting);
        return "redirect:consulting";
    }

    @RequestMapping(path = "/advisor", method = RequestMethod.GET)
    public String showAdvisorPage(Model model) {
        Advisor advisor = (Advisor)userService.getCurrentUser();
        if(advisor.getRunningConsulting() != null) {
            model.addAttribute("consulting", advisor.getRunningConsulting());
            return "accepted";
        } else {
            model.addAttribute("consultingRequests", consultingService.getOpenRequests());
            return "advisor";
        }
    }

    @RequestMapping(path = "/api/advisor", method = RequestMethod.POST)
    @ResponseBody
    public Advisor showAdvisorPage(@RequestBody Advisor advisor) {
        advisor = consultingService.createAdvisor(advisor);
        return advisor;
    }

    @RequestMapping(path = "/consulting/accept/{consultingId}", method = RequestMethod.POST)
    public String acceptConsulting(@PathVariable("consultingId") UUID consultingId, Model model) {
        Consulting consulting = consultingService.acceptConsulting(consultingId);
        model.addAttribute("consulting", consulting);
        return "accepted";
    }

    @RequestMapping(path = "/consulting/close/{consultingId}", method = RequestMethod.POST)
    public String closeConsulting(@PathVariable("consultingId") UUID consultingId) {
        consultingService.closeConsulting(consultingId);
        return "advisor";
    }
}
