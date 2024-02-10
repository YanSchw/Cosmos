package Cosmos.Views;

import Cosmos.Data.Database;
import Cosmos.Data.WebCrawler;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Theme(variant=Lumo.LIGHT)
public class Application implements AppShellConfigurator {

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		Database.setup();
		WebCrawler.init(4);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
