package com.careerconnect.service;

import com.careerconnect.entity.Connection;
import com.careerconnect.entity.ConnectionStatus;
import com.careerconnect.entity.User;
import com.careerconnect.repository.ConnectionRepository;
import com.careerconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConnectionService connectionService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setName("User One");
        user1.setEmail("user1@gmail.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setEmail("user2@gmail.com");
    }

    @Test
    void testSendRequestSuccessfully() {

        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(user1));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        when(connectionRepository
                .findBySenderAndReceiver(user1, user2))
                .thenReturn(Optional.empty());

        when(connectionRepository
                .findBySenderAndReceiver(user2, user1))
                .thenReturn(Optional.empty());

        Connection savedConnection = new Connection();
        savedConnection.setSender(user1);
        savedConnection.setReceiver(user2);
        savedConnection.setStatus(ConnectionStatus.PENDING);

        when(connectionRepository.save(any(Connection.class)))
                .thenReturn(savedConnection);

        var response = connectionService.sendRequest(
                "user1@gmail.com",
                2L
        );

        assertNotNull(response);

        assertEquals(
                ConnectionStatus.PENDING,
                response.getStatus()
        );

        verify(connectionRepository).save(any(Connection.class));
    }

    @Test
    void testSendRequestToSelf() {

        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(user1));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        assertThrows(
                RuntimeException.class,
                () -> connectionService.sendRequest(
                        "user1@gmail.com",
                        1L
                )
        );

        verify(connectionRepository, never())
                .save(any(Connection.class));
    }

    @Test
    void testSendRequestUserNotFound() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> connectionService.sendRequest(
                        "unknown@gmail.com",
                        2L
                )
        );

        verify(connectionRepository, never())
                .save(any(Connection.class));
    }

    @Test
    void testSendRequestReceiverNotFound() {

        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(user1));

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> connectionService.sendRequest(
                        "user1@gmail.com",
                        99L
                )
        );

        verify(connectionRepository, never())
                .save(any(Connection.class));
    }

    @Test
    void testSendRequestDuplicate() {

        Connection existingConnection = new Connection();
        existingConnection.setSender(user1);
        existingConnection.setReceiver(user2);
        existingConnection.setStatus(ConnectionStatus.PENDING);

        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(user1));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        when(connectionRepository
                .findBySenderAndReceiver(user1, user2))
                .thenReturn(Optional.of(existingConnection));

        assertThrows(
                RuntimeException.class,
                () -> connectionService.sendRequest(
                        "user1@gmail.com",
                        2L
                )
        );

        verify(connectionRepository, never())
                .save(any(Connection.class));
    }

    @Test
    void testAcceptRequestSuccessfully() {

        Connection connection = new Connection();

        connection.setSender(user1);
        connection.setReceiver(user2);
        connection.setStatus(ConnectionStatus.PENDING);

        when(userRepository.findByEmail("user2@gmail.com"))
                .thenReturn(Optional.of(user2));

        when(connectionRepository.findById(1L))
                .thenReturn(Optional.of(connection));

        connectionService.acceptRequest(
                "user2@gmail.com",
                1L
        );

        assertEquals(
                ConnectionStatus.ACCEPTED,
                connection.getStatus()
        );

        verify(connectionRepository).save(connection);
    }

    @Test
    void testAcceptRequestForbiddenForSender() {

        Connection connection = new Connection();

        connection.setSender(user1);
        connection.setReceiver(user2);
        connection.setStatus(ConnectionStatus.PENDING);

        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(user1));

        when(connectionRepository.findById(1L))
                .thenReturn(Optional.of(connection));

        assertThrows(
                RuntimeException.class,
                () -> connectionService.acceptRequest(
                        "user1@gmail.com",
                        1L
                )
        );

        verify(connectionRepository, never())
                .save(any(Connection.class));
    }

    @Test
    void testRejectRequestSuccessfully() {

        Connection connection = new Connection();

        connection.setSender(user1);
        connection.setReceiver(user2);
        connection.setStatus(ConnectionStatus.PENDING);

        when(userRepository.findByEmail("user2@gmail.com"))
                .thenReturn(Optional.of(user2));

        when(connectionRepository.findById(1L))
                .thenReturn(Optional.of(connection));

        connectionService.rejectRequest(
                "user2@gmail.com",
                1L
        );

        assertEquals(
                ConnectionStatus.REJECTED,
                connection.getStatus()
        );

        verify(connectionRepository).save(connection);
    }

    @Test
    void testAcceptRequestNotFound() {

        when(userRepository.findByEmail("user2@gmail.com"))
                .thenReturn(Optional.of(user2));

        when(connectionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> connectionService.acceptRequest(
                        "user2@gmail.com",
                        99L
                )
        );

        verify(connectionRepository, never())
                .save(any(Connection.class));
    }

    @Test
    void testGetConnections() {

        Connection connection = new Connection();

        connection.setSender(user1);
        connection.setReceiver(user2);
        connection.setStatus(ConnectionStatus.ACCEPTED);

        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(user1));

        when(connectionRepository
                .findByStatusAndSenderOrStatusAndReceiver(
                        ConnectionStatus.ACCEPTED,
                        user1,
                        ConnectionStatus.ACCEPTED,
                        user1
                ))
                .thenReturn(List.of(connection));

        var result = connectionService
                .getConnections("user1@gmail.com");

        assertEquals(1, result.size());

        assertEquals(
                ConnectionStatus.ACCEPTED,
                result.get(0).getStatus()
        );
    }
}