package Cosmos.Views;

import Cosmos.Data.Database;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("/")
public class HomeView  extends AppLayout {

    public HomeView() {
        setContent(createContent());
    }

    Component createContent() {
        H1 cosmos = new H1("cosmos");

        Database database = new Database();
        H6 info = new H6("Found " + database.getWebContentCount() + " WebPages using " + database.getWebIndexCount() + " Indicies.");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        TextField input = new TextField();
        layout.add(input);

        Button search = new Button("Search", new Icon(VaadinIcon.SEARCH));
        search.addClickListener(event -> {
           UI.getCurrent().navigate("/search/" + input.getValue());
        });
        search.addClickShortcut(Key.ENTER);
        layout.add(search);

        VerticalLayout content = new VerticalLayout(cosmos, layout, info);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setHeight("100%");
        return content;
    }

}