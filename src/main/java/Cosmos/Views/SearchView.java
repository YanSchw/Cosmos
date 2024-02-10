package Cosmos.Views;

import Cosmos.Data.Database;
import Cosmos.Data.SearchResult;
import Cosmos.Data.WebPage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;

@Route("/search")
class SearchView extends AppLayout implements HasUrlParameter<String> {

    public SearchView() {
        //setContent(createContent());
    }

    void addNestedContent(ArrayList<Component> components, int i, VerticalLayout parent, boolean canNest) {
        if (i >= components.size()) {
            return;
        }

        parent.add(components.get(i));

        if (i % 50 == 0 && canNest) {
            VerticalLayout newParent = new VerticalLayout();
            newParent.setAlignItems(FlexComponent.Alignment.CENTER);
            Details details = new Details("Show more", newParent);
            parent.add(details);
            addNestedContent(components, i + 1, newParent, false);
        } else {
            addNestedContent(components, i + 1, parent, true);
        }
    }


    private Component createWebEntry(WebPage page) {
        Span span = new Span();
        span.getElement().getThemeList().add("badge contrast");
        span.setWidth("80%");
        span.setHeight("175px");

        Button title = new Button(new Span(page.title));
        title.addClickListener(event -> UI.getCurrent().getPage().setLocation(page.url));

        Span url = new Span(page.url);
        url.setWidth("95%");

        VerticalLayout layout = new VerticalLayout(title, url);
        span.add(layout);
        return span;
    }

    public Component createContent(String query) {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        SearchResult result = Database.processQuery(query);
        ArrayList<WebPage> pages = new ArrayList<>(result.matches.values());

        ArrayList<Component> components = new ArrayList<>();

        for (WebPage page : pages) {
            components.add(createWebEntry(page));
        }
        addNestedContent(components, 0, layout, false);

        return layout;
    }
    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (!parameter.isEmpty()) {
            setContent(createContent(parameter));
        } else {
            UI.getCurrent().navigate("/");
        }
    }

}