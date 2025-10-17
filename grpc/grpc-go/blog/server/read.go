package main

import (
	"context"
	"fmt"
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func (*Server) ReadBlog(ctx context.Context, req *pb.ReadBlogRequest) (*pb.Blog, error) {
	log.Printf("ReadBlog function was invoked with %v\n", req)

	oid, err := primitive.ObjectIDFromHex(req.GetId())
	if err != nil {
		return nil, status.Errorf(
			codes.InvalidArgument,
			fmt.Sprintf("Cannot parse ID: %v", err),
		)
	}

	data := &BlogItem{}
	res := collection.FindOne(ctx, bson.M{"_id": oid})
	if err := res.Decode(data); err != nil {
		return nil, status.Errorf(
			codes.NotFound,
			fmt.Sprintf("Blog with ID %v not found", req.GetId()),
		)
	}

	return documentToBlog(data), nil
}
