package main

import (
	"log"
	"net"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc"
)

var addr string = "0.0.0.0:50051"

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

	// Create a gRPC server
	server := grpc.NewServer()
	pb.RegisterGreetServiceServer(server, &Server{})

	if err := server.Serve(listener); err != nil {
		log.Fatalf("Failed to serve gRPC server over port %v: %v", addr, err)
	}

}
