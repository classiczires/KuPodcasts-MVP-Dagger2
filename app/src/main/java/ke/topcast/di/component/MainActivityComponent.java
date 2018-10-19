package ke.topcast.di.component;

import javax.inject.Singleton;

import dagger.Component;
import ke.topcast.di.module.MainActivityModule;
import ke.topcast.view.activities.MainActivity;

@Singleton
@Component( modules = {MainActivityModule.class} )
public interface MainActivityComponent {
    MainActivity inject(MainActivity activity);
}
