package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
)

func readBlog(c pb.BlogServiceClient, id string) *pb.Blog {
	log.Println("readBlog was invoked")

	res, err := c.ReadBlog(context.Background(), &pb.ReadBlogRequest{
		BlogId: id,
	})
	if err != nil {
		log.Fatalf("Error while reading blog: %v", err)
	}

	return res.GetBlog()
}
