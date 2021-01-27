package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.service.IPaymentService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class PaymentController {

    private final IUserService userService;

    private final IPaymentService paymentService;

    public PaymentController(IUserService userService, IPaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @RequestMapping(path = "/api/payment/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestHeader("api-key") UUID apiKey, @Valid @RequestBody Payment payment) {
        try {
            Customer receiver = (Customer) userService.loadUserByUsername(payment.getReceiverName());

            if (receiver.getHamiltonApiKey().equals(apiKey)) {
                payment = paymentService.createPayment(payment);
                return new ResponseEntity<>(payment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You cannot create a Payment for this username.", HttpStatus.UNAUTHORIZED);
            }
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Could not parse request. Please check your input", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/api/payment/check/{paymentId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> checkPayment(@RequestHeader("api-key") UUID apiKey, @PathVariable("paymentId") UUID paymentId) {
        try {
            Payment payment = paymentService.findPayment(paymentId);
            Customer receiver = (Customer) userService.loadUserByUsername(payment.getReceiverName());
            if (!receiver.getHamiltonApiKey().equals(apiKey)) {
                return new ResponseEntity<>("You cannot access this Payment.", HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Payment with id '" + paymentId.toString() + "' could not be found", HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/payment/{paymentId}")
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

    @RequestMapping(path = "/payment/{paymentId}/fulfill", method = RequestMethod.POST)
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

    /* catches MethodArgumentNotValidException
    *  Answers with BAD_REQUEST and error string like "receiverName: cannot be empty" */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        List<FieldError> fieldErrors = objectErrors.stream()
                .map(err -> (FieldError) err)
                .collect(Collectors.toList());

        String errorMessage = fieldErrors.stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(",\n"));

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
