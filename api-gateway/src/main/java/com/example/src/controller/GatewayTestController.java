package com.example.src.controller;

import com.example.proto.UserRequest;
import com.example.proto.UserResponse;
import com.example.proto.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
public class GatewayTestController {

    @GrpcClient("identity-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @GetMapping("/user/{id}")
    public Map<String, String> getUser(@PathVariable String id) {
        UserRequest request = UserRequest.newBuilder()
                .setUserId(id)
                .build();

        UserResponse response = userServiceStub.getUser(request);

        return Map.of(
                "userId", response.getUserId(),
                "username", response.getUsername(),
                "email", response.getEmail(),
                "source", "gRPC from Identity Service"
        );
    }
}
