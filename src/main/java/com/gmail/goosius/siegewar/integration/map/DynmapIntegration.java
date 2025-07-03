package com.gmail.goosius.siegewar.integration.map;

import com.gmail.goosius.siegewar.SiegeWar;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.settings.Settings;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import com.palmergames.bukkit.towny.object.Translation;
import org.bukkit.Location;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.Marker;
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
	private MarkerAPI markerAPI;
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
				markerAPI = api.getMarkerAPI();
				SiegeWar.info("Enabling dynmap support");
			}
		});
	}

	@Override
	public void registerMarkers() {
		MarkerAPI markerAPI = api.getMarkerAPI();
		if (markerSet == null) {
			MarkerIcon activeIcon;
			MarkerIcon dormantIcon;
			try (InputStream activeImg = new FileInputStream(Settings.getActiveIconFile());
				 InputStream dormantImg = new FileInputStream(Settings.getDormantIconFile())) {
				activeIcon = markerAPI.createMarkerIcon(ACTIVE_BANNER_ICON_ID, "Siege Banner", activeImg);
				dormantIcon = markerAPI.createMarkerIcon(DORMANT_BANNER_ICON_ID, "Siege Banner", dormantImg);
			} catch (IOException e) {
				SiegeWar.severe("Failed to load icons from class loader");
				return;
			}
			markerSet = markerAPI.createMarkerSet(
					"sisgewar.markerset",
					SiegeWarSettings.getWebmapLayerName(),
					Set.of(dormantIcon, activeIcon),
					false
			);
			markerSet.setHideByDefault(false);
		}
	}

	@Override
	public void updateMarker(Siege siege, boolean useDormant) {
		UUID siegeId = siege.getDefender().getUUID();
		String id = siegeId.toString();
		Marker siegeMarker = markerSet.findMarker(id);
		MarkerIcon icon = api.getMarkerAPI().getMarkerIcon(useDormant ? DORMANT_BANNER_ICON_ID : ACTIVE_BANNER_ICON_ID);

		// Create icon if it doesn't exist
		if (siegeMarker == null) {
			String label = Translation.of("dynmap_siege_title", siege.getAttackerNameForDisplay(), siege.getDefenderNameForDisplay());
			String world = siege.getFlagBlock().getWorld().getName();
			Location loc = siege.getFlagLocation();
			siegeMarker = markerSet.createMarker(id, label, false, world, loc.getX(), loc.getY(), loc.getZ(), icon, false);
		} else {
			siegeMarker.setMarkerIcon(icon);
		}

		String tooltip = iconToolTip(siege);
		siegeMarker.setDescription(tooltip);
		map.put(siegeId, useDormant ? SiegeIcon.DORMANT : SiegeIcon.ACTIVE);
	}

	@Override
	public void removeMarker(Siege siege) {
		UUID id = siege.getDefender().getUUID();
		Marker siegeMarker = markerSet.findMarker(id.toString());
		siegeMarker.deleteMarker();
		map.remove(id);
	}
}
