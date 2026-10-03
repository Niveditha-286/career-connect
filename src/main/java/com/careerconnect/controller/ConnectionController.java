package com.careerconnect.controller;

import com.careerconnect.dto.ConnectionResponse;
import com.careerconnect.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/request/{receiverId}")
    public ResponseEntity<ConnectionResponse> sendRequest(
            Authentication authentication,
            @PathVariable Long receiverId) {

        String senderEmail = authentication.getName();

        ConnectionResponse response =
                connectionService.sendRequest(
                        senderEmail,
                        receiverId
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @PutMapping("/{connectionId}/accept")
    public ResponseEntity<Void> acceptRequest(
            Authentication authentication,
            @PathVariable Long connectionId) {

        String receiverEmail = authentication.getName();

        connectionService.acceptRequest(
                receiverEmail,
                connectionId
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{connectionId}/reject")
    public ResponseEntity<Void> rejectRequest(
            Authentication authentication,
            @PathVariable Long connectionId) {

        String receiverEmail = authentication.getName();

        connectionService.rejectRequest(
                receiverEmail,
                connectionId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequests(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                connectionService.getPendingRequests(email)
        );
    }

    @GetMapping("/sent")
    public ResponseEntity<List<ConnectionResponse>> getSentRequests(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                connectionService.getSentRequests(email)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(
            Authentication authentication) {

        

        String email = authentication.getName();

        return ResponseEntity.ok(
                connectionService.getConnections(email)
        );
    }
}