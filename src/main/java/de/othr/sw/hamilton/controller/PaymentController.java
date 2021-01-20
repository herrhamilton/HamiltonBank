package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
//TODO move /payment/ into "root" path ? problem with /api
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(path = "/api/payment/create", method = RequestMethod.POST)
    @ResponseBody
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }

    @RequestMapping(path = "/api/payment/check/{paymentId}", method = RequestMethod.GET)
    @ResponseBody
    public Payment checkPayment(@PathVariable("paymentId") UUID paymentId) {
        //TODO maybe auth, sodass nicht jeder alle Payments checken kann?
        return paymentService.findPayment(paymentId);
    }

    @RequestMapping(path = "/payment/{paymentId}")
    public String showPaymentPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        //TODO Exception Handling hier und überall
        //TODO Unit Tests
        Payment payment = paymentService.findPayment(paymentId);
        model.addAttribute("payment", payment);
        return "payment";
    }

    @RequestMapping(path = "/payment/{paymentId}/fulfill", method = RequestMethod.POST)
    public String fulfillPaymentAndShowOverviewPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        Payment payment = paymentService.findPayment(paymentId);
        paymentService.fulfillPayment(payment);

        return "redirect:overview";
    }
}
