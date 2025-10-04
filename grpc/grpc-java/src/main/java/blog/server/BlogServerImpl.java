package blog.server;

import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;

public class BlogServerImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private final MongoCollection<Document> mongoCollection;

    BlogServerImpl(MongoClient mongoClient) {
        MongoDatabase db = mongoClient.getDatabase("blogdb");
        mongoCollection = db.getCollection("blogs");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        Document document = new Document("author", request.getAuthor())
                .append("title", request.getTitle())
                .append("content", request.getContent()
                );

        InsertOneResult result;
        try {
            result = mongoCollection.insertOne(document);
        } catch (MongoException e) {
            handleObserverError(responseObserver, Status.INTERNAL, e.getLocalizedMessage());
            return;
        }

        if (!result.wasAcknowledged() || result.getInsertedId() == null) {
            handleObserverError(responseObserver, Status.INTERNAL, "Blog couldn't be created");
            return;
        }

        String id = result.getInsertedId().asObjectId().getValue().toString();
        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        super.readBlog(request, responseObserver);
    }

    @Override
    public void updateBlog(Blog request, StreamObserver<Empty> responseObserver) {
        super.updateBlog(request, responseObserver);
    }

    @Override
    public void delete(BlogId request, StreamObserver<Empty> responseObserver) {
        super.delete(request, responseObserver);
    }

    @Override
    public void listBlogs(Empty request, StreamObserver<Blog> responseObserver) {
        super.listBlogs(request, responseObserver);
    }

    private void handleObserverError(StreamObserver<?> observer, Status status, String description) {
        observer.onError(status.withDescription(description).asRuntimeException());
    }
}
