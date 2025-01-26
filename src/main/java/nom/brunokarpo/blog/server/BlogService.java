package nom.brunokarpo.blog.server;

import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.blog.Blog;
import nom.brunokarpo.grpc.blog.BlogId;
import nom.brunokarpo.grpc.blog.BlogServiceGrpc;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BlogService extends BlogServiceGrpc.BlogServiceImplBase {

    private final MongoCollection<Document> mongoCollection;

    BlogService(MongoClient client) {
        MongoDatabase db = client.getDatabase("blogdb");
        mongoCollection = db.getCollection("blog");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        Document doc = new Document("author", request.getAuthor())
                .append("title", request.getTitle())
                .append("content", request.getContent());

        InsertOneResult result;

        try {
            result = mongoCollection.insertOne(doc);
        } catch (MongoException ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(ex.getLocalizedMessage())
                    .asRuntimeException());
            return;
        }

        if (!result.wasAcknowledged() || result.getInsertedId() == null) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Blog couldn't be created")
                    .asRuntimeException());
            return;
        }

        String id = result.getInsertedId().asObjectId().getValue().toString();

        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        String id = request.getId();

        if (id.isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The blog id cannot be empty")
                            .asRuntimeException()
            );

            return;
        }

        Document doc = mongoCollection
                .find(eq("_id", new ObjectId(id)))
                .first();

        if (doc == null) {
            responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Blog was not found")
                            .augmentDescription("Blog id: " + id)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Blog.newBuilder()
                        .setAuthor(doc.getString("author"))
                        .setTitle(doc.getString("title"))
                        .setContent(doc.getString("content"))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlog(Blog request, StreamObserver<Empty> responseObserver) {
        String id = request.getId();

        if (id.isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The blog id cannot be empty")
                            .asRuntimeException()
            );

            return;
        }

        Document doc = mongoCollection.findOneAndUpdate(
                eq("_id", new ObjectId(id)),
                combine(
                        set("author", request.getAuthor()),
                        set("title", request.getTitle()),
                        set("content", request.getContent())
                )
        );

        if (doc == null) {
            responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Blog was not found")
                            .augmentDescription("BlogId: " + id)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void listBlogs(Empty request, StreamObserver<Blog> responseObserver) {
        for (Document doc : mongoCollection.find()) {
            responseObserver.onNext(
                    Blog.newBuilder()
                            .setId(doc.getObjectId("_id").toString())
                            .setAuthor(doc.getString("author"))
                            .setTitle(doc.getString("title"))
                            .setContent(doc.getString("content"))
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlog(BlogId request, StreamObserver<Empty> responseObserver) {
        String id = request.getId();

        if (id.isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The blog id cannot be empty")
                            .asRuntimeException()
            );

            return;
        }

        DeleteResult result;

        try {
            result = mongoCollection.deleteOne(
                    eq("_id", new ObjectId(id))
            );
        } catch (MongoException ex) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("The blog could not be deleted")
                    .asRuntimeException());
            return;
        }

        if (!result.wasAcknowledged()) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("The blog could not be deleted")
                    .asRuntimeException());
            return;
        }

        if (result.getDeletedCount() == 0) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog was not found")
                            .augmentDescription("BlogId: " + id)
                            .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
