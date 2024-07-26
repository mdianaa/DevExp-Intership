package com.org.devexp.linkvalidator.service.impl;

import com.org.devexp.linkvalidator.entity.Link;
import com.org.devexp.linkvalidator.repository.LinkRepository;
import com.org.devexp.linkvalidator.service.LinkValidationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class LinkValidationServiceImpl implements LinkValidationService {

    private final LinkRepository linkRepository;

    private final RestTemplate restTemplate;

    public LinkValidationServiceImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
        this.restTemplate = new RestTemplate();
    }

    public String validateLink(String url) {

        Link link = new Link();
        link.setUrl(url);

        try {
            // check whether the URL is properly formatted
            URI uri = new URI(url.trim().replaceAll("^\"|\"$", ""));

            // send HEAD request to check link status
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, null, Void.class);
            HttpStatus statusCode = (HttpStatus) response.getStatusCode();
            HttpStatus.Series series = statusCode.series();

            switch (series) {
                //2xx
                case SUCCESSFUL:
                    link.setValid(true);
                    link.setResponseMessage("Success with status code: " + statusCode);
                    break;
                //3xx
                case REDIRECTION:
                    link.setValid(true);
                    link.setResponseMessage("Redirection with status code: " + statusCode);
                    break;
                //4xx
                case CLIENT_ERROR:
                    link.setValid(false);
                    link.setResponseMessage("Client error with status code: " + statusCode);
                    break;
                //5xx
                case SERVER_ERROR:
                    link.setValid(false);
                    link.setResponseMessage("Server error with status code: " + statusCode);
                    break;
                default:
                    link.setValid(false);
                    link.setResponseMessage("Unexpected status code: " + statusCode);
                    break;
            }

        } catch (URISyntaxException e) {
            link.setValid(false);
            link.setResponseMessage("Invalid URL syntax: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            link.setValid(false);
            link.setResponseMessage("Client error: " + e.getStatusCode() + " - " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            link.setValid(false);
            link.setResponseMessage("Server error: " + e.getStatusCode() + " - " + e.getStatusText());
        } catch (RestClientException e) {
            link.setValid(false);
            link.setResponseMessage("Rest client error: " + e.getMessage());
        } catch (Exception e) {
            link.setValid(false);
            link.setResponseMessage("General error: " + e.getMessage());
        }

        linkRepository.save(link);

        return link.getResponseMessage();
    }
}
