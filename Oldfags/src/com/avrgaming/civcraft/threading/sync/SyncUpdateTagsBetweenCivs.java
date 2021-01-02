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
package com.avrgaming.civcraft.threading.sync;

import java.util.HashSet;
import java.util.Set;

import net.md_5.itag.iTag;

import org.bukkit.entity.Player;

import com.avrgaming.civcraft.config.CivSettings;


public class SyncUpdateTagsBetweenCivs implements Runnable {
	Set<Player> civList = new HashSet<Player>();
	Set<Player> otherCivList = new HashSet<Player>();
	
	public SyncUpdateTagsBetweenCivs(Set<Player> civList, Set<Player> otherCivList) {
		this.civList = civList;
		this.otherCivList = otherCivList;
	}

	@Override
	public void run() {
		if (CivSettings.hasITag) {
			for (Player player : civList) {
				if (!otherCivList.isEmpty()) {
					iTag.instance.refreshPlayer(player, otherCivList);
				}
			}
			
			for (Player player : otherCivList) {
				if (!civList.isEmpty()) {
					iTag.instance.refreshPlayer(player, civList);
				}
			}
		}
	}
	
}
