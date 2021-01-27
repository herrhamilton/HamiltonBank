package de.othr.sw.hamilton.controller.rest;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.service.IConsultingService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.KeyAlreadyExistsException;

@RestController
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class ConsultingRestController {

    private final IConsultingService consultingService;

    public ConsultingRestController(IConsultingService consultingService) {
        this.consultingService = consultingService;
    }

    @ResponseBody
    @RequestMapping(path = "/api/advisor", method = RequestMethod.POST)
    public ResponseEntity<?> createAdvisor(@RequestBody Advisor advisor) {
        try {
            advisor = consultingService.createAdvisor(advisor);
            return new ResponseEntity<>(advisor, HttpStatus.OK);
        } catch (KeyAlreadyExistsException ex) {
            return new ResponseEntity<>("Username already in use. Please try again with another one.", HttpStatus.OK);
        }
    }
}
