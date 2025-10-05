package blog.server;

import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    static final String BLOG_COULDNT_BE_CREATED = "The blog could not be created";
    static final String BLOG_COULDNT_BE_DELETED = "The blog could not be deleted";
    static final String BLOG_NOT_FOUND = "The blog with the corresponding ID was not found";
    static final String ID_CANNOT_BE_EMPTY = "The blog ID cannot be empty";

    private final MongoCollection<Document> mongoCollection;

    BlogServiceImpl(MongoClient mongoClient) {
        MongoDatabase db = mongoClient.getDatabase("blogdb");
        mongoCollection = db.getCollection("blogs");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        System.out.println("Creating a blog...");
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
            responseObserver.onError(error(Status.INTERNAL, BLOG_COULDNT_BE_CREATED));
            return;
        }

        String id = result.getInsertedId().asObjectId().getValue().toString();
        System.out.printf("Blog with id %s is has been created%n", id);
        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        System.out.println("Received a request to read a blog");
        if (request.getId().isEmpty()) {
            responseObserver.onError(error(Status.NOT_FOUND, ID_CANNOT_BE_EMPTY));
            return;
        }

        String id = request.getId();
        System.out.printf("Reading a blog with id %s%n", id);

        Document document = mongoCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        if (document == null) {
            responseObserver.onError(error(Status.NOT_FOUND, BLOG_NOT_FOUND));

            handleObserverError(responseObserver, Status.NOT_FOUND, "Blog not found");
            return;
        }

        System.out.printf("Blog with id %s retrieved successfully%n", id);
        responseObserver.onNext(documentToBlog(document));
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlog(Blog request, StreamObserver<Empty> responseObserver) {
        System.out.println("Received a request to update a blog...");

        String id = request.getId();
        if (id.isEmpty()) {
            responseObserver.onError(error(Status.INVALID_ARGUMENT, ID_CANNOT_BE_EMPTY));
            return;
        }

        System.out.printf("Updating a blog with id %s%n", id);
        Document updatedDocument = mongoCollection.findOneAndUpdate(
                Filters.eq("_id", new ObjectId(id)),
                Updates.combine(
                        Updates.set("author", request.getAuthor()),
                        Updates.set("content", request.getContent()),
                        Updates.set("title", request.getTitle())
                )
        );
        if (updatedDocument == null) {
            responseObserver.onError(error(Status.NOT_FOUND, BLOG_NOT_FOUND));
            return;
        }

        System.out.printf("Updated a blog with id %s%n", id);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void delete(BlogId request, StreamObserver<Empty> responseObserver) {
        System.out.println("Received a request to delete a blog...");

        String id = request.getId();
        if (id.isEmpty()) {
            responseObserver.onError(error(Status.INVALID_ARGUMENT, ID_CANNOT_BE_EMPTY));
            return;
        }

        DeleteResult deleteResult;
        System.out.printf("Deleting a blog with id %s%n", id);
        try {
            deleteResult = mongoCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
        } catch (MongoException e) {
            responseObserver.onError(error(Status.INTERNAL, BLOG_COULDNT_BE_DELETED, e.getLocalizedMessage()));
            return;
        }

        if (!deleteResult.wasAcknowledged()) {
            responseObserver.onError(
                    error(Status.INTERNAL, BLOG_COULDNT_BE_DELETED, String.format("Blog[id = %s]", id))
            );
            return;
        }

        if (deleteResult.getDeletedCount() == 0) {
            responseObserver.onError(
                    error(Status.NOT_FOUND, BLOG_NOT_FOUND, String.format("Blog[id = %s]", id))
            );
            return;
        }

        System.out.printf("Deleted a blog with id %s%n", id);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void listBlogs(Empty request, StreamObserver<Blog> responseObserver) {
        System.out.println("Listing blogs...");
        for (Document document : mongoCollection.find()) {
            responseObserver.onNext(documentToBlog(document));
        }

        responseObserver.onCompleted();
    }

    private void handleObserverError(StreamObserver<?> observer, Status status, String description) {
        observer.onError(status.withDescription(description).asRuntimeException());
    }

    private io.grpc.StatusRuntimeException error(Status status, String message) {
        return status.withDescription(message).asRuntimeException();
    }

    @SuppressWarnings("SameParameterValue")
    private io.grpc.StatusRuntimeException error(Status status, String message, String augmentMessage) {
        return status.withDescription(message)
                .augmentDescription(augmentMessage)
                .asRuntimeException();
    }

    private Blog documentToBlog(Document document) {
        return Blog.newBuilder()
                .setAuthor(document.getString("author"))
                .setContent(document.getString("content"))
                .setId(document.get("_id").toString())
                .setTitle(document.getString("title"))
                .build();
    }
}
