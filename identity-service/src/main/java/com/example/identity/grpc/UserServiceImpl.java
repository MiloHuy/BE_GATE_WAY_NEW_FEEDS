package com.example.identity.grpc;

import com.example.proto.UserRequest;
import com.example.proto.UserResponse;
import com.example.proto.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        // Mock data
        UserResponse response = UserResponse.newBuilder()
                .setUserId(request.getUserId())
                .setUsername("JohnDoe")
                .setEmail("john.doe@example.com")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
