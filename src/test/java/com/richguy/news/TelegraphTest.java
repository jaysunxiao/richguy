package com.richguy.news;

import com.richguy.controller.NewsController;
import com.zfoo.protocol.util.JsonUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author godotg
 * @version 3.0
 */
@Ignore
public class TelegraphTest {

    @Test
    public void test() throws IOException, InterruptedException {
        var response = new NewsController().requestForTelegraph();

        System.out.println(JsonUtils.object2String(response));
    }

}
