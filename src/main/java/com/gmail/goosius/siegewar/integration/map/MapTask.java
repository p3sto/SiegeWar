package com.gmail.goosius.siegewar.integration.map;

import org.bukkit.scheduler.BukkitRunnable;

public class MapTask extends BukkitRunnable {

	private final MapIntegration integration;

	public MapTask(MapIntegration integration) {
		this.integration = integration;
	}

	@Override
	public void run() {
		integration.updateSiegeMarkers();
	}
}
