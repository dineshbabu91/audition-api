package com.audition.web;

import com.audition.TestConstants;
import com.audition.TestUtil;
import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import com.audition.service.AuditionService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class AuditionControllerTest {
    @InjectMocks
    private AuditionController auditionController;

    @Mock
    private AuditionService auditionService;

    private MockMvc mockMvc;

    private static final String GET_POSTS = "/posts";
    private static final String GET_POSTS_BY_ID = "/posts/";
    private static final String VALIDATION_ERROR_MESSAGE = "Given postId [%s] is invalid. It must be a positive number.";

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditionController).build();
    }

    @Test
    void testGetPostsSuccess() throws Exception {
        final AuditionPost[] auditionPosts = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS, AuditionPost[].class);
        when(auditionService.getPosts()).thenReturn(Arrays.stream(auditionPosts).toList());

        final MockHttpServletResponse response = mockMvc.perform(
                get(GET_POSTS).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

        verify(auditionService, times(1)).getPosts();
    }

    @Test
    void testGetPostsSuccessWithUserIdFilter() throws Exception {
        final AuditionPost[] auditionPosts = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS, AuditionPost[].class);
        when(auditionService.getPosts()).thenReturn(Arrays.stream(auditionPosts).toList());

        final MockHttpServletResponse response = mockMvc.perform(
                get(GET_POSTS + "?userId=1").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

        verify(auditionService, times(1)).getPosts();
    }

    @Test
    void testGetPostByIdSuccess() throws Exception {
        final AuditionPost auditionPost = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_BY_ID, AuditionPost.class);
        when(auditionService.getPostById(anyString())).thenReturn(auditionPost);

        final MockHttpServletResponse response = mockMvc.perform(
                get(GET_POSTS_BY_ID + "2").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

        verify(auditionService, times(1)).getPostById(anyString());
    }

    @Test
    void testGetPostByIdFailureInvalidPostIdNonDigit() throws Exception {
        final String invalidPostId = "abc";
        try {
            mockMvc.perform(
                    get(GET_POSTS_BY_ID + invalidPostId).accept(MediaType.APPLICATION_JSON)
            ).andReturn().getResponse();
        } catch (ServletException e) {
            final SystemException exception = (SystemException) e.getCause();
            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
            assertEquals(String.format(VALIDATION_ERROR_MESSAGE, invalidPostId), exception.getMessage());
        }
        verify(auditionService, times(0)).getPostById(anyString());
    }

    @Test
    void testGetPostByIdFailureInvalidPostIdBlank() throws Exception {
        final String invalidPostId = " ";
        try {
            mockMvc.perform(
                    get(GET_POSTS_BY_ID + invalidPostId).accept(MediaType.APPLICATION_JSON)
            ).andReturn().getResponse();
        } catch (ServletException e) {
            final SystemException exception = (SystemException) e.getCause();
            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
            assertEquals(String.format(VALIDATION_ERROR_MESSAGE, invalidPostId), exception.getMessage());
        }
        verify(auditionService, times(0)).getPostById(anyString());
    }

    @Test
    void testGetPostByIdWithCommentsSuccess() throws Exception {
        final AuditionPost auditionPost = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_BY_ID, AuditionPost.class);
        final AuditionPostComment[] auditionPostComments = TestUtil.getFile(TestConstants.RESPONSE_FOR_GET_POSTS_COMMENTS, AuditionPostComment[].class);
        auditionPost.setComments(Arrays.stream(auditionPostComments).toList());
        when(auditionService.getPostByIdWithComments(anyString())).thenReturn(auditionPost);
        final MockHttpServletResponse response = mockMvc.perform(
                get(GET_POSTS + "/2/comments").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

        verify(auditionService, times(1)).getPostByIdWithComments(anyString());
    }

    @Test
    void testGetPostByIdWithCommentsFailureInvalidPostIdNonDigit() throws Exception {
        final String invalidPostId = "abc";
        try {
            mockMvc.perform(
                    get(GET_POSTS_BY_ID + invalidPostId + "/comments").accept(MediaType.APPLICATION_JSON)
            ).andReturn().getResponse();
        } catch (ServletException e) {
            final SystemException exception = (SystemException) e.getCause();
            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
            assertEquals(String.format(VALIDATION_ERROR_MESSAGE, invalidPostId), exception.getMessage());
        }
        verify(auditionService, times(0)).getPostByIdWithComments(anyString());
    }

    @Test
    void testGetPostByIdWithCommentsFailureInvalidPostIdBlank() throws Exception {
        final String invalidPostId = " ";
        try {
            mockMvc.perform(
                    get(GET_POSTS_BY_ID + invalidPostId + "/comments").accept(MediaType.APPLICATION_JSON)
            ).andReturn().getResponse();
        } catch (ServletException e) {
            final SystemException exception = (SystemException) e.getCause();
            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
            assertEquals(String.format(VALIDATION_ERROR_MESSAGE, invalidPostId), exception.getMessage());
        }
        verify(auditionService, times(0)).getPostByIdWithComments(anyString());
    }
}
