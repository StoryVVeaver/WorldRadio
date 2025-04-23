package by.roman.worldradio0.business_logic.modules;

import android.app.NotificationManager;
import android.content.Context;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.player.RadioManager;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class PlayerModule {

    @Provides
    @Singleton
    public static RadioManager provideRadioManager(@ApplicationContext Context context) {
        return new RadioManager(context);
    }
    @Provides
    @Singleton
    public static NotificationManager provideNotificationManager(@ApplicationContext Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
