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
package com.avrgaming.civcraft.threading.timers;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Civilization;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.structure.TownHall;
import com.avrgaming.civcraft.threading.CivAsyncTask;

public class BeakerTimer extends CivAsyncTask {

	//private double beakersPerRun;
	
	public static final int BEAKER_PERIOD = 60;
	
	public BeakerTimer(int periodInSeconds) {
		
	//	this.beakersPerRun = ((double)periodInSeconds/60);
	}
	
	@Override
	public void run() {
		
		for (Civilization civ : CivGlobal.getCivs()) {
			
			if (civ.getCapitolName() == null) {
				CivMessage.sendCivRightMessage(civ, CivSettings.localize.localizedString("beaker_ErrorNoCapitol"));
				continue;
			}
			
			Town town = CivGlobal.getTown(civ.getCapitolName());
			if (town == null) {
				CivMessage.sendCivRightMessage(civ, CivSettings.localize.localizedString("var_beaker_noCapitol",civ.getCapitolName()));
				continue;
			}
			
			TownHall townhall = town.getTownHall();
			if (townhall == null) {
				CivMessage.sendCivRightMessage(civ, CivSettings.localize.localizedString("beaker_noCapitolHall"));
			}
			
			try {
				/* 
				 * The base_beakers defines the number of beakers per hour to give.
				 * This timer runs every min, so dividing my 60 will give us the number
				 * of beakers per min.
				 */
				if (civ.getResearchTech() != null) {
					civ.addBeakers(civ.getBeakers() / BEAKER_PERIOD);
				} else {
					civ.processUnusedBeakers();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
