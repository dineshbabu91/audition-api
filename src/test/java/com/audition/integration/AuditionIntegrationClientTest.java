package com.audition.integration;

import com.audition.TestConstants;
import com.audition.TestUtil;
import com.audition.common.exception.SystemException;
import com.audition.configuration.ApiClientConfiguration;
import com.audition.configuration.ApiConfiguration;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuditionIntegrationClientTest {
    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @Mock
    private ApiConfiguration apiConfiguration;

    @Mock
    private RestTemplate restTemplate;

    static final String POST_ID = "abc";

    @BeforeEach
    void init() {
        final ApiClientConfiguration apiClientConfiguration = new ApiClientConfiguration();
        apiClientConfiguration.setBaseUrl("/sample-host");
        Mockito.when(apiConfiguration.getAuditionApi()).thenReturn(apiClientConfiguration);
    }

    @Test
    void testGetPostsSuccess() throws IOException {
        final AuditionPost[] auditionPosts = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS, AuditionPost[].class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost[].class)))
                .thenReturn(new ResponseEntity<>(auditionPosts, HttpStatusCode.valueOf(200)));

        Assertions.assertNotNull(auditionIntegrationClient.getPosts());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost[].class));
    }

    @Test
    void testGetPostByIdSuccess() throws IOException {
        final AuditionPost auditionPost = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_BY_ID, AuditionPost.class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenReturn(new ResponseEntity<>(auditionPost, HttpStatusCode.valueOf(200)));

        final String postId = "2";
        final AuditionPost response = auditionIntegrationClient.getPostById(postId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(postId, String.valueOf(response.getId()));
        Assertions.assertNull(response.getComments());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
    }

    @Test
    void testGetPostByIdFailureWith404() {
        final int errorCode = 404;
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenThrow(new HttpClientErrorException(HttpStatusCode.valueOf(errorCode), "Resource not found"));

        final String postId = "105";
        final SystemException exception = Assertions.assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(postId));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Cannot find a resource for " + String.format(AuditionIntegrationClient.GET_POSTS_BY_ID, postId), exception.getMessage());
        Assertions.assertEquals(errorCode, exception.getStatusCode());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
    }

    @Test
    void testGetPostByIdFailureWith400() {
        final int errorCode = 400;
        final String errorMessage = "Invalid Request";
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenThrow(new HttpClientErrorException(HttpStatusCode.valueOf(errorCode), errorMessage));

        final SystemException exception = Assertions.assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(POST_ID));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(errorCode + " " + errorMessage, exception.getMessage());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
    }

    @Test
    void testGetPostByIdFailureWith500() {
        final int errorCode = 500;
        final String errorMessage = "Service Unavailable";
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenThrow(new HttpServerErrorException(HttpStatusCode.valueOf(errorCode), errorMessage));

        final SystemException exception = Assertions.assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(POST_ID));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(errorCode + " " + errorMessage, exception.getMessage());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
    }

    @Test
    void testGetPostByIdFailureWithUnknownHttpStatusCodeException() {
        final int errorCode = 900;
        final String errorMessage = "Unknown exception";
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenThrow(new UnknownHttpStatusCodeException(errorCode, errorMessage, null, null, null));

        final SystemException exception = Assertions.assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(POST_ID));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(String.format("Unknown status code [%s] ", errorCode) + errorMessage, exception.getMessage());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
    }

    @Test
    void testGetPostByIdFailureWithRuntimeException() {
        final String errorMessage = "Unknown Error message";
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenThrow(new RestClientResponseException(errorMessage, HttpStatusCode.valueOf(100), "unhandled error", null, null, null));

        final SystemException exception = Assertions.assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(POST_ID));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(errorMessage, exception.getMessage());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
    }

    @Test
    void testGetPostByIdWithCommentsSuccess() throws IOException {
        final AuditionPost auditionPost = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_BY_ID, AuditionPost.class);
        final AuditionPostComment[] auditionPostComments = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_COMMENTS, AuditionPostComment[].class);
        auditionPost.setComments(Arrays.stream(auditionPostComments).toList());
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPost.class)))
                .thenReturn(new ResponseEntity<>(auditionPost, HttpStatusCode.valueOf(200)));
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPostComment[].class)))
                .thenReturn(new ResponseEntity<>(auditionPostComments, HttpStatusCode.valueOf(200)));

        final String postId = "2";
        final AuditionPost response = auditionIntegrationClient.getPostByIdWithComments(postId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(postId, String.valueOf(response.getId()));
        Assertions.assertTrue(response.getComments().stream().allMatch(res -> postId.equals(String.valueOf(res.getPostId()))));

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.isNull(), ArgumentMatchers.eq(AuditionPost.class));
        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPostComment[].class));
    }

    @Test
    void testGetCommentsForPostSuccess() throws IOException {
        final AuditionPostComment[] auditionPostComments = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_COMMENTS, AuditionPostComment[].class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPostComment[].class)))
                .thenReturn(new ResponseEntity<>(auditionPostComments, HttpStatusCode.valueOf(200)));

        final String postId = "2";
        final List<AuditionPostComment> response = auditionIntegrationClient.getCommentsForPost(postId);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.stream().allMatch(res -> postId.equals(String.valueOf(res.getPostId()))));

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(HttpMethod.GET),
                        ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(AuditionPostComment[].class));
    }
}
