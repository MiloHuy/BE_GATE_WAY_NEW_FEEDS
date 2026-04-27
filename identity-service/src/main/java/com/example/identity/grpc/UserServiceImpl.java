package com.example.identity.grpc;

import com.example.proto.UserRequest;
import com.example.proto.UserResponse;
import com.example.proto.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final com.example.identity.repository.UserRepository userRepository;

    public UserServiceImpl(com.example.identity.repository.UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        userRepository.findById(request.getUserId()).ifPresentOrElse(user -> {
            UserResponse response = UserResponse.newBuilder()
                    .setUserId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, () -> {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.NOT_FOUND.withDescription("User not found")));
        });
    }
}
