package com.gmail.goosius.siegewar.integration.map.impl;

import com.gmail.goosius.siegewar.SiegeWar;
import com.gmail.goosius.siegewar.integration.map.MapIntegration;
import com.gmail.goosius.siegewar.settings.Settings;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.Registry;
import xyz.jpenilla.squaremap.api.Squaremap;
import xyz.jpenilla.squaremap.api.SquaremapProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SquaremapIntegration extends MapIntegration {
	private Squaremap api;

	protected SquaremapIntegration(SiegeWar plugin) {
		super(plugin);
	}

	@Override
	public void initialize() {
		api = SquaremapProvider.get();
		SiegeWar.info("Enabling squaremap support...");
	}

	@Override
	public void registerMarkers() {
		try {
			// Register crossed swords and fire icon
			Registry<BufferedImage> registry = api.iconRegistry();
			{
				Key key = Key.of(DORMANT_BANNER_ICON_ID);
				BufferedImage img = ImageIO.read(Settings.getDormantIconFile());
				registry.register(key, img);
			}
			{
				Key key = Key.of(ACTIVE_BANNER_ICON_ID);
				BufferedImage img = ImageIO.read(Settings.getActiveIconFile());
				registry.register(key, img);
			}
			SiegeWar.info("Registered squaremap icons");
		} catch (IOException e) {
			SiegeWar.severe("Failed to register squaremap icons");
		}
	}

	@Override
	public void addMarker() {

	}

	@Override
	public void deleteMarker(UUID uuid) {

	}

	private BufferedImage loadIcon(@NotNull File file) throws IOException {
		return ImageIO.read(file);
	}
}
