package com.oneplus.shit.settings;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import com.oneplus.shit.settings.utils.FileUtils;
import com.oneplus.shit.settings.ShitPanelSettings;

import com.oneplus.shit.settings.R;

public class SpectrumTileService extends TileService {

    @Override
        public void onStartListening() {
            int currentState = FileUtils.getintProp(ShitPanelSettings.SPECTRUM_SYSTEM_PROPERTY, 0);

            Tile tile = getQsTile();
            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(getResources().getStringArray(R.array.spectrum_profiles)[currentState]);

            tile.updateTile();
            super.onStartListening();
    }

    @Override
        public void onClick() {
            int currentState = FileUtils.getintProp(ShitPanelSettings.SPECTRUM_SYSTEM_PROPERTY, 0);

            int nextState;
            if (currentState == 2) {
            nextState = 0;
        } else {
            nextState = currentState + 1;
        }

        Tile tile = getQsTile();
        FileUtils.setintProp(ShitPanelSettings.SPECTRUM_SYSTEM_PROPERTY, nextState);
        tile.setLabel(getResources().getStringArray(R.array.spectrum_profiles)[nextState]);

        tile.updateTile();
        super.onClick();
    }
}

