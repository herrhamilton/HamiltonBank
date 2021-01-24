package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.service.PaymentService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class PaymentController {

    private final UserService userService;

    private final PaymentService paymentService;

    public PaymentController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @RequestMapping(path = "/api/payment/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestHeader("api-key") UUID apiKey, @RequestBody Payment payment) {
        Customer receiver = (Customer) userService.loadUserByUsername(payment.getReceiverName());
        //TODO check apikey not empty
        if(!apiKey.equals(receiver.getHamiltonApiKey())) {
            return new ResponseEntity<>("You are not authorized to create a Payment for this username.", HttpStatus.UNAUTHORIZED);
        }
        payment = paymentService.createPayment(payment);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @RequestMapping(path = "/api/payment/check/{paymentId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> checkPayment(@RequestHeader("api-key") UUID apiKey, @PathVariable("paymentId") UUID paymentId) {
        Payment payment = paymentService.findPayment(paymentId);
        if(payment == null) {
            return new ResponseEntity<>("Payment with id '" + paymentId.toString() + "' could not be found", HttpStatus.NOT_FOUND);
        }
        Customer receiver = (Customer) userService.loadUserByUsername(payment.getReceiverName());
        //TODO check apikey not empty
        if(!apiKey.equals(receiver.getHamiltonApiKey())) {
            return new ResponseEntity<>("You are not authorized to access this Payment.", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @RequestMapping(path = "/payment/{paymentId}")
    public String showPaymentPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        //TODO Exception Handling hier und überall
        //TODO Unit Tests
        Payment payment = paymentService.findPayment(paymentId);
        Customer customer = userService.getCurrentCustomer();

        model.addAttribute("customer", customer);
        model.addAttribute("payment", payment);
        return "payment";
    }

    @RequestMapping(path = "/payment/{paymentId}/fulfill", method = RequestMethod.POST)
    public String fulfillPaymentAndShowOverviewPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        Payment payment = paymentService.findPayment(paymentId);
        paymentService.fulfillPayment(payment);

        return "redirect:/overview";
    }
}
