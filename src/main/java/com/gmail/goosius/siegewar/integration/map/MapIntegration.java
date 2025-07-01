package com.gmail.goosius.siegewar.integration.map;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.SiegeWar;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.enums.SiegeStatus;
import com.gmail.goosius.siegewar.objects.BattleSession;
import com.gmail.goosius.siegewar.objects.Siege;

import java.util.*;

public abstract class MapIntegration {
	protected static final String DORMANT_BANNER_ICON_ID = "siegewar.dormant";
	protected static final String ACTIVE_BANNER_ICON_ID = "siegewar.active";
	protected final SiegeWar plugin;

	protected MapIntegration(SiegeWar plugin) {
		this.plugin = plugin;
	}

	public abstract void initialize();

	public abstract void registerMarkers();

	public abstract void addMarker();

	public abstract void deleteMarker(UUID uuid);

	public void updateSiegeMarker(UUID townId) {
		Siege siege = SiegeController.getSiegeByTownUUID(townId);

		// Remove the marker if the siege is not present or in progress
		if (Objects.isNull(siege) || siege.getStatus() != SiegeStatus.IN_PROGRESS) {
			deleteMarker(townId);
		}


	}

	public void updateSiegeMarkers() {

	}


	/**
	 * A siege is inactive if there is no significant activity there (e.g., kills, banner control).
	 * This state is represented by a fire icon on the map. If the battle becomes active, the icon
	 * changes to crossed-swords.
	 *
	 * @return true if the siege is inactive
	 */
	public boolean isSiegeDormant(Siege siege) {
		return !BattleSession.getBattleSession().isActive()
				|| (siege.getAttackerBattlePoints() == 0
				&& siege.getDefenderBattlePoints() == 0
				&& siege.getBannerControllingSide() == SiegeSide.NOBODY
				&& siege.getBannerControlSessions().isEmpty());
	}
}
