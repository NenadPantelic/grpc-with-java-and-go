package main

import (
	"context"
	"fmt"
	"log"

	"go.mongodb.org/mongo-driver/bson/primitive"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
)

func (*Server) CreateBlog(ctx context.Context, req *pb.CreateBlogRequest) (*pb.BlogId, error) {
	log.Printf("CreateBlog function was invoked with %v\n", req)
	blog := req.GetBlog()

	res := &pb.CreateBlogResponse{
		Blog: &pb.BlogItem{
			AuthorId: blog.GetAuthorId(),
			Title:    blog.GetTitle(),
			Content:  blog.GetContent(),
		},
	}

	res, err := collection.InsertOne(ctx, blog)
	if err != nil {
		return nil, status.Errorf(
			codes.Internal,
			fmt.Sprintf("Internal error: %v", err),
		)
	}

	oid, ok := res.InsertedID.(primitive.ObjectID)
	if !ok {
		return nil, status.Errorf(
			codes.Internal,
			fmt.Sprintf("Internal error: %v", err),
		)
	}

	return &pb.BlogId{
		Id: oid.Hex(),
	}, nil
}
