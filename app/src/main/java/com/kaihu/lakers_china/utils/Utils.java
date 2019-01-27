package com.kaihu.lakers_china.utils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kai on 2019/1/27
 * Email：kaihu1989@gmail.com
 * Feature:
 */
public class Utils {
    /**
     * GlideUrl防盗链
     * */
    public static GlideUrl getDoorChainUrl(String url){
        GlideUrl glideUrl = new GlideUrl(url, new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                //不一定都要添加，具体看原站的请求信息
                header.put("Referer", "https://www.lakerschina.com");
                return header;
            }
        });
        return glideUrl;
    }
}
