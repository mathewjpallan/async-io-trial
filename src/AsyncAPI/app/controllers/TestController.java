package controllers;

import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class TestController extends Controller {

    @Inject
    private WSClient wsClient;

    public TestController() {
    }

    public CompletionStage<Result> testAsync() {
        CompletionStage<Result> data = wsClient.url("http://172.21.0.7:9595/echoafter/500").get().thenApply(r -> ok(r.getBody()));
        return data;
    }

    public Result testSync() throws Exception {
        CompletionStage<String> data = wsClient.url("http://172.21.0.7:9595/echoafter/500").get().thenApply(r -> r.getBody());
        System.out.println("invoked : " + System.currentTimeMillis());
        String result = data.toCompletableFuture().get();
        System.out.println("result is ready : " + System.currentTimeMillis());
        return ok(result);
    }
}

