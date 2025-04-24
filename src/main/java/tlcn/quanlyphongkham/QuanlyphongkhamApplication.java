package tlcn.quanlyphongkham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "tlcn.quanlyphongkham")
@EnableScheduling
public class QuanlyphongkhamApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanlyphongkhamApplication.class, args);

	}

}


