package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.service.IPaymentService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(value="/payment")
public class PaymentController {

    private final IUserService userService;

    private final IPaymentService paymentService;

    public PaymentController(IUserService userService, IPaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @RequestMapping(path = "/{paymentId}")
    public String showPaymentPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        Customer customer = userService.getCurrentCustomer();
        model.addAttribute("customer", customer);

        try {
            Payment payment = paymentService.findPayment(paymentId);
            model.addAttribute("payment", payment);
        } catch (NoSuchElementException ex) {
            model.addAttribute("notFound", true);
        }
        return "payment";
    }

    @RequestMapping(path = "/{paymentId}/fulfill", method = RequestMethod.POST)
    public String fulfillPaymentAndShowOverviewPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        try {
            Payment payment = paymentService.findPayment(paymentId);
            paymentService.fulfillPayment(payment);
            return "redirect:/overview";
        } catch (NoSuchElementException ex) {
            model.addAttribute("notFound", true);
            return "payment";
        }
    }
}
