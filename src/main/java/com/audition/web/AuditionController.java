package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam(value = "userId", required = false) final String userId) {

        final List<AuditionPost> auditionPosts = auditionService.getPosts();

        if (StringUtils.isBlank(userId)) {
            return auditionPosts;
        }
        return auditionPosts.stream().filter(auditionPost -> userId.equals(String.valueOf(auditionPost.getUserId()))).toList();
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostById(@PathVariable("id") final String postId) {
        validateInput(postId);
        return auditionService.getPostById(postId);
    }

    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostByIdWithComments(@PathVariable("id") final String postId) {
        validateInput(postId);
        return auditionService.getPostByIdWithComments(postId);
    }

    private void validateInput(final String postId) {
        if (StringUtils.isBlank(postId) || !postId.matches("^[0-9]+$")) {
            throw new SystemException(String.format("Given postId [%s] is invalid. It must be a positive number.", postId), "Invalid Request",
                    400);
        }
    }

}
