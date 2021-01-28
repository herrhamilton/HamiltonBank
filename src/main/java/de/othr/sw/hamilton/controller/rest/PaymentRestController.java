package de.othr.sw.hamilton.controller.rest;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.PaymentRequest;
import de.othr.sw.hamilton.service.IPaymentService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(path = "/api/payment")
public class PaymentRestController {

    private final IUserService userService;

    private final IPaymentService paymentService;

    public PaymentRestController(IUserService userService, IPaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Payment> createPayment(@RequestHeader("api-key") UUID apiKey, @Valid @RequestBody PaymentRequest paymentRequest) {
        Customer receiver = (Customer) userService.loadUserByUsername(paymentRequest.getReceiverName());

        if (receiver.getHamiltonApiKey().equals(apiKey)) {
            Payment payment = paymentService.createPayment(paymentRequest);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("API Key does not belong to username");
        }
    }

    @RequestMapping(path = "/check/{paymentId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Payment> checkPayment(@RequestHeader("api-key") UUID apiKey, @PathVariable("paymentId") UUID paymentId) {
        Payment payment = paymentService.findPayment(paymentId);
        Customer receiver = (Customer) userService.loadUserByUsername(payment.getReceiverName());

        if (receiver.getHamiltonApiKey().equals(apiKey)) {
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("API Key does not belong to username");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    private ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

     /*   errorMessage looks like "amount: must be greater than 0.00, description: cannot be empty" */
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
