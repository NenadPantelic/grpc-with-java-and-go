package blog.server;

import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            BlogId blogCreationResponse = stub.createBlog(Blog.newBuilder()
                    .setAuthor("Nenad")
                    .setContent("Hello World")
                    .setTitle("New blog")
                    .build()
            );
            System.out.println("Blog created: " + blogCreationResponse.getId());
            return blogCreationResponse;
        } catch (Exception e) {
            System.out.println("Could not create a blog. Reason: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);

        BlogId blogId = createBlog(stub);
        if (blogId == null) {

        }

    }

    public static void main(String[] args) {
        // Channels will create the TCP connection between the client and the server
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 20002)
                .usePlaintext()
                .build();

        run(channel);
        System.out.println("Shutting down");
        channel.shutdown();
    }
}
