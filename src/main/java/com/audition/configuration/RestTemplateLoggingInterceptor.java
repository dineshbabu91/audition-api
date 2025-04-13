package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Autowired
    private AuditionLogger logger;

    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
        final String logInfoRequest = String.format("Request URL : %s with HTTP Method : %s", request.getURI(), request.getMethod());
        logger.info(LOG, logInfoRequest);
        final String logDebugRequest = String.format("Request body : %s", new String(body, StandardCharsets.UTF_8));
        logger.debug(LOG, logDebugRequest);

        final ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().isError()) {
            final String logErrorResponse = String.format("Error response from : %s with status : %s and Error response : %s",
                    request.getURI(), response.getStatusCode(), new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
            logger.error(LOG, logErrorResponse);
        } else {
            final String logInfoResponse = String.format("Success response from : %s with status %s",
                    request.getURI(),
                    response.getStatusCode());
            logger.info(LOG, logInfoResponse);
            final String logDebugResponse = String.format("Response body : %s", new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
            logger.debug(LOG, logDebugResponse);
        }

        return response;
    }
}
