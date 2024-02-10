package Cosmos.Views;

import Cosmos.Data.Database;
import Cosmos.Data.SearchResult;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
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

    Component createContent(String query) {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        SearchResult result = Database.processQuery(query);
        ArrayList<String> urls = new ArrayList<>(result.matches.keySet());

        ArrayList<Component> components = new ArrayList<>();

        for (String url : urls) {
            Button button = new Button(url);
            button.setWidth("80%");
            button.addClickListener(event -> UI.getCurrent().getPage().setLocation(url));
            components.add(button);
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