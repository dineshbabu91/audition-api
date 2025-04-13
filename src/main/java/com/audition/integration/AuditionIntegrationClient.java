package com.audition.integration;

import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuditionIntegrationClient extends BaseClient {

    protected static final String GET_POSTS = "/posts";
    protected static final String GET_POSTS_BY_ID = "/posts/%s";
    protected static final String GET_COMMENTS_FOR_A_POST = "/comments";

    public List<AuditionPost> getPosts() {
        final AuditionPost[] response = perform(apiConfiguration.getAuditionApi(),
                HttpMethod.GET,
                GET_POSTS,
                null,
                AuditionPost[].class,
                null);
        return Arrays.stream(response).toList();
    }

    public AuditionPost getPostById(final String id) {
        return perform(apiConfiguration.getAuditionApi(),
                HttpMethod.GET,
                String.format(GET_POSTS_BY_ID, id),
                null,
                AuditionPost.class,
                null);
    }

    public AuditionPost getPostByIdWithComments(final String id) {
        final AuditionPost auditionPost = getPostById(id);
        auditionPost.setComments(getCommentsForPost(id));
        return auditionPost;
    }

    public List<AuditionPostComment> getCommentsForPost(final String id) {
        final AuditionPostComment[] response = perform(apiConfiguration.getAuditionApi(),
                HttpMethod.GET,
                GET_COMMENTS_FOR_A_POST,
                null,
                AuditionPostComment[].class,
                Map.of("postId", id));
        return Arrays.stream(response).toList();
    }
}
