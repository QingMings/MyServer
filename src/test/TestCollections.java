import com.iezview.server.model.Picture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TestCollections {

    public static final String UNKNOW_NAME="UNKNOW_NAME";
    public static void main(String[] args) {
        Picture picture = new Picture("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles/MR2017112709701511761158513/IMG_3227.JPG");

        Optional<Picture> mybeFooo = Optional.ofNullable(null);

                    mybeFooo.orElse(new Picture("/Users/shishifanbuxie/IdeaProjects/MyServer/.temp/IMG_3227__thumb.JPG"));
            String s=  mybeFooo.map(u->u.getName()).map(name->name.toLowerCase()).orElse(UNKNOW_NAME);
        System.out.println(s);

    }
}
