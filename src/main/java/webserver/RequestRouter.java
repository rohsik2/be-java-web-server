package webserver;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpMock.CustomHttpRequest;
import webserver.httpMock.CustomHttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class RequestRouter {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static RequestRouter requestRouter;
    public final Map<String, RequestService> requestMap = new HashMap<>() {{
        put("/index.html", (req, res) -> getFile(req, res));
    }};

    private RequestRouter(){
        ;
    }

    public static RequestRouter getRequestRouter(){
        if(requestRouter == null)
            requestRouter = new RequestRouter();
        return requestRouter;
    }

    public void doRoute(CustomHttpRequest req, CustomHttpResponse res){
        if(requestMap.containsKey(req.getCurrentRootUrl())){
            requestMap.get(req.getCurrentRootUrl()).doSomething(req, res);
        }
        else{
            logger.info("Not match any url : ", req.getCurrentRootUrl());
        }
    }

    public void getFile(CustomHttpRequest req, CustomHttpResponse res){
        try {
            URL resource = getClass().getClassLoader().getResource("./templates");
            String filePath = resource.getPath();
            byte[] file = Files.readAllBytes(new File(filePath + req.getCurrentRootUrl()).toPath());
            res.addToBody(ArrayUtils.toObject(file));
        } catch (IOException e) {
            logger.error("file not found at " + "./templates" + req.getCurrentRootUrl());
            throw new RuntimeException(e);
        }

    }


}
