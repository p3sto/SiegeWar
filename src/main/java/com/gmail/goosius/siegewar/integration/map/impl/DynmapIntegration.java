package com.gmail.goosius.siegewar.integration.map.impl;

import com.gmail.goosius.siegewar.SiegeWar;
import com.gmail.goosius.siegewar.integration.map.MapIntegration;
import com.gmail.goosius.siegewar.settings.Settings;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class DynmapIntegration extends MapIntegration {
	private DynmapCommonAPI api;
	private MarkerSet markerSet;

	protected DynmapIntegration(SiegeWar plugin) {
		super(plugin);
	}

	@Override
	public void initialize() {
		DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
			@Override
			public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
				api = dynmapCommonAPI;
				SiegeWar.info("Enabling dynmap support");
			}
		});
	}

	@Override
	public void registerMarkers() {
		MarkerAPI markerAPI = api.getMarkerAPI();
		if (markerSet == null) {

			// Create markers for crosssedwords and fire
			MarkerIcon activeIcon;
			MarkerIcon dormantIcon;
			try (InputStream activeImg = new FileInputStream(Settings.getActiveIconFile());
					InputStream dormantImg = new FileInputStream(Settings.getDormantIconFile());
			) {
				activeIcon = markerAPI.createMarkerIcon(ACTIVE_BANNER_ICON_ID, "Active Banner", activeImg);
				dormantIcon = markerAPI.createMarkerIcon(DORMANT_BANNER_ICON_ID, "Dormant Banner", dormantImg);
			} catch (IOException e) {
				SiegeWar.severe("Failed to load icons from class loader");
				return;
			}

			markerSet = markerAPI.createMarkerSet(
					"sisgewar.markerset",
					SiegeWarSettings.getDynmapLayerName(),
					Set.of(dormantIcon, activeIcon),
					false
			);
			markerSet.setHideByDefault(false);
		}
	}

	@Override
	public void addMarker() {

	}

	@Override
	public void deleteMarker(UUID uuid) {

	}
}
