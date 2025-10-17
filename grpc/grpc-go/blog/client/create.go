package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
)

func createBlog(c pb.BlogServiceClient) string {
	log.Println("createBlog was invoked")

	blog := &pb.Blog{
		AuthorId: "1",
		Title:    "New Blog",
		Content:  "This is the content of the new blog",
	}

	res, err := c.CreateBlog(context.Background(), blog)
	if err != nil {
		log.Fatalf("Error while creating blog: %v", err)
	}

	log.Printf("Blog created with ID: %s\n", res.GetBlog().GetId())
	return res.GetBlog().GetId()
}
