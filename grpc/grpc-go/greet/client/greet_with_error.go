package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func doGreetWithError(c pb.GreetServiceClient, firstName string) {
	log.Println("Invoking doGreet...")

	req := &pb.GreetRequest{
		FirstName: firstName,
	}

	res, err := c.GreetWithError(context.Background(), req)
	if err != nil {
		e, ok := status.FromError(err)
		if ok {
			log.Printf("Error message from server: %v\n", e.Message())
			if e.Code() == codes.InvalidArgument {
				log.Printf("Invalid argument error: %v", e.Message())
				return
			}
		}

		log.Fatalf("Error while calling Greet: %v", err)
	}

	log.Printf("Response from Greet: %v", res)
}
