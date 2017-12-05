import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientResponse
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.awaitEvent
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
@RunWith(VertxUnitRunner::class)
class TestClient{

    lateinit var vertx:Vertx;
    @Before
    fun init(){
        vertx = Vertx.vertx();
    }

    @Test
     fun test_httpBaidu(context:TestContext){
        var async = context.async()
        var  client = WebClient.create(vertx)
        launch(vertx.dispatcher()){
            var   result = awaitResult<HttpResponse<Buffer>> {  client.get("www.baidu.com","/").send(it) }
            println(result.statusCode())
            println(result.bodyAsString())
            async.complete()
            async.await(5000)
        }

    }
}