package main

import (
	"context"
	"io"
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
	"google.golang.org/protobuf/types/known/emptypb"
)

func listBlog(c pb.BlogServiceClient) {
	log.Println("listBlog was invoked")

	stream, err := c.ListBlog(context.Background(), &emptypb.Empty{})
	if err != nil {
		log.Fatalf("Error while calling ListBlog RPC: %v", err)
	}

	for {
		blog, err := stream.Recv()
		if err == io.EOF {
			break
		}

		if err != nil {
			log.Fatalf("Something went wrong while reading the stream: %v", err)
		}

		log.Printf("Blog received: %v", blog)
	}
}
