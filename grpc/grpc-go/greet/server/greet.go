package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func (s *Server) Greet(ctx context.Context, req *pb.GreetRequest) (*pb.GreetResponse, error) {
	log.Printf("Greet function was invoked with %v", req)

	return &pb.GreetResponse{
		Result: "Hello " + req.GetFirstName(),
	}, nil
}
