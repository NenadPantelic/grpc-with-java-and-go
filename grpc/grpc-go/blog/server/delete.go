package main

import (
	"context"
	"fmt"
	"log"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"
)

func (*Server) DeleteBlog(ctx context.Context, req *pb.BlogId) (*pb.Empty, error) {
	log.Printf("DeleteBlog function was invoked with %v\n", req)

	oid, err := primitive.ObjectIDFromHex(req.GetId())
	if err != nil {
		return nil, status.Errorf(
			codes.InvalidArgument,
			fmt.Sprintf("Cannot parse ID: %v", err),
		)
	}

	res, err := collection.DeleteOne(ctx, bson.M{"_id": oid})
	if err != nil {
		return nil, status.Errorf(
			codes.Internal,
			fmt.Sprintf("Error occurred while deleting blog: %v", err),
		)
	}

	if res.DeletedCount == 0 {
		return nil, status.Errorf(
			codes.NotFound,
			fmt.Sprintf("Blog with ID %v not found", req.GetId()),
		)
	}

	return &pb.Empty{}, nil
}
