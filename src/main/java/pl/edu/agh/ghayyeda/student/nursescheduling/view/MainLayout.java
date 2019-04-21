package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.builder.interfaces.NavigationElementContainer;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.Push;

import static com.vaadin.flow.shared.communication.PushMode.MANUAL;

@Push
@StyleSheet("frontend://css/main.css")
public class MainLayout extends AppLayoutRouterLayout {

    private final NavigationComponents navigationComponents;

    public MainLayout(NavigationComponents navigationComponents) {
        this.navigationComponents = navigationComponents;
        init();
    }

    private void init() {
        init(AppLayoutBuilder.get(Behaviour.LEFT_HYBRID)
                .withTitle("Nurse Scheduling Assistant")
                .withAppMenu(buildLeftAppMenu())
                .build());
    }

    private NavigationElementContainer buildLeftAppMenu() {
        var builder = LeftAppMenuBuilder.get();
        navigationComponents.getNavigationComponents().forEach(builder::add);
        return builder.build();
    }

}


