package controller;

import Utility.HtmlMakerUtility;
import httpMock.CustomHttpFactory;
import httpMock.CustomHttpRequest;
import httpMock.CustomHttpResponse;
import httpMock.constants.ContentType;
import httpMock.constants.StatusCode;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.SessionService;
import service.StaticFileService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static service.StaticFileService.getFileTypeFromUrl;

public class StaticFileController implements RequestController {
    private static Logger logger = LoggerFactory.getLogger(StaticFileController.class);

    private static final StaticFileController fileController = new StaticFileController();
    public static final String[] supportedFileTypes = {
            "html", "ico", "css", "js", "ttf", "woff", "svg", "eot", "woff2", "png", "jpeg", "gif"
    };

    public static StaticFileController get() {
        return fileController;
    }

    @Override
    public CustomHttpResponse handleRequest(CustomHttpRequest req) {
        return getFile(req);
    }

    public CustomHttpResponse getFile(CustomHttpRequest req) {
        if(StaticFileService.isTemplateFile(req.getUrl()))
            return handleTemplateFile(req);
        return handleStaticFile(req);
    }

    public CustomHttpResponse handleTemplateFile(CustomHttpRequest req){
        File file = StaticFileService.getFile(req.getUrl());
        User user = SessionService.getUserBySessionId(req.getSSID()).orElse(User.GUEST);
        try {
            Map<String, String> matchings = HtmlMakerUtility.getLoginLogoutTemplate(user != User.GUEST);
            matchings.put("name", user.getName());

            String lines = StaticFileService.renderFile(file, matchings);
            return CustomHttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML, new HashMap<>(), lines.getBytes());
        } catch (IOException e) {
            return CustomHttpFactory.NOT_FOUND();
        }
    }

    public CustomHttpResponse handleStaticFile(CustomHttpRequest req){
        File file = StaticFileService.getFile(req.getUrl());
        String fileType = StaticFileService.getFileTypeFromUrl(req.getUrl());
        ContentType contentType = ContentType.getContentTypeByFileType(fileType);
        try {
            return CustomHttpResponse.of(StatusCode.OK, contentType, new HashMap<>(), Files.readAllBytes(Path.of(file.getPath())));
        }catch (IOException e) {
            return CustomHttpFactory.NOT_FOUND();
        }
    }

    public static boolean ifFileTypeRequested(String url) {
        String type = getFileTypeFromUrl(url);
        return Arrays.stream(supportedFileTypes).filter(item -> item.equals(type)).count() == 1L;
    }
}
