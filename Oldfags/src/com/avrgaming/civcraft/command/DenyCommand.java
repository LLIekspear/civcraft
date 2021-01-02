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
package com.avrgaming.civcraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.threading.tasks.CivLeaderQuestionTask;
import com.avrgaming.civcraft.threading.tasks.PlayerQuestionTask;

public class DenyCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if (!(sender instanceof Player)) {
			CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_MustBePlayer"));
			return false;
		}
		
		Player player = (Player)sender;
		
		PlayerQuestionTask task = (PlayerQuestionTask) CivGlobal.getQuestionTask(player.getName());
		if (task != null) {
			/* We have a question, and the answer was "Accepted" so notify the task. */
			synchronized(task) {
				task.setResponse("deny");
				task.notifyAll();
			}
			return true;
		}

		Resident resident = CivGlobal.getResident(player);
		if (resident.getCiv().getLeaderGroup().hasMember(resident)) {
			CivLeaderQuestionTask civTask = (CivLeaderQuestionTask) CivGlobal.getQuestionTask("civ:"+resident.getCiv().getName());
			if (civTask != null) {
				synchronized(civTask) {
					civTask.setResponse("deny");
					civTask.setResponder(resident);
					civTask.notifyAll();
				}
			}
			return true;
		}
		

		CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_acceptError"));
		return false;
	}
}
