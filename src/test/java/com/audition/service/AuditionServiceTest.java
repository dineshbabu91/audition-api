package com.audition.service;

import com.audition.TestConstants;
import com.audition.TestUtil;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditionServiceTest {
    @InjectMocks
    private AuditionService auditionService;

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @Test
    void testGetPostsSuccess() throws Exception {
        final AuditionPost[] auditionPosts = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS, AuditionPost[].class);
        when(auditionIntegrationClient.getPosts()).thenReturn(Arrays.stream(auditionPosts).toList());

        assertNotNull(auditionService.getPosts());

        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    void testGetPostByIdSuccess() throws Exception {
        final AuditionPost auditionPost = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_BY_ID, AuditionPost.class);
        when(auditionIntegrationClient.getPostById(anyString())).thenReturn(auditionPost);

        final String postId = "2";
        final AuditionPost response = auditionService.getPostById(postId);

        assertNotNull(response);
        assertEquals(postId, String.valueOf(response.getId()));
        assertNull(response.getComments());

        verify(auditionIntegrationClient, times(1)).getPostById(anyString());
    }

    @Test
    void testGetPostByIdWithCommentsSuccess() throws Exception {
        final AuditionPost auditionPost = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_BY_ID, AuditionPost.class);
        final AuditionPostComment[] auditionPostComments = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_COMMENTS, AuditionPostComment[].class);
        auditionPost.setComments(Arrays.stream(auditionPostComments).toList());
        when(auditionIntegrationClient.getPostByIdWithComments(anyString())).thenReturn(auditionPost);

        final String postId = "2";
        final AuditionPost response = auditionService.getPostByIdWithComments(postId);

        assertNotNull(response);
        assertEquals(postId, String.valueOf(response.getId()));
        assertTrue(response.getComments().stream().allMatch(res -> postId.equals(String.valueOf(res.getPostId()))));

        verify(auditionIntegrationClient, times(1)).getPostByIdWithComments(anyString());
    }
}
