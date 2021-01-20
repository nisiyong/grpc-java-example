package io.github.nisiyong.example;


import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

/**
 * @author nisiyong
 */
public class HelloWorldClient {

    private GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWorldClient(Channel channel) {
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void greet(String name) {
        System.out.println("Will try to greet " + name + " ...");
        HelloWorld.HelloRequest helloRequest = HelloWorld.HelloRequest.newBuilder()
                .setName(name)
                .build();

        HelloWorld.HelloReply response;
        try {
            response = blockingStub.sayHello(helloRequest);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return;
        }

        System.out.println("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        String target = System.getProperty("server.address", "localhost:8081");
        String name = "world";
        if (args.length > 0) {
            name = args[0];
        }

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        try {
            HelloWorldClient helloWorldClient = new HelloWorldClient(channel);
            helloWorldClient.greet(name);
        } catch (Exception e) {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
