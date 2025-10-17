package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
)

func updateBlog(c pb.BlogServiceClient, id string) {
	log.Println("updateBlog was invoked")

	newBlog := &pb.Blog{
		Id:       id,
		AuthorId: "Updated Author",
		Title:    "My first blog",
		Body:     "Content of the first blog",
	}

	_, err := c.UpdateBlog(context.Background(), newBlog)
	if err != nil {
		log.Fatalf("Error while updating blog: %v", err)
	}

	log.Printf("Blog with ID: %s was updated\n", id)
}
