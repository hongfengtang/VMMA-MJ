package com.mmh.vmma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.mmh.vmma.ui.dialogs.InitSystem;
import com.mmh.vmma.ui.frames.MainWindow;

@SpringBootApplication
@ComponentScan()
public class VmmaApplication {
	
	private final static Logger logger=LoggerFactory.getLogger(VmmaApplication.class);

	public static void main(String[] args) {
//		SpringApplication.run(VmmaApplication.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(VmmaApplication.class);
		builder.bannerMode(Banner.Mode.OFF);
		ConfigurableApplicationContext context = builder.headless(false).web(WebApplicationType.NONE).run(args);
		logger.info("初始化系統。");
		context.getBean(InitSystem.class).setModal(true);
		context.getBean(InitSystem.class).setVisible(true);
		logger.info("啟動主窗口。");
		context.getBean(MainWindow.class).setVisible(true);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			logger.info("Let's inspect the beans provided by Spring Boot:");
//
//			String[] beanNames = ctx.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				logger.info(beanName);
//			}
//			
//		};
//	}
	
}
