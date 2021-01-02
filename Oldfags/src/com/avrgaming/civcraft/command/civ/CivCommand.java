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
package com.avrgaming.civcraft.command.civ;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.avrgaming.civcraft.command.CommandBase;
import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.endgame.EndConditionDiplomacy;
import com.avrgaming.civcraft.endgame.EndGameCondition;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.exception.InvalidConfiguration;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Civilization;
import com.avrgaming.civcraft.object.Relation.Status;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.sessiondb.SessionEntry;
import com.avrgaming.civcraft.structure.TownHall;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.civcraft.war.War;

public class CivCommand extends CommandBase {

	@Override
	public void init() {		
		command = "/civ";
		displayName = CivSettings.localize.localizedString("cmd_civ_name");
		
		commands.put("townlist", CivSettings.localize.localizedString("cmd_civ_townlistDesc"));
		commands.put("deposit", CivSettings.localize.localizedString("cmd_civ_depositDesc"));
		commands.put("withdraw", CivSettings.localize.localizedString("cmd_civ_withdrawDesc"));
		commands.put("info", CivSettings.localize.localizedString("cmd_civ_infoDesc"));
		commands.put("show", CivSettings.localize.localizedString("cmd_civ_showDesc"));
		commands.put("list", CivSettings.localize.localizedString("cmd_civ_listDesc"));
		commands.put("research", CivSettings.localize.localizedString("cmd_civ_researchDesc"));
		commands.put("gov", CivSettings.localize.localizedString("cmd_civ_govDesc"));
		commands.put("time", CivSettings.localize.localizedString("cmd_civ_timeDesc"));
		commands.put("set", CivSettings.localize.localizedString("cmd_civ_setDesc"));
		commands.put("group", CivSettings.localize.localizedString("cmd_civ_groupDesc"));
		commands.put("dip", CivSettings.localize.localizedString("cmd_civ_dipDesc"));
		commands.put("victory", CivSettings.localize.localizedString("cmd_civ_victoryDesc"));
		commands.put("vote", CivSettings.localize.localizedString("cmd_civ_voteDesc"));
		commands.put("votes", CivSettings.localize.localizedString("cmd_civ_votesDesc"));
		commands.put("top5", CivSettings.localize.localizedString("cmd_civ_top5Desc"));
		commands.put("disbandtown", CivSettings.localize.localizedString("cmd_civ_disbandtownDesc"));
		commands.put("revolution", CivSettings.localize.localizedString("cmd_civ_revolutionDesc"));
		commands.put("claimleader", CivSettings.localize.localizedString("cmd_civ_claimleaderDesc"));
		commands.put("motd", CivSettings.localize.localizedString("cmd_civ_motdDesc"));
		commands.put("location", CivSettings.localize.localizedString("cmd_civ_locationDesc"));
	}

	public void location_cmd() throws CivException {
		Civilization civ = getSenderCiv();
	    Resident resident = getResident();
	    if (resident.getCiv() == civ) {
    		for (Town town : civ.getTowns())
    		{
    			String name = town.getName();
    			TownHall townhall = town.getTownHall();
	            if (townhall == null) {
	                    CivMessage.send(sender, CivColor.Rose+CivColor.BOLD+name+CivColor.RESET+CivColor.Gray+CivSettings.localize.localizedString("cmd_civ_locationMissingTownHall"));
	            } else {
	                    CivMessage.send(sender, CivColor.Rose+CivColor.BOLD+name+CivColor.LightPurple+" - "+CivSettings.localize.localizedString("cmd_civ_locationSuccess")+" "+townhall.getCorner());
	            }
    		}
	    }
	}
	
	public void motd_cmd() throws CivException {
		CivMotdCommand cmd = new CivMotdCommand();	
		cmd.onCommand(sender, null, "motd", this.stripArgs(args, 1));
	}
	
	public void claimleader_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		Resident resident = getResident();		
		
		if (!civ.areLeadersInactive()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_claimleaderStillActive"));
		}
		
		civ.getLeaderGroup().addMember(resident);
		civ.getLeaderGroup().save();
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_civ_claimLeaderSuccess",civ.getName()));
		CivMessage.sendCivRightMessage(civ, CivSettings.localize.localizedString("var_cmd_civ_claimLeaderBroadcast",resident.getName()));
	}
	
	public void votes_cmd() throws CivException {
		
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_votesHeading"));
		for (Civilization civ : CivGlobal.getCivs()) {
			Integer votes = EndConditionDiplomacy.getVotesFor(civ);
			if (votes != 0) {
				CivMessage.send(sender, CivColor.LightBlue+
						CivColor.BOLD+civ.getName()+CivColor.White+": "+
						CivColor.LightPurple+CivColor.BOLD+votes+CivColor.White+" "+CivSettings.localize.localizedString("cmd_civ_votes"));
			}
		}
	}
	
	public void victory_cmd() {
		
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_victoryHeading"));
		boolean anybody = false;

		for (EndGameCondition endCond : EndGameCondition.endConditions) {
			ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(endCond.getSessionKey());
			if (entries.size() == 0) {
				continue;
			}
			
			anybody = true;
			for (SessionEntry entry : entries) {
				Civilization civ = EndGameCondition.getCivFromSessionData(entry.value);
				Integer daysLeft = endCond.getDaysToHold() - endCond.getDaysHeldFromSessionData(entry.value);
				CivMessage.send(sender, CivColor.LightBlue+CivColor.BOLD+civ.getName()+CivColor.White+": "+
				CivSettings.localize.localizedString("var_cmd_civ_victoryDays",(CivColor.Yellow+CivColor.BOLD+daysLeft+CivColor.White),(CivColor.LightPurple+CivColor.BOLD+endCond.getVictoryName()+CivColor.White)));
			}
		}
		
		if (!anybody) {
			CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("cmd_civ_victoryNoOne"));
		}
		
	}
	
	public void revolution_cmd() throws CivException {
		Town town = getSelectedTown();
		
		if (War.isWarTime() || War.isWithinWarDeclareDays()) {
			throw new CivException(CivSettings.localize.localizedString("var_cmd_civ_revolutionErrorWar1",War.getTimeDeclareDays()));
		}
		
		if (town.getMotherCiv() == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_revolutionErrorNoMother"));
		}
		
		Civilization motherCiv = town.getMotherCiv();
		
		if (!motherCiv.getCapitolName().equals(town.getName())) {
			throw new CivException(CivSettings.localize.localizedString("var_cmd_civ_revolutionErrorNotCapitol",motherCiv.getCapitolName()));
		}
		
		
		try {
			int revolution_cooldown = CivSettings.getInteger(CivSettings.civConfig, "civ.revolution_cooldown");
		
			Calendar cal = Calendar.getInstance();
			Calendar revCal = Calendar.getInstance();
			
			Date conquered = town.getMotherCiv().getConqueredDate();
			if (conquered == null) {
				throw new CivException(CivSettings.localize.localizedString("cmd_civ_revolutionErrorNoMother"));
			}
			
			revCal.setTime(town.getMotherCiv().getConqueredDate());
			revCal.add(Calendar.DAY_OF_MONTH, revolution_cooldown);
			
			if (!cal.after(revCal)) {
				throw new CivException(CivSettings.localize.localizedString("var_cmd_civ_revolutionErrorTooSoon",revolution_cooldown));
			}
			
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			throw new CivException(CivSettings.localize.localizedString("internalException"));
		}
		
		
		double revolutionFee = motherCiv.getRevolutionFee();
		
		if (args.length < 2 || !args[1].equalsIgnoreCase("yes")) {
			CivMessage.send(sender, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_cmd_civ_revolutionConfirm1",revolutionFee,CivSettings.CURRENCY_NAME));
			CivMessage.send(sender, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("cmd_civ_revolutionConfirm2"));
			CivMessage.send(sender, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("cmd_civ_revolutionConfirm3"));
			CivMessage.send(sender, CivColor.LightGreen+CivSettings.localize.localizedString("cmd_civ_revolutionConfirm4"));
			return;
		}
		
		if(!town.getTreasury().hasEnough(revolutionFee)) {
			throw new CivException(CivSettings.localize.localizedString("var_cmd_civ_revolutionErrorTooPoor",revolutionFee,CivSettings.CURRENCY_NAME));
		}

		/* Starting a revolution! Give back all of our towns to us. */
		HashSet<String> warCivs = new HashSet<String>(); 
		for (Town t : CivGlobal.getTowns()) {
			if (t.getMotherCiv() == motherCiv) {
				warCivs.add(t.getCiv().getName());
				t.changeCiv(motherCiv);
				t.setMotherCiv(null);
				t.save();
			}
		}
		
		for (String warCivName : warCivs) {
			Civilization civ = CivGlobal.getCiv(warCivName);
			if (civ != null) {
				CivGlobal.setRelation(civ, motherCiv, Status.WAR);
				/* THEY are the aggressor in a revolution. */
				CivGlobal.setAggressor(civ, motherCiv, civ);
			}
		}
		
		motherCiv.setConquered(false);
		CivGlobal.removeConqueredCiv(motherCiv);
		CivGlobal.addCiv(motherCiv);
		motherCiv.save();
		
		
		town.getTreasury().withdraw(revolutionFee);
		CivMessage.global(CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_cmd_civ_revolutionSuccess1",motherCiv.getName()));

	}
	
	public void disbandtown_cmd() throws CivException {
		this.validLeaderAdvisor();
		Town town = this.getNamedTown(1);
		
		if (town.getMotherCiv() != null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_disbandtownError"));
		}
		
		if (town.leaderWantsToDisband) {
			town.leaderWantsToDisband = false;
			CivMessage.send(sender, CivSettings.localize.localizedString("cmd_civ_disbandtownErrorLeader"));
			return;
		}	
		
		town.leaderWantsToDisband = true;		

		if (town.leaderWantsToDisband && town.mayorWantsToDisband) {
			CivMessage.sendCivRightMessage(town.getCiv(), CivSettings.localize.localizedString("var_cmd_civ_disbandtownSuccess",town.getName()));
			town.disband();
		}
		
		CivMessage.send(sender, CivColor.Yellow+CivSettings.localize.localizedString("cmd_civ_disbandtownPrompt"));
	}
	
	public void top5_cmd() {	
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_top5Heading"));
//		TreeMap<Integer, Civilization> scores = new TreeMap<Integer, Civilization>();
//		
//		for (Civilization civ : CivGlobal.getCivs()) {
//			if (civ.isAdminCiv()) {
//				continue;
//			}
//			scores.put(civ.getScore(), civ);
//		}
		
		synchronized(CivGlobal.civilizationScores) {
			int i = 1;
			for (Integer score : CivGlobal.civilizationScores.descendingKeySet()) {
				CivMessage.send(sender, i+") "+CivColor.Gold+CivGlobal.civilizationScores.get(score).getName()+CivColor.White+" - "+score);
				i++;
				if (i > 5) {
					break;
				}
			}
		}
		
	}
	
	public void dip_cmd() {
		CivDiplomacyCommand cmd = new CivDiplomacyCommand();	
		cmd.onCommand(sender, null, "dip", this.stripArgs(args, 1));
	}
	
	public void group_cmd() {
		CivGroupCommand cmd = new CivGroupCommand();	
		cmd.onCommand(sender, null, "group", this.stripArgs(args, 1));	
	}
	
	public void set_cmd() {
		CivSetCommand cmd = new CivSetCommand();	
		cmd.onCommand(sender, null, "set", this.stripArgs(args, 1));	
	}
	
	public void time_cmd() throws CivException {
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_timeHeading"));
		Resident resident = getResident();
		ArrayList<String> out = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone(resident.getTimezone()));
		sdf.setTimeZone(cal.getTimeZone());
		
		
		out.add(CivColor.Green+CivSettings.localize.localizedString("cmd_civ_timeServer")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		
		cal.setTime(CivGlobal.getNextUpkeepDate());
		out.add(CivColor.Green+CivSettings.localize.localizedString("cmd_civ_timeUpkeep")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		
		cal.setTime(CivGlobal.getNextHourlyTickDate());
		out.add(CivColor.Green+CivSettings.localize.localizedString("cmd_civ_timeHourly")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		
		cal.setTime(CivGlobal.getNextRepoTime());
		out.add(CivColor.Green+CivSettings.localize.localizedString("cmd_civ_timeRepo")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		
		if (War.isWarTime()) {
			out.add(CivColor.Yellow+CivSettings.localize.localizedString("cmd_civ_timeWarNow"));
			cal.setTime(War.getStart());
			out.add(CivColor.Yellow+CivSettings.localize.localizedString("cmd_civ_timeWarStarted")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
			
			cal.setTime(War.getEnd());
			out.add(CivColor.Yellow+CivSettings.localize.localizedString("cmd_civ_timeWarEnds")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		} else {
			cal.setTime(War.getNextWarTime());
			out.add(CivColor.Green+CivSettings.localize.localizedString("cmd_civ_timeWarNext")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		}
		
		Player player = null;
		try {
			player = getPlayer();
		} catch (CivException e) {
		}

		if (player == null || player.hasPermission(CivSettings.MINI_ADMIN) || player.isOp()) {
			cal.setTime(CivGlobal.getTodaysSpawnRegenDate());
			out.add(CivColor.LightPurple+CivSettings.localize.localizedString("cmd_civ_timeSpawnRegen")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
			
			cal.setTime(CivGlobal.getNextRandomEventTime());
			out.add(CivColor.LightPurple+CivSettings.localize.localizedString("cmd_civ_timeRandomEvent")+" "+CivColor.LightGreen+sdf.format(cal.getTime()));
		}
		
		CivMessage.send(sender, out);
	}
	
	public void gov_cmd() {
		CivGovCommand cmd = new CivGovCommand();	
		cmd.onCommand(sender, null, "gov", this.stripArgs(args, 1));	
	}
	
	public void research_cmd() {
		CivResearchCommand cmd = new CivResearchCommand();	
		cmd.onCommand(sender, null, "research", this.stripArgs(args, 1));	
	}
	
	public void list_cmd() throws CivException {
		if (args.length < 2) {	
			String out = "";
			CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_listHeading"));
			for (Civilization civ : CivGlobal.getCivs()) {
				out += civ.getName()+", ";
			}
			
			CivMessage.send(sender, out);
			return;
		}
		
		Civilization civ = getNamedCiv(1);
		
		String out = "";
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("var_cmd_civ_listtowns",args[1]));
		
		for (Town t : civ.getTowns()) {
			out += t.getName()+", ";
		}
		
		CivMessage.send(sender, out);
	}
	
	public void show_cmd() throws CivException {
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_showPrompt"));
		}
		
		Civilization civ = getNamedCiv(1);
		if (sender instanceof Player) {
			CivInfoCommand.show(sender, getResident(), civ);
		} else {
			CivInfoCommand.show(sender, null, civ);
		}
	}
	
	public void deposit_cmd() throws CivException {
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_despositPrompt"));
		}
		
		Resident resident = getResident();
		Civilization civ = getSenderCiv();
		
		try {
			Double amount = Double.valueOf(args[1]);
			if (amount < 1) {
				throw new CivException(amount+" "+CivSettings.localize.localizedString("cmd_enterNumerError2"));
			}
			amount = Math.floor(amount);
			
			civ.depositFromResident(resident, Double.valueOf(args[1]));			
			
		} catch (NumberFormatException e) {
			throw new CivException(args[1]+" "+CivSettings.localize.localizedString("cmd_enterNumerError"));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CivException(CivSettings.localize.localizedString("internalDatabaseException"));
		}
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("Deposited")+args[1]+" "+CivSettings.CURRENCY_NAME);
	}

	public void withdraw_cmd() throws CivException {
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_withdrawPrompt"));
		}
		
		Civilization civ = getSenderCiv();
		Resident resident = getResident();
		
		if (!civ.getLeaderGroup().hasMember(resident)) {
			throw new CivException(CivSettings.localize.localizedString("cmd_NeedHigherCivRank2"));
		}
		
		try {
			Double amount = Double.valueOf(args[1]);
			if (amount < 1) {
				throw new CivException(amount+" "+CivSettings.localize.localizedString("cmd_enterNumerError2"));
			}
			amount = Math.floor(amount);
			
			if(!civ.getTreasury().payTo(resident.getTreasury(), Double.valueOf(args[1]))) {
				throw new CivException(CivSettings.localize.localizedString("cmd_civ_withdrawTooPoor"));
			}
		} catch (NumberFormatException e) {
			throw new CivException(args[1]+" "+CivSettings.localize.localizedString("cmd_enterNumerError"));
		}
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_civ_withdrawSuccess",args[1],CivSettings.CURRENCY_NAME));
	}
	
	public void townlist_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		CivMessage.sendHeading(sender, civ.getName()+" "+CivSettings.localize.localizedString("cmd_civ_townListHeading"));
		String out = "";
		for (Town town : civ.getTowns()) {
			out += town.getName()+",";
		}
		CivMessage.send(sender, out);	
	}
	
	public void info_cmd() throws CivException {
		CivInfoCommand cmd = new CivInfoCommand();	
		cmd.onCommand(sender, null, "info", this.stripArgs(args, 1));		
	}
	
	public void vote_cmd() throws CivException {
	
		if (args.length < 2) {
			CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_civ_voteHeading"));
			return;
		}

		if (sender instanceof Player) {
			Player player = (Player)sender;
			Resident resident = CivGlobal.getResident(player);
			
			if (!resident.hasTown()) {
				CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_civ_voteNotInTown"));
				return;
			}
			
			Civilization civ = CivGlobal.getCiv(args[1]);
			if (civ == null) {
				CivMessage.sendError(sender, CivSettings.localize.localizedString("var_cmd_civ_voteInvalidCiv",args[1]));
				return;
			}
			
			if (!EndConditionDiplomacy.canPeopleVote()) {
				CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_civ_voteNoCouncil"));
				return;
			}
			
			EndConditionDiplomacy.addVote(civ, resident);
			return;
		} else {
			return;
		}
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		this.showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		
	}
	
}
