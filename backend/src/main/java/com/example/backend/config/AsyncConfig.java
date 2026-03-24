package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "photoUploadExecutor")
    public Executor photoUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 1. Số lượng "nhân viên" luôn túc trực làm việc
        executor.setCorePoolSize(10);

        // 2. Số lượng "nhân viên" tối đa được gọi thêm khi có quá nhiều người upload
        executor.setMaxPoolSize(50);

        // 3. Hàng đợi: Nếu cả 50 người đều đang bận, sức chứa tối đa của phòng chờ là 500 bức ảnh
        executor.setQueueCapacity(500);

        // Đặt tên luồng để dễ debug (xem log)
        executor.setThreadNamePrefix("PhotoWorker-");

        // 4. CHÍNH SÁCH BẢO VỆ SERVER (Cực kỳ quan trọng)
        // Nếu hàng đợi 500 cũng đầy nốt, hệ thống sẽ ép cái luồng chính (của Tomcat) tự đi mà làm,
        // giúp hãm phanh tốc độ nhận request lại, tránh sập RAM.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
