package com.gmail.goosius.siegewar.integration.map;

import com.gmail.goosius.siegewar.SiegeWar;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.settings.Settings;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.Translation;
import org.bukkit.Location;
import org.dynmap.markers.MarkerAPI;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Icon;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SquaremapIntegration extends MapIntegration {
	private static final Key LAYER_ID = Key.of("siegewar_layer");
	private Squaremap api;
	private SimpleLayerProvider layer;
	private Key activeIcon;
	private Key dormantIcon;

	protected SquaremapIntegration(SiegeWar plugin) {
		super(plugin);
	}

	@Override
	public void initialize() {
		this.api = SquaremapProvider.get();
		this.layer = SimpleLayerProvider.builder(SiegeWarSettings.getWebmapLayerName())
				.defaultHidden(false)
				.showControls(true)
				.layerPriority(5)
				.zIndex(250)
				.build();

		// Register the layer on all towny worlds
		List<TownyWorld> worlds = TownyUniverse.getInstance().getTownyWorlds();
		for (TownyWorld world : worlds) {
			WorldIdentifier id = BukkitAdapter.worldIdentifier(world.getBukkitWorld());
			Optional<MapWorld> opt = api.getWorldIfEnabled(id);
			opt.ifPresent(map -> map.layerRegistry().register(LAYER_ID, layer));
		}
		SiegeWar.info("Enabling squaremap support...");
	}

	@Override
	public void registerMarkers() {
		try {
			Registry<BufferedImage> registry = api.iconRegistry();
			activeIcon = Key.of(ACTIVE_BANNER_ICON_ID);
			BufferedImage active = ImageIO.read(Settings.getActiveIconFile());
			registry.register(activeIcon, active);

			dormantIcon = Key.of(DORMANT_BANNER_ICON_ID);
			BufferedImage passive = ImageIO.read(Settings.getDormantIconFile());
			registry.register(dormantIcon, passive);

			SiegeWar.info("Registered squaremap markers");
		} catch (IOException e) {
			SiegeWar.severe("Failed to register squaremap markers ");
		}
	}

	@Override
	public void updateMarker(Siege siege, boolean useDormantIcon) {
		// Configure icon settings
		Location loc = siege.getFlagLocation();
		Point point = Point.of(loc.getX(), loc.getZ());
		Key iconKey = useDormantIcon ? dormantIcon : activeIcon;
		Icon icon = Marker.icon(point, iconKey, 16);
		String name = Translation.of("dynmap_siege_title", siege.getAttackerNameForDisplay(), siege.getDefenderNameForDisplay());
		String toolTip = iconToolTip(siege);
		icon.markerOptions(MarkerOptions.builder()
				.hoverTooltip(name)
				.clickTooltip(toolTip)
				.build());

		// Add to layer and map
		UUID id = siege.getDefender().getUUID();
		Key key = Key.key(id.toString());
		layer.addMarker(key, icon);
	}

	@Override
	public void removeMarker(Siege siege) {
		UUID id = siege.getDefender().getUUID();
		Key key = Key.key(id.toString());
		layer.removeMarker(key);
	}
}
