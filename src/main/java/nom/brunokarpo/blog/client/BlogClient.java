package nom.brunokarpo.blog.client;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import nom.brunokarpo.grpc.blog.Blog;
import nom.brunokarpo.grpc.blog.BlogId;
import nom.brunokarpo.grpc.blog.BlogServiceGrpc;

public class BlogClient {

    public static void main(String[] args) {
        // Create managed channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50054)
                .usePlaintext()
                .build();

        run(channel);

        // Shutdown channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);

        BlogId blogId = createBlog(stub);

        if (blogId == null) {
            return;
        }

        readBlog(stub, blogId);
        updateBlog(stub, blogId);
        listBlogs(stub);
        deleteBlog(stub, blogId);
    }

    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            BlogId blogId = stub.createBlog(
                    Blog.newBuilder()
                            .setAuthor("Bruno")
                            .setTitle("New Blog")
                            .setContent("Hello, World! This is a new blog").build()
            );

            System.out.println("Blog created: " + blogId.getId());
            return blogId;
        } catch (StatusRuntimeException ex) {
            System.out.println("Could not create the blog");
            ex.printStackTrace();
            return null;
        }
    }

    private static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog blog = stub.readBlog(blogId);

            System.out.println("Blog read: " + blog);
        } catch (StatusRuntimeException ex) {
            System.out.println("Could not read the blog");
            ex.printStackTrace();
        }
    }

    private static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog blog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setAuthor("Bruno")
                    .setTitle("New blog changed")
                    .setContent("Hello, World! This is my first blog. I've added some content")
                    .build();

            stub.updateBlog(blog);
            System.out.println("Blog updated: " + blog);
        } catch (StatusRuntimeException ex) {
            System.out.println("Could not update the blog");
            ex.printStackTrace();
        }
    }

    private static void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        stub.listBlogs(Empty.getDefaultInstance()).forEachRemaining(System.out::println);
    }

    private static void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            stub.deleteBlog(blogId);
            System.out.println("Blog deleted: " + blogId);
        } catch (StatusRuntimeException ex) {
            System.out.println("Could not delete the blog");
            ex.printStackTrace();
        }
    }
}
