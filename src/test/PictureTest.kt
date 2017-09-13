import com.iezview.server.model.Picture
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject

fun main(args: Array<String>) {


    var pic = Picture("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles/0000_20170822_090740_0652_4229.bmp")
    println(Json.encodePrettily(pic))
//    println(JsonObject.mapFrom(pic).encodePrettily())
}