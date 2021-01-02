/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package com.avrgaming.civcraft.questions;

import org.bukkit.entity.Player;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.AlreadyRegisteredException;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.util.CivColor;

public class JoinTownResponse implements QuestionResponseInterface {

	public Town town;
	public Resident resident;
	public Player sender;
	
	@Override
	public void processResponse(String param) {
		if (param.equalsIgnoreCase("accept")) {
			CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("var_joinTown_accepted",resident.getName()));
			
			try {
				town.addResident(resident);
			} catch (AlreadyRegisteredException e) {
				CivMessage.sendError(sender, CivSettings.localize.localizedString("var_joinTown_errorInTown",resident.getName()));
				return;
			}

			CivMessage.sendTownRightMessage(town, CivSettings.localize.localizedString("var_joinTown_alert",resident.getName()));
			resident.save();
		} else {
			CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("var_joinTown_Declined",resident.getName()));
		}
	}
	
	@Override
	public void processResponse(String response, Resident responder) {
		processResponse(response);		
	}
}
