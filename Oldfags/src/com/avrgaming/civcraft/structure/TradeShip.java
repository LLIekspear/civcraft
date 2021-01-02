
package com.avrgaming.civcraft.structure;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.components.AttributeBiomeRadiusPerLevel;
import com.avrgaming.civcraft.components.TradeLevelComponent;
import com.avrgaming.civcraft.components.TradeLevelComponent.Result;
import com.avrgaming.civcraft.components.TradeShipResults;
import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMineLevel;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.exception.CivTaskAbortException;
import com.avrgaming.civcraft.exception.InvalidConfiguration;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Buff;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.template.Template;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.util.BlockCoord;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.civcraft.util.ItemManager;
import com.avrgaming.civcraft.util.MultiInventory;
import com.avrgaming.civcraft.util.SimpleBlock;
import com.avrgaming.civcraft.util.TimeTools;

public class TradeShip extends WaterStructure {
	
	private int upgradeLevel = 1;
	private int tickLevel = 1;

	public HashSet<BlockCoord> goodsDepositPoints = new HashSet<BlockCoord>();
	public HashSet<BlockCoord> goodsWithdrawPoints = new HashSet<BlockCoord>();
	
	private TradeLevelComponent consumeComp = null;
	
	protected TradeShip(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		setUpgradeLvl(town.saved_tradeship_upgrade_levels);
	}

	public TradeShip(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
		
	@Override
	public void loadSettings() {
		super.loadSettings();
	}
	
	public String getkey() {
		return getTown().getName()+"_"+this.getConfigId()+"_"+this.getCorner().toString(); 
	}
		
	@Override
	public String getDynmapDescription() {
		return null;
	}
	
	@Override
	public String getMarkerIconName() {
		return "anchor";
	}
	
	public TradeLevelComponent getConsumeComponent() {
		if (consumeComp == null) {
			consumeComp = (TradeLevelComponent) this.getComponent(TradeLevelComponent.class.getSimpleName());
		}
		return consumeComp;
	}
	
	@Override 
	public void updateSignText() {
		reprocessCommandSigns();
	}
	
	public void reprocessCommandSigns() {
		/* Load in the template. */
		//Template tpl = new Template();
		Template tpl;
		try {
			//tpl.load_template(this.getSavedTemplatePath());
			tpl = Template.getTemplate(this.getSavedTemplatePath(), null);
		} catch (IOException | CivException e) {
			e.printStackTrace();
			return;
		}
		class SyncTask implements Runnable {
			Template template;
			BlockCoord structCorner;
			
			public SyncTask(Template template, BlockCoord structCorner) {
				this.template = template;
				this.structCorner = structCorner;
			}
			
			@Override
			public void run() {
				
				processCommandSigns(template, structCorner);
			}
		}
		
		TaskMaster.syncTask(new SyncTask(tpl, corner), TimeTools.toTicks(1));

	}
	
	private void processCommandSigns(Template tpl, BlockCoord corner) {
		for (BlockCoord relativeCoord : tpl.commandBlockRelativeLocations) {
			SimpleBlock sb = tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()];
			BlockCoord absCoord = new BlockCoord(corner.getBlock().getRelative(relativeCoord.getX(), relativeCoord.getY(), relativeCoord.getZ()));

			switch (sb.command) {
			case "/incoming":{
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getUpgradeLvl() >= ID+1) {
					this.goodsWithdrawPoints.add(absCoord);
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.CHEST));
					byte data3 = CivData.convertSignDataToChestData((byte)sb.getData());
					ItemManager.setData(absCoord.getBlock(), data3);
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.AIR));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
				}
				this.addStructureBlock(absCoord, false);
				break;}
			case "/inSign":{
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getUpgradeLvl() >= ID+1) {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("tradeship_sign_input_line0"));
					sign.setLine(1, ""+(ID+1));
					sign.setLine(2, "");
					sign.setLine(3, "");
					sign.update();
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("tradeship_sign_input_line0"));
					sign.setLine(1, CivSettings.localize.localizedString("tradeship_sign_input_notupgraded_line1"));
					sign.setLine(2, (CivSettings.localize.localizedString("tradeship_sign_input_notupgraded_line2")));
					sign.setLine(3, CivSettings.localize.localizedString("tradeship_sign_input_notupgraded_line3"));
					sign.update();
				}
				this.addStructureBlock(absCoord, false);
				break;}
			case "/outgoing":{
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				
				if (this.getLevel() >= (ID*2)+1) {
					this.goodsDepositPoints.add(absCoord);
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.CHEST));
					byte data3 = CivData.convertSignDataToChestData((byte)sb.getData());
					ItemManager.setData(absCoord.getBlock(), data3);
					this.addStructureBlock(absCoord, false);
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.AIR));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
				}
				break;}
			case "/outSign":{
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getLevel() >= (ID*2)+1) {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("tradeship_sign_output_line0"));
					sign.setLine(1, ""+(ID+1));
					sign.setLine(2, "");
					sign.setLine(3, "");
					sign.update();
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("tradeship_sign_output_line0"));
					sign.setLine(1, CivSettings.localize.localizedString("tradeship_sign_output_notupgraded_line1"));
					sign.setLine(2, (CivSettings.localize.localizedString("var_tradeship_sign_output_notupgraded_line2",((ID*2)+1))));
					sign.setLine(3, CivSettings.localize.localizedString("tradeship_sign_output_notupgraded_line3"));
					sign.update();
				}
				this.addStructureBlock(absCoord, false);
				break;}
			case "/in":{
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (ID == 0) {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("tradeship_sign_input_line0"));
					sign.setLine(1, "1");
					sign.setLine(2, "2");
					sign.setLine(3, "");
					sign.update();
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("tradeship_sign_input_line0"));
					sign.setLine(1, "3");
					sign.setLine(2, "4");
					sign.setLine(3, "");
					sign.update();
				}
				this.addStructureBlock(absCoord, false);
				break;}
			default:{
				/* Unrecognized command... treat as a literal sign. */
				ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
				ItemManager.setData(absCoord.getBlock(), sb.getData());
				
				Sign sign = (Sign)absCoord.getBlock().getState();
				sign.setLine(0, sb.message[0]);
				sign.setLine(1, sb.message[1]);
				sign.setLine(2, sb.message[2]);
				sign.setLine(3, sb.message[3]);
				sign.update();

				this.addStructureBlock(absCoord, false);
				break;}
			}
		}
	}
		
	
	public TradeShipResults consume(CivAsyncTask task) throws InterruptedException {
		TradeShipResults tradeResult;
		//Look for the TradeShip chests.
		if (this.goodsDepositPoints.size() == 0 || this.goodsWithdrawPoints.size() == 0)
		{
			tradeResult = new TradeShipResults();
			tradeResult.setResult(Result.STAGNATE);
			return tradeResult;
		}
		MultiInventory mInv = new MultiInventory();
		
		for (BlockCoord bcoord : this.goodsDepositPoints) {
			task.syncLoadChunk(bcoord.getWorldname(), bcoord.getX(), bcoord.getZ());
			Inventory tmp;
			try {
				tmp = task.getChestInventory(bcoord.getWorldname(), bcoord.getX(), bcoord.getY(), bcoord.getZ(), true);
			} catch (CivTaskAbortException e) {
				tradeResult = new TradeShipResults();
				tradeResult.setResult(Result.STAGNATE);
				return tradeResult;
			}
			mInv.addInventory(tmp);
		}
		
		if (mInv.getInventoryCount() == 0) {
			tradeResult = new TradeShipResults();
			tradeResult.setResult(Result.STAGNATE);
			return tradeResult;
		}
		getConsumeComponent().setSource(mInv);
		getConsumeComponent().setConsumeRate(1.0);
		tradeResult = getConsumeComponent().processConsumption(this.getUpgradeLvl()-1);
		getConsumeComponent().onSave();		
		return tradeResult;
	}
	
	public void process_trade_ship(CivAsyncTask task) throws InterruptedException, InvalidConfiguration {	
		TradeShipResults tradeResult = this.consume(task);
		
		Result result = tradeResult.getResult();
		switch (result) {
		case STAGNATE:
			CivMessage.sendTownRightMessage(getTown(), CivColor.Rose+CivSettings.localize.localizedString("var_tradeship_stagnated",getConsumeComponent().getLevel(),CivColor.LightGreen+getConsumeComponent().getCountString()));
			break;
		case GROW:
			CivMessage.sendTownRightMessage(getTown(), CivColor.LightGreen+CivSettings.localize.localizedString("var_tradeship_productionGrew",getConsumeComponent().getLevel(),getConsumeComponent().getCountString()));
			break;
		case LEVELUP:
			CivMessage.sendTownRightMessage(getTown(), CivColor.LightGreen+CivSettings.localize.localizedString("var_tradeship_lvlUp",getConsumeComponent().getLevel()));
			this.reprocessCommandSigns();
			break;
		case MAXED:
			CivMessage.sendTownRightMessage(getTown(), CivColor.LightGreen+CivSettings.localize.localizedString("var_tradeship_maxed",getConsumeComponent().getLevel(),CivColor.LightGreen+getConsumeComponent().getCountString()));
			break;
		default:
			break;
		}
		if (tradeResult.getCulture() >= 1) {
			int total_culture = (int)Math.round(tradeResult.getCulture());

			this.getTown().addAccumulatedCulture(total_culture);
			this.getTown().save();
		}
		if (tradeResult.getMoney() >= 1) {
			double total_coins = tradeResult.getMoney();
			if (this.getTown().getBuffManager().hasBuff("buff_ingermanland_trade_ship_income")) {
				total_coins *= this.getTown().getBuffManager().getEffectiveDouble("buff_ingermanland_trade_ship_income");
			}
			
			if (this.getTown().getBuffManager().hasBuff("buff_great_lighthouse_trade_ship_income")) {
				total_coins *= this.getTown().getBuffManager().getEffectiveDouble("buff_great_lighthouse_trade_ship_income");
			}
			if (this.getTown().getStructureTypeCount("s_lighthouse") >=1)
			{
				total_coins *= CivSettings.getDouble(CivSettings.townConfig, "town.lighthouse_trade_ship_boost");
			}
			
			double taxesPaid = total_coins*this.getTown().getDepositCiv().getIncomeTaxRate();

			if (total_coins >= 1) {
				CivMessage.sendTownRightMessage(getTown(), CivColor.LightGreen+CivSettings.localize.localizedString("var_tradeship_success",Math.round(total_coins),CivSettings.CURRENCY_NAME,tradeResult.getCulture(),tradeResult.getConsumed()));
			}
			if (taxesPaid > 0) {
				CivMessage.sendTownRightMessage(this.getTown(), CivColor.Yellow+CivSettings.localize.localizedString("var_tradeship_taxesPaid",Math.round(taxesPaid),CivSettings.CURRENCY_NAME));
			}

			this.getTown().getTreasury().deposit(total_coins - taxesPaid);
			this.getTown().getDepositCiv().taxPayment(this.getTown(), taxesPaid);
		}
		
		if (tradeResult.getReturnCargo().size() >= 1) {
			MultiInventory multiInv = new MultiInventory();
			
			for (BlockCoord bcoord : this.goodsWithdrawPoints) {
				task.syncLoadChunk(bcoord.getWorldname(), bcoord.getX(), bcoord.getZ());
				Inventory tmp;
				try {
					tmp = task.getChestInventory(bcoord.getWorldname(), bcoord.getX(), bcoord.getY(), bcoord.getZ(), true);
					multiInv.addInventory(tmp);
				} catch (CivTaskAbortException e) {

					e.printStackTrace();
				}
			}
			
			for (ItemStack item :tradeResult.getReturnCargo()) {
				multiInv.addItem(item);
			}
			CivMessage.sendTownRightMessage(getTown(), CivColor.LightGreen+CivSettings.localize.localizedString("tradeship_successSpecail"));
		}
	}
	
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		this.upgradeLevel = getTown().saved_tradeship_upgrade_levels;
		this.reprocessCommandSigns();
	}

	public int getUpgradeLvl() {
		return upgradeLevel;
	}

	public void setUpgradeLvl(int level) {
		this.upgradeLevel = level;

		if (this.isComplete()) {
			this.reprocessCommandSigns();
		}
	}

	public int getLevel() {
		try {
			return this.getConsumeComponent().getLevel();
		} catch (Exception e) {
			return tickLevel;
		}
	}
	
	public double getHammersPerTile() {
		AttributeBiomeRadiusPerLevel attrBiome = (AttributeBiomeRadiusPerLevel)this.getComponent("AttributeBiomeBase");
		double base = attrBiome.getBaseValue();
	
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.ADVANCED_TOOLING);
		return (rate*base);
	}

	public int getCount() {
		return this.getConsumeComponent().getCount();
	}

	public int getMaxCount() {
		int level = getLevel();
		
		ConfigMineLevel lvl = CivSettings.mineLevels.get(level);
		return lvl.count;	
	}

	public Result getLastResult() {
		return this.getConsumeComponent().getLastResult();
	}
	
	@Override
	public void delete() throws SQLException {
		super.delete();
		if (getConsumeComponent() != null) {
			getConsumeComponent().onDelete();
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		getConsumeComponent().setLevel(1);
		getConsumeComponent().setCount(0);
		getConsumeComponent().onSave();
	}

}
