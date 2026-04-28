package com.example.post.client;

import com.example.proto.UserRequest;
import com.example.proto.UserResponse;
import com.example.proto.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserClient {

    @GrpcClient("identity-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserResponse getUser(String userId) {
        UserRequest request = UserRequest.newBuilder()
                .setUserId(userId)
                .build();
        try {
            // Set timeout 2 seconds
            return userServiceStub.withDeadlineAfter(2, java.util.concurrent.TimeUnit.SECONDS)
                    .getUser(request);
        } catch (Exception e) {
            return UserResponse.newBuilder()
                    .setUserId(userId)
                    .setUsername("Unknown Client")
                    .build();
        }
    }
}
