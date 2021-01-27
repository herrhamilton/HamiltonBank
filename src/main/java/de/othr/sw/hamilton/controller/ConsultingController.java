package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.service.IConsultingService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.UUID;

@Controller
public class ConsultingController {

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    private final IUserService userService;

    private final IConsultingService consultingService;

    public ConsultingController(IUserService userService, IConsultingService consultingService) {
        this.userService = userService;
        this.consultingService = consultingService;
    }

    @RequestMapping(path = "/api/advisor", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createAdvisor(@RequestBody Advisor advisor) {
        try {
            advisor = consultingService.createAdvisor(advisor);
            return new ResponseEntity<>(advisor, HttpStatus.OK);
        } catch(KeyAlreadyExistsException ex) {
            return new ResponseEntity<>("Username already in use. Please try again with another one.", HttpStatus.OK);
        }
    }

    @RequestMapping(path ="/consulting", method = RequestMethod.GET)
    public String showConsultingPage(Model model) {
        Customer customer = userService.getCurrentCustomer();
        Consulting consulting = customer.getPendingConsulting();
        if(consulting == null) {
            model.addAttribute("hasConsulting", false);
            model.addAttribute("consulting", new Consulting());
        } else {
            model.addAttribute("hasConsulting", true);
            model.addAttribute("consulting", consulting);
            model.addAttribute("consultingUrl", vociUrl + "/invitation?accessToken=" + consulting.getAccessToken());
        }
        return "consulting";
    }

    @RequestMapping(path = "/advisor", method = RequestMethod.GET)
    public String showAdvisorPage(Model model) {
        Advisor advisor = (Advisor)userService.getCurrentUser();
        if(advisor.getRunningConsulting() != null) {
            model.addAttribute("consulting", advisor.getRunningConsulting());
            //TODO rename templates
            return "accepted";
        } else {
            model.addAttribute("consultingRequests", consultingService.getOpenConsultings());
            model.addAttribute("acceptFailed", false);
            return "advisor";
        }
    }

    @RequestMapping(path = "/consulting", method = RequestMethod.POST)
    public String createConsulting(@ModelAttribute Consulting consulting, Model model) {
        consultingService.createConsulting(consulting);
        model.addAttribute("consultingUrl", vociUrl + "/invitation?accessToken=" + consulting.getAccessToken());
        return "redirect:/consulting";
    }

    @RequestMapping(path = "/consulting/accept/{consultingId}", method = RequestMethod.POST)
    public String acceptConsulting(@PathVariable("consultingId") UUID consultingId, Model model) {
        Consulting consulting;
        try {
            consulting = consultingService.acceptConsulting(consultingId);
            model.addAttribute("consulting", consulting);
            model.addAttribute("advisorUrl",  vociUrl + "/call?=" + consulting.getAccessToken());
        } catch(HttpClientErrorException | HttpServerErrorException e) {
            model.addAttribute("acceptFailed", true);
            model.addAttribute("consultingRequests", consultingService.getOpenConsultings());
            return "advisor";
        }
        return "accepted";
    }

    @RequestMapping(path = "/consulting/close/{consultingId}", method = RequestMethod.POST)
    public String closeConsulting(@PathVariable("consultingId") UUID consultingId) {
        consultingService.closeConsulting(consultingId);
        return "advisor";
    }

    @RequestMapping(path = "/consulting/cancel", method = RequestMethod.POST)
    public String cancelConsulting() {
        consultingService.cancelConsulting();
        return "redirect:/consulting";
    }
}
