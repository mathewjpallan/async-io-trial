package controllers;

import com.typesafe.config.Config;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class TestController extends Controller {

    @Inject
    private WSClient wsClient;

    @Inject
    private Config config;

    public TestController() {
    }

    public CompletionStage<Result> testAsync(String delaytime) {
        CompletionStage<Result> data = wsClient.url(
                config.getString("echo.endpoint.baseapipath") + "/echo/hello/after/" + delaytime).get()
                .thenApply(r -> ok(r.getBody()));
        return data;
    }

    public Result testSync(String delaytime) throws Exception {
        CompletionStage<String> data = wsClient.url(
                config.getString("echo.endpoint.baseapipath") + "/echo/hello/after/" + delaytime).get()
                .thenApply(r -> r.getBody());
        String result = data.toCompletableFuture().get();
        return ok(result);
    }
}

