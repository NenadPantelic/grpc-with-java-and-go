package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func (s *Server) GreetWithError(ctx context.Context, req *pb.GreetRequest) (*pb.GreetResponse, error) {
	log.Printf("GreetWithError function was invoked with %v", req)

	if req.GetFirstName() == "" {
		return nil, status.Errorf(codes.InvalidArgument, "Invalid name")
	}

	return &pb.GreetResponse{
		Result: "Hello " + req.GetFirstName(),
	}, nil
}
