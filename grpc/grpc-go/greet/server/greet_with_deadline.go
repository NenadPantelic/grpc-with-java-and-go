package main

import (
	"context"
	"log"
	"time"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func (*Server) GreetWithDeadline(ctx context.Context, req *pb.GreetRequest) (*pb.GreetResponse, error) {
	log.Printf("GreetWithDeadline function was invoked with %v", req)

	for i := 0; i < 3; i++ {
		if ctx.Err() == context.Canceled {
			log.Println("The client canceled the request!")
			return nil, status.Error(codes.Canceled, "The client canceled the request")
		}
		time.Sleep(greetWithDeadlineTime)
	}

	return &pb.GreetResponse{
		Result: "Hello " + req.GetFirstName(),
	}, nil
}
