package blog.server;

import com.google.protobuf.Empty;
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

    static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog blog = stub.readBlog(blogId);
            System.out.println("Blog with id " + blogId.getId() + ": " + blog);
        } catch (Exception e) {
            System.out.println("Could not read a blog with id " + blogId.getId());
            e.printStackTrace();
        }
    }

    static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog newBlogData = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setAuthor("NP")
                    .setContent("Hey ho")
                    .setTitle("NenadP")
                    .build();
            stub.updateBlog(newBlogData);
            System.out.println("Blog with id " + blogId.getId() + " has been updated");
        } catch (Exception e) {
            System.out.println("Could not update a blog with id " + blogId.getId());
            e.printStackTrace();
        }
    }

    static void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        System.out.println("Listing all blogs");
        stub.listBlogs(Empty.getDefaultInstance()).forEachRemaining(blog -> System.out.println("Blog: " + blog));
    }

    static void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            stub.delete(blogId);
            System.out.println("Blog with id " + blogId.getId() + " has been deleted");
        } catch (Exception e) {
            System.out.println("Could not delete a blog with id " + blogId.getId());
            e.printStackTrace();
        }
    }

    public static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);

        BlogId blogId = createBlog(stub);
        if (blogId == null) {
            return;
        }

        readBlog(stub, blogId);
        updateBlog(stub, blogId);
        listBlogs(stub);
        deleteBlog(stub, blogId);
        readBlog(stub, blogId);
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
