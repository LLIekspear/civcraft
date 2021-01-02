package com.avrgaming.civcraft.interactive;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.global.reports.ReportManager;
import com.avrgaming.global.reports.ReportManager.ReportType;

public class InteractiveReportPlayer implements InteractiveResponse {

	String playerName;
	
	public InteractiveReportPlayer(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}

		if (message.equalsIgnoreCase("cancel")) {
			CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+CivSettings.localize.localizedString("interactive_report_cancel"));
			resident.clearInteractiveMode();
			return;
		}
		
		ReportType selectedType = null;
		for (ReportType type : ReportManager.ReportType.values()) {
			if (message.equalsIgnoreCase(type.name())) {
				selectedType = type;
				break;
			}
		}
		
		if (selectedType == null) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("interactive_report_category")+" ("+ReportManager.getReportTypes()+")");
			return;
		}
		
		CivMessage.send(player, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("interactive_report_description"));
		resident.setInteractiveMode(new InteractiveReportPlayerMessage(playerName, selectedType));		
		
	}

}
