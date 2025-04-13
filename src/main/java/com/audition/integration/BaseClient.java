package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.configuration.ApiClientConfiguration;
import com.audition.configuration.ApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.Map;

public class BaseClient {
    @Autowired
    protected ApiConfiguration apiConfiguration;
    @Autowired
    protected RestTemplate restTemplate;

    protected <T> T perform(final ApiClientConfiguration apiClientConfiguration,
                            final HttpMethod httpMethod,
                            final String relativePath,
                            final Object request,
                            final Class<T> responseType,
                            final Map<String, String> queryParams) {
        final String url = constructUrl(apiClientConfiguration, relativePath, queryParams);
        try {
            return restTemplate.exchange(url, httpMethod, request == null ? null : new HttpEntity<>(request), responseType).getBody();
        } catch (final RestClientResponseException e) {
            throw throwException(e, relativePath);
        }
    }

    private String constructUrl(final ApiClientConfiguration apiClientConfiguration, final String relativePath, final Map<String, String> queryParams) {
        return String.format("%s/%s%s", apiClientConfiguration.getBaseUrl(), relativePath, buildQueryParams(queryParams));
    }

    private Object buildQueryParams(final Map<String, String> queryParams) {
        return null == queryParams ? "" :
                queryParams.entrySet().stream().map(p -> p.getKey() + "=" + p.getValue())
                        .reduce((p1, p2) -> p1 + "&" + p2)
                        .map(s -> "?" + s)
                        .orElse("");
    }

    private SystemException throwException(final Exception e, final String relativePath) {
        if (e instanceof HttpClientErrorException clientError) {
            if (clientError.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new SystemException("Cannot find a resource for " + relativePath, "Resource Not Found",
                        404, clientError);
            }
            return new SystemException(clientError.getMessage(), clientError.getStatusCode().value(), clientError);
        } else if (e instanceof HttpServerErrorException serverError) {
            return new SystemException(serverError.getMessage(), serverError.getStatusCode().value(), serverError);
        } else if (e instanceof UnknownHttpStatusCodeException unknown9Error) {
            return new SystemException(e.getMessage(), unknown9Error);
        }
        return new SystemException("Unknown Error message", e);
    }
}
