package windoctor7.github.io.spring4.sse.Spring4SSE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Spring4SseApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring4SseApplication.class, args);
	}
}
