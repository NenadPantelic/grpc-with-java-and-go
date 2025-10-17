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
	"google.golang.org/protobuf/types/known/emptypb"
)

func (*Server) UpdateBlog(ctx context.Context, blog *pb.Blog) (*emptypb.Empty, error) {
	log.Printf("UpdateBlog function was invoked with %v\n", blog)

	oid, err := primitive.ObjectIDFromHex(blog.GetId())
	if err != nil {
		return nil, status.Errorf(
			codes.InvalidArgument,
			fmt.Sprintf("Cannot parse ID: %v", err),
		)
	}

	update := BlogItem{
		Title:    blog.GetTitle(),
		AuthorID: blog.GetAuthor(),
		Content:  blog.GetContent(),
	}

	res, err := collection.UpdateOne(ctx, bson.M{"_id": oid}, bson.M{"$set": update})
	if err != nil {
		return nil, status.Errorf(
			codes.Internal,
			fmt.Sprintf("Error updating blog: %v", err),
		)
	}

	if res.MatchedCount == 0 {
		return nil, status.Errorf(
			codes.NotFound,
			fmt.Sprintf("Blog with ID %v not found", blog.GetId()),
		)
	}

	return blog, nil
}
