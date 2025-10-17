package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
)

func deleteBlog(c pb.BlogServiceClient, id string) {
	log.Println("deleteBlog was invoked")

	_, err := c.DeleteBlog(context.Background(), &pb.DeleteBlogRequest{
		BlogId: id,
	})

	if err != nil {
		log.Fatalf("Error while deleting blog: %v", err)
	}

	log.Printf("Blog with ID: %s was deleted\n", id)
}
