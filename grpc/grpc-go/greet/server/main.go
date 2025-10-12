package main

import (
	"log"
	"net"
	"time"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
	"google.golang.org/grpc/reflection"
)

var addr string = "0.0.0.0:50051"
var greetWithDeadlineTime time.Duration = 1 * time.Second

type Server struct {
	pb.GreetServiceServer
}

func main() {
	listener, err := net.Listen("tcp", addr)
	if err != nil {
		log.Fatalf("Failed to listen on port %v: %v", addr, err)
	}
	defer listener.Close()

	log.Printf("Server is listening on port %v...", addr)
	opts := []grpc.ServerOption{}

	tls := true
	if tls {
		log.Println("TLS enabled")
		certFile := "ssl/server.crt"
		keyFile := "ssl/server.pem"

		creds, err := credentials.NewServerTLSFromFile(certFile, keyFile)
		if err != nil {
			log.Fatalf("Failed loading certificates: %v", err)
		}

		opts = append(opts, grpc.Creds(creds))
	}

	// Create a gRPC server
	server := grpc.NewServer(opts...)
	pb.RegisterGreetServiceServer(server, &Server{})
	reflection.Register(server) // register reflection service on gRPC server

	if err := server.Serve(listener); err != nil {
		log.Fatalf("Failed to serve gRPC server over port %v: %v", addr, err)
	}

}
