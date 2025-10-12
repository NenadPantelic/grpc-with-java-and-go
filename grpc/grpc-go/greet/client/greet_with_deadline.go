package main

import (
	"context"
	"log"
	"time"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func doGreetWithDeadline(c pb.GreetServiceClient, timeout time.Duration) {
	log.Println("doGreetWithDeadline was invoked...")

	req := &pb.GreetRequest{
		FirstName: "Nenad",
	}

	ctx, cancel := context.WithTimeout(context.Background(), timeout)
	defer cancel()

	res, err := c.GreetWithDeadline(ctx, req)
	if err != nil {
		statusErr, ok := status.FromError(err)
		if ok {
			if statusErr.Code() == codes.DeadlineExceeded {
				log.Println("Deadline exceeded!")
				return
			}

			log.Fatalf("Unexpected error: %v", statusErr)
		} else {
			log.Fatalf("Error while calling GreetWithDeadline: %v", err)
		}

	}

	log.Printf("GreetWithDeadline response: %v", res.Result)
}
