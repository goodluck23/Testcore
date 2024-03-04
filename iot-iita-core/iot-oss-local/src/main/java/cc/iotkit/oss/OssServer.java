package cc.iotkit.oss;

import cc.iotkit.common.oss.constant.OssConstant;
import cc.iotkit.common.oss.properties.OssProperties;
import cc.iotkit.common.redis.utils.RedisUtils;
import cn.hutool.core.util.RandomUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class OssServer {

    private final int port = RandomUtil.randomInt(1660, 1880);

    @PostConstruct
    public void init() {
        RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, OssProperties.builder()
                .endpoint(String.format("http://127.0.0.1:%d", port))
                .build());
    }


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpVerticle httpVerticle=new HttpVerticle();
        Future<String> future = vertx.deployVerticle(httpVerticle);
        future.onSuccess((s -> {
            log.info("http plugin startup success");
        }));
        future.onFailure((e) -> {
            log.error("http plugin startup failed", e);
        });
    }

}
