package com.careerconnect.service;

import com.careerconnect.dto.ConnectionResponse;
import com.careerconnect.entity.Connection;
import com.careerconnect.entity.ConnectionStatus;
import com.careerconnect.entity.User;
import com.careerconnect.repository.ConnectionRepository;
import com.careerconnect.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public ConnectionService(
            ConnectionRepository connectionRepository,
            UserRepository userRepository) {

        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ConnectionResponse sendRequest(
            String senderEmail,
            Long receiverId) {

        log.info("Sending connection request from {} to user ID {}",
                senderEmail, receiverId);

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow();

        User receiver = userRepository.findById(receiverId)
                .orElseThrow();

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException(
                    "You cannot send a connection request to yourself"
            );
        }

        boolean requestExists =
                connectionRepository
                        .findBySenderAndReceiver(sender, receiver)
                        .isPresent();

        if (requestExists) {
            throw new RuntimeException(
                    "Connection request already exists"
            );
        }

        boolean reverseRequestExists =
                connectionRepository
                        .findBySenderAndReceiver(receiver, sender)
                        .isPresent();

        if (reverseRequestExists) {
            throw new RuntimeException(
                    "A connection request already exists from this user"
            );
        }

        Connection connection = new Connection();

        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);

        Connection savedConnection =
                connectionRepository.save(connection);

        log.info("Connection request created successfully. Connection ID: {}",
                savedConnection.getId());

        return convertToResponse(savedConnection);
    }

    @Transactional
    public void acceptRequest(
            String receiverEmail,
            Long connectionId) {

        log.info("User {} is accepting connection ID {}",
                receiverEmail, connectionId);

        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow();

        Connection connection = connectionRepository
                .findById(connectionId)
                .orElseThrow();

        if (!connection.getReceiver().getId()
                .equals(receiver.getId())) {

            throw new RuntimeException(
                    "You cannot accept this request"
            );
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException(
                    "Connection request is not pending"
            );
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);

        connectionRepository.save(connection);

        log.info("Connection ID {} accepted successfully",
                connectionId);
    }

    @Transactional
    public void rejectRequest(
            String receiverEmail,
            Long connectionId) {

        log.info("User {} is rejecting connection ID {}",
                receiverEmail, connectionId);

        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow();

        Connection connection = connectionRepository
                .findById(connectionId)
                .orElseThrow();

        if (!connection.getReceiver().getId()
                .equals(receiver.getId())) {

            throw new RuntimeException(
                    "You cannot reject this request"
            );
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException(
                    "Connection request is not pending"
            );
        }

        connection.setStatus(ConnectionStatus.REJECTED);

        connectionRepository.save(connection);

        log.info("Connection ID {} rejected successfully",
                connectionId);
    }

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getPendingRequests(
            String email) {

        log.info("Fetching pending connection requests for {}",
                email);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return connectionRepository
                .findByReceiverAndStatus(
                        user,
                        ConnectionStatus.PENDING
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getSentRequests(
            String email) {

        log.info("Fetching sent connection requests for {}",
                email);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return connectionRepository
                .findBySenderAndStatus(
                        user,
                        ConnectionStatus.PENDING
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getConnections(
            String email) {

        log.info("Fetching accepted connections for {}",
                email);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return connectionRepository
                .findByStatusAndSenderOrStatusAndReceiver(
                        ConnectionStatus.ACCEPTED,
                        user,
                        ConnectionStatus.ACCEPTED,
                        user
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ConnectionResponse convertToResponse(
            Connection connection) {

        ConnectionResponse response =
                new ConnectionResponse();

        response.setId(connection.getId());

        response.setSenderId(
                connection.getSender().getId()
        );

        response.setSenderName(
                connection.getSender().getName()
        );

        response.setReceiverId(
                connection.getReceiver().getId()
        );

        response.setReceiverName(
                connection.getReceiver().getName()
        );

        response.setStatus(
                connection.getStatus()
        );

        response.setCreatedAt(
                connection.getCreatedAt()
        );

        response.setUpdatedAt(
                connection.getUpdatedAt()
        );

        return response;
    }
}