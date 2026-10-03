package com.careerconnect.repository;

import com.careerconnect.entity.Connection;
import com.careerconnect.entity.ConnectionStatus;
import com.careerconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository
        extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySenderAndReceiver(
            User sender,
            User receiver
    );

    List<Connection> findByReceiverAndStatus(
            User receiver,
            ConnectionStatus status
    );

    List<Connection> findBySenderAndStatus(
            User sender,
            ConnectionStatus status
    );

    List<Connection> findByStatusAndSenderOrStatusAndReceiver(
            ConnectionStatus senderStatus,
            User sender,
            ConnectionStatus receiverStatus,
            User receiver
    );
}