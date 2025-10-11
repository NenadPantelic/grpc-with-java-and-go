package main

import (
	"context"
	"io"
	"log"
	"time"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func doGreetEveryone(c pb.GreetServiceClient) {
	log.Println("doGreetEveryone was invoked")

	stream, err := c.GreetEveryone(context.Background())
	if err != nil {
		log.Fatalf("Error while calling GreetEveryone: %v\n", err)
	}

	requests := []*pb.GreetRequest{
		{FirstName: "Nenad"},
		{FirstName: "John"},
		{FirstName: "Doe"},
		{FirstName: "Jane"},
		{FirstName: "Doe"},
	}

	waitc := make(chan struct{})

	// to sends requests
	go func() {
		for _, req := range requests {
			log.Printf("Sending message: %v\n", req)
			if err := stream.Send(req); err != nil {
				log.Fatalf("Error while sending message to GreetEveryone: %v\n", err)
			}

			time.Sleep(1 * time.Second)
		}
		stream.CloseSend()
	}()

	// to receive responses
	go func() {
		for {
			res, err := stream.Recv()

			if err == io.EOF {
				break
			}

			if err != nil {
				log.Printf("Error while receiving message from GreetEveryone: %v\n", err)
				break
			}

			log.Printf("Received message: %v\n", res)
		}
		// this cannot be done until all messages are sent and read (read stream
		// won't get EOF until all of them are sent)
		close(waitc)
	}()

	// this channel just waits for all messages to sent and read
	<-waitc
}
