package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.dto.PaymentVerifyRequest;
import com.example.demo.service.UserService;
import org.json.JSONObject;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

  @Value("${razorpay.key.id}")
  private String keyId;

  @Value("${razorpay.key.secret}")
  private String keySecret;

  @Value("${razorpay.amount}")
  private int amount;

  @Autowired
  private UserService userService;

  @PostMapping("/create-order")
  public ResponseEntity<?> createOrder(
      @AuthenticationPrincipal UserDetails userDetails) {
    
    // Add detailed logging to find exact error
    try {
      System.out.println("=== CREATE ORDER CALLED ===");
      System.out.println("KeyId: " + keyId);
      System.out.println("Amount: " + amount);
      System.out.println("User: " + userDetails.getUsername());

      RazorpayClient client = new RazorpayClient(keyId, keySecret);
      
      JSONObject options = new JSONObject();
      options.put("amount", amount);
      options.put("currency", "INR");
      options.put("receipt", "rcpt_" + System.currentTimeMillis());

      Order order = client.orders.create(options);
      
      System.out.println("Order created: " + order.get("id"));

      Map<String, Object> response = new HashMap<>();
      response.put("orderId", order.get("id").toString());
      response.put("amount", amount);
      response.put("currency", "INR");
      response.put("keyId", keyId);

      return ResponseEntity.ok(response);

    } catch (RazorpayException e) {
      System.err.println("Razorpay error: " + e.getMessage());
      return ResponseEntity.status(500)
        .body(Map.of("error", "Razorpay error: " + e.getMessage()));

    } catch (Exception e) {
      System.err.println("General error: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(500)
        .body(Map.of("error", "Order failed: " + e.getMessage()));
    }
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verifyPayment(
      @RequestBody PaymentVerifyRequest req,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      System.out.println("=== VERIFY PAYMENT CALLED ===");
      System.out.println("OrderId: " + req.getOrderId());
      System.out.println("PaymentId: " + req.getPaymentId());

      // Verify Razorpay signature
      String payload = req.getOrderId() + "|" + req.getPaymentId();
      String generated = hmacSHA256(payload, keySecret);

      if (!generated.equals(req.getSignature())) {
        System.err.println("Signature mismatch!");
        return ResponseEntity.status(400)
          .body(Map.of("error", "Invalid payment signature"));
      }

      // Upgrade user to PRO in DB
      userService.upgradeUserToPro(userDetails.getUsername());
      
      System.out.println("User upgraded to PRO: " + userDetails.getUsername());

      return ResponseEntity.ok(Map.of(
        "success", true,
        "message", "You are now PRO!"
      ));

    } catch (Exception e) {
      System.err.println("Verify error: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(500)
        .body(Map.of("error", "Verification failed: " + e.getMessage()));
    }
  }

  private String hmacSHA256(String data, String secret) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(
      secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
    ));
    byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder();
    for (byte b : hash) sb.append(String.format("%02x", b));
    return sb.toString();
  }
}