package com.gmail.goosius.siegewar.integration.map;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.SiegeWar;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.enums.SiegeStatus;
import com.gmail.goosius.siegewar.objects.BattleSession;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Translation;
import com.palmergames.util.StringMgmt;
import org.apache.commons.lang.WordUtils;

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

	public abstract void updateMarker(Siege siege, boolean useDormantIcon);

	public abstract void removeMarker(Siege siege);

	public void updateSiegeMarkers() {
		for (Siege siege : SiegeController.getSieges()) {

			// Delete marker for sieges that aren't active or not present
			if (siege.getStatus() != SiegeStatus.IN_PROGRESS) {
				removeMarker(siege);
				continue;
			}

			UUID id = siege.getDefender().getUUID();
			SiegeIcon icon = map.get(id);
			boolean dormant = icon == SiegeIcon.ACTIVE && isSiegeDormant(siege);
			map.put(id, dormant ? SiegeIcon.DORMANT : SiegeIcon.ACTIVE);
			updateMarker(siege, dormant);
		}
	}

	protected String iconToolTip(Siege siege) {
		List<String> lines = new ArrayList<>();
		String name = Translation.of("dynmap_siege_title", siege.getAttackerNameForDisplay(), siege.getDefenderNameForDisplay());
		lines.add(Translation.of("dynmap_siege_town", siege.getTown().getName()));
		lines.add(Translation.of("dynmap_siege_type", siege.getSiegeType().getName()));
		if (TownyEconomyHandler.isActive())
			lines.add(Translation.of("dynmap_siege_war_chest", TownyEconomyHandler.getFormattedBalance(siege.getWarChestAmount())));

		lines.add(Translation.of("dynmap_siege_progress", siege.getNumBattleSessionsCompleted(), SiegeWarSettings.getSiegeDurationBattleSessions()));
		lines.add(Translation.of("dynmap_siege_status", siege.getStatus().getName()));
		lines.add(Translation.of("dynmap_siege_balance", siege.getSiegeBalance()));
		lines.add(Translation.of("dynmap_siege_banner_control",
				WordUtils.capitalizeFully(siege.getBannerControllingSide().name())
						+ (siege.getBannerControllingSide() == SiegeSide.NOBODY ? "" : " (" + siege.getBannerControllingResidents().size() + ")")));
		lines.add(Translation.of("dynmap_siege_battle_points", siege.getFormattedAttackerBattlePoints(), siege.getFormattedDefenderBattlePoints()));
		lines.add(Translation.of("dynmap_siege_battle_time_left", siege.getFormattedBattleTimeRemaining()));
		return "<b>" + name + "</b><hr>" + StringMgmt.join(lines, "<br>");
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
