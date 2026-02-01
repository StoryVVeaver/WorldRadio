package by.roman.worldradio0.business_logic;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import android.content.Context;
import android.util.Log;
import com.bumptech.glide.GlideBuilder;

@GlideModule
public final class WorldRadioGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setLogLevel(Log.ERROR);
    }
}