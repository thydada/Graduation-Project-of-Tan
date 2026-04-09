package com.example.gpot.listener;

import com.example.gpot.service.PackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    private static final String PACKAGE_KEY_PREFIX = "gpot:package:timeout:";

    private PackageService packageService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer container) {
        super(container);
    }

    @Autowired
    public void setPackageService(PackageService packageService) {
        this.packageService = packageService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());

        if (expiredKey.startsWith(PACKAGE_KEY_PREFIX)) {
            try {
                String packageIdStr = expiredKey.substring(PACKAGE_KEY_PREFIX.length());
                Long packageId = Long.parseLong(packageIdStr);

                logger.info("检测到包裹超时，包裹ID: {}", packageId);

                if (packageService != null) {
                    packageService.handlePickupTimeout(packageId);
                    logger.info("包裹超时处理完成，包裹ID: {}", packageId);
                } else {
                    logger.error("PackageService 未注入，跳过处理");
                }
            } catch (Exception e) {
                logger.error("处理包裹超时事件失败，key: {}, error: {}", expiredKey, e.getMessage(), e);
            }
        }
    }
}
