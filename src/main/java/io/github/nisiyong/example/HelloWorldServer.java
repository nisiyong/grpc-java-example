package io.github.nisiyong.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author nisiyong
 */
public class HelloWorldServer {

    private Server server;

private void start(int port) throws IOException {
    server = ServerBuilder.forPort(port)
            .addService(new GreeterImpl())
            .build()
            .start();
    System.out.println("Server started, listening on " + port);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            HelloWorldServer.this.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("*** server shut down");
    }));
}

    private void blockUntilShutdown() throws InterruptedException {
        server.awaitTermination();
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(System.getProperty("server.port", "8081"));

        HelloWorldServer helloWorldServer = new HelloWorldServer();
        helloWorldServer.start(port);
        helloWorldServer.blockUntilShutdown();
    }

    static
class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloWorld.HelloRequest request, StreamObserver<HelloWorld.HelloReply> responseObserver) {
        String name = request.getName();
        System.out.println(new Date().toString() + "Receive: " + name);
        HelloWorld.HelloReply helloReply = HelloWorld.HelloReply.newBuilder()
                .setMessage("Hello, " + name)
                .build();

        responseObserver.onNext(helloReply);
        responseObserver.onCompleted();
    }
}
}
