package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.service.ConsultingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class ConsultingController {

    private final ConsultingService consultingService;

    public ConsultingController(ConsultingService consultingService) {
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
        model.addAttribute("consultingRequests", consultingService.getOpenRequests());
        return "advisor";
    }

    @RequestMapping(path = "/api/advisor", method = RequestMethod.POST)
    @ResponseBody
    public Advisor showAdvisorPage(@RequestBody Advisor advisor) {
        advisor = consultingService.createAdvisor(advisor);
        return advisor;
    }

    @RequestMapping(path = "/consulting/accept/{consultingId}", method = RequestMethod.GET)
    public String acceptConsulting(@PathVariable("consultingId") UUID consultingId) {
        consultingService.acceptConsulting(consultingId);
        //TODO is das echt n GET?
        // call bei voci erstellen
        // customer iwie sagen, dass call existiert
        return "index";
    }

    @RequestMapping(path = "/consulting/join/{consultingId}", method = RequestMethod.GET )
    public String joinConsulting(@PathVariable("consultingId") UUID consultingId) {
        return consultingService.joinConsulting(consultingId);
    }
}
