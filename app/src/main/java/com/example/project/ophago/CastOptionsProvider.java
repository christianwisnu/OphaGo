package com.example.project.ophago;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

public class CastOptionsProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context context) {
        /*CastOptions castOptions = new CastOptions.Builder()
                .setResumeSavedSession(true)
                .setEnableReconnectionService(true)
                .setReceiverApplicationId(context.getString(R.string.app_id))
                .build();
*/
        return null;
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }

}
