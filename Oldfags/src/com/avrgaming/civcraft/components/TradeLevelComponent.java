package com.avrgaming.civcraft.components;

import gpl.AttributeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigTradeShipLevel;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.lorestorage.LoreMaterial;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.sessiondb.SessionEntry;
import com.avrgaming.civcraft.structure.Buildable;
import com.avrgaming.civcraft.structure.TradeShip;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.util.ItemManager;
import com.avrgaming.civcraft.util.MultiInventory;

public class TradeLevelComponent extends Component {

	private static final double PACK_CHANCE = CivSettings.getDoubleStructure("tradeship.pack_chance"); //0.5%
	private static final double MEDIUMPACK_CHANCE = CivSettings.getDoubleStructure("tradeship.mediumpack_chance"); //0.1%
	private static final double BIGPACK_CHANCE = CivSettings.getDoubleStructure("tradeship.bigpack_chance"); //0.05%
	private static final double HUGEPACK_CHANCE = CivSettings.getDoubleStructure("tradeship.hugepack_chance"); //0.01%
	

	/* Current level we're operating at. */
	private int level;

	/* Current count we have in this level. */
	private int upgradeTrade;

	/* Last result. */
	private TradeShipResults lastTrade;
	private Result lastResult;

	/*
	 * Consumption mod rate, can be used to increase or decrease consumption
	 * rates.
	 */
	private double consumeRate;

	private double cultureEarned;

	private double moneyEarned;

	/* Buildable this component is attached to. */
	// private Buildable buildable;

	/*
	 * The first key is the level id, followed by a hashmap containing integer,
	 * amount entries for each item consumed for that level. For each item in
	 * the hashmap, we must have ALL of the items in the inventory.
	 */
	private HashMap<Integer, Integer> consumptions = new HashMap<Integer, Integer>();

	/*
	 * Contains a hashmap of levels and counts configured for this component.
	 */
	private HashMap<Integer, Integer> levelCounts = new HashMap<Integer, Integer>();
	private HashMap<Integer, Double> culture = new HashMap<Integer, Double>();

	/* Inventory we're trying to pull from. */
	private MultiInventory source;

	// consumeComp.createComponent(this);

	@Override
	public void createComponent(Buildable buildable, boolean async) {
		super.createComponent(buildable, async);

		// XXX make both mine/cottage/longhouse levels similar in the yml so
		// they can be loaded
		// without this check.
		if (buildable instanceof TradeShip) {
			for (ConfigTradeShipLevel lvl : CivSettings.tradeShipLevels
					.values()) {
				this.addCulture(lvl.level, lvl.culture);
				this.addLevel(lvl.level, lvl.upgradeTrade);
				this.setConsumes(lvl.level, lvl.maxTrade);
			}
		}

	}

	/* Possible Results. */
	public enum Result {
		STAGNATE, GROW, LEVELUP, MAXED, UNKNOWN
	}

	public TradeLevelComponent() {
		this.level = 1;
		this.upgradeTrade = 0;
		this.consumeRate = 1.0;
		this.lastResult = Result.UNKNOWN;
	}

	private String getKey() {
		return getBuildable().getDisplayName() + ":" + getBuildable().getId()
				+ ":" + "levelcount";
	}

	private String getValue() {
		return this.level + ":" + this.upgradeTrade;
	}

	@Override
	public void onLoad() {
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(
				getKey());

		if (entries.size() == 0) {
			getBuildable().sessionAdd(getKey(), getValue());
			return;
		}

		String[] split = entries.get(0).value.split(":");
		this.level = Integer.valueOf(split[0]);
		this.upgradeTrade = Integer.valueOf(split[1]);
	}

	@Override
	public void onSave() {

		class AsyncTask implements Runnable {
			@Override
			public void run() {
				ArrayList<SessionEntry> entries = CivGlobal.getSessionDB()
						.lookup(getKey());

				if (entries.size() == 0) {
					getBuildable().sessionAdd(getKey(), getValue());
					return;
				}

				CivGlobal.getSessionDB().update(entries.get(0).request_id,
						getKey(), getValue());
			}
		}

		if (getBuildable().getId() != 0) {
			TaskMaster.asyncTask(new AsyncTask(), 0);
		}
	}

	public void onDelete() {
		class AsyncTask implements Runnable {
			@Override
			public void run() {
				CivGlobal.getSessionDB().delete_all(getKey());
			}
		}

		if (getBuildable().getId() != 0) {
			TaskMaster.asyncTask(new AsyncTask(), 0);
		}
	}

	public void addLevel(int level, int count) {
		levelCounts.put(level, count);
	}

	public void addCulture(int level, Double cultureCount) {
		culture.put(level, cultureCount);
	}

	public void setConsumes(int level, int maxConsume) {
		this.consumptions.put(level, maxConsume);
	}

	public void setSource(MultiInventory source) {
		this.source = source;
	}

	public int getConsumedAmount(int amount) {
		return (int) Math.max(1, amount * this.consumeRate);
	}

	private int hasCountToConsume(int thisLevelConsumptions) {

		thisLevelConsumptions+= getConsumedAmount(consumptions.get(this.level));
		int stacksToConsume = 0;

		for (ItemStack stack : source.getContents()) {
			if (stack == null) {
				continue;
			}

			AttributeUtil attrs = new AttributeUtil(stack);
			String tradeable = attrs.getCivCraftProperty("tradeable");
			if (tradeable != null) {
				if (tradeable.equalsIgnoreCase("true")) {
					if (stacksToConsume < thisLevelConsumptions) {
						stacksToConsume++;
					} else {
						break;
					}
				}
			}
		}
		return stacksToConsume;
	}
	
	private void processItemsFromStack(ItemStack stack) {
		String itemID = LoreMaterial.getMaterial(stack).getId();
		Integer countInStack = stack.getAmount();
		Random rand = new Random();
		int MaxRand = 10000;
		ArrayList<ItemStack> newItems = new ArrayList<ItemStack>();
		for (int i = 0; i < countInStack; i++) {
			int rand1 = rand.nextInt(MaxRand);

			if (rand1 < (int)(HUGEPACK_CHANCE*MaxRand)) {
				if (itemID.contains("_egg")) {
					if (itemID.contains("_egg_4")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4"),2));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_boots")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_boots")));
						}
						break;
					} else if (itemID.contains("_egg_3")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3"),2));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_boots")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_boots")));
						}
						break;
					} else if (itemID.contains("_egg_2")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2"),2));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_boots")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_boots")));
						}
						break;
					} else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1"),2));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_boots")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_leggings")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_chestplate")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_helmet")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_boots")));
						}
						break;
					}
				} else {
					int emeraldRand = (rand.nextInt(4))+1;
					if (emeraldRand >= 3) {
						newItems.add(ItemManager.createItemStack(CivData.GUNPOWDER, 3));
					} else {
						newItems.add(ItemManager.createItemStack(CivData.EMERALD, 1));
					}
				}
			} else if (rand1 < (int)(BIGPACK_CHANCE*MaxRand)) {
				if (itemID.contains("_egg")) {
					if (itemID.contains("_egg_4")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4")));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_sword")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_marksmen_bow")));
						}
						break;
					} else if (itemID.contains("_egg_3")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_sword")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_longbow")));
						}
						break;
					} else if (itemID.contains("_egg_2")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_sword")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_recurve_bow")));
						}
						break;
					} else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
						if (itemID.contains("creeper") || itemID.contains("skeleton") || itemID.contains("spider") || itemID.equals("zombie") || itemID.contains("slime") || itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_sword")));
						}  else if (itemID.contains("pig") || itemID.contains("cow") || itemID.contains("chicken") || itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hunting_bow")));
						}
						break;
					}
				} else {
					int diamondRand = (rand.nextInt(4))+1;
					if (diamondRand >= 3) {
						newItems.add(ItemManager.createItemStack(CivData.GUNPOWDER, 2));
					} else {
						newItems.add(ItemManager.createItemStack(CivData.DIAMOND, 1));
					}

					newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_3"), (rand.nextInt(3))+1));
					newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_3"), (rand.nextInt(3))+1));
				}
			} else if (rand1 < (int)(MEDIUMPACK_CHANCE*MaxRand)) {
				if (itemID.contains("_egg")) {
					if (itemID.contains("_egg_4")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4")));
						break;
					} else if (itemID.contains("_egg_3")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
						break;
					} else if (itemID.contains("_egg_2")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
						break;
					} else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
						break;
					}
				} else {
					int goldRand = (rand.nextInt(4))+1;
					if (goldRand >= 3) {
						newItems.add(ItemManager.createItemStack(CivData.GUNPOWDER, 2));
					} else {
						newItems.add(ItemManager.createItemStack(CivData.GOLD_INGOT, 1));
					}
					
					newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_2"), (rand.nextInt(3))+1));
					newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_2"), (rand.nextInt(3))+1));
				}
			}  else if (rand1 < (int)(PACK_CHANCE*MaxRand)) {
				if (itemID.contains("_egg")) {
					if (itemID.contains("_egg_4")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_4")));
						break;
					} else if (itemID.contains("_egg_3")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_3")));
						break;
					} else if (itemID.contains("_egg_2")) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_2")));
						break;
					} else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_1")));
						break;
					}
				} else {
					int ironRand = (rand.nextInt(4))+1;
					if (ironRand >= 3) {
						newItems.add(ItemManager.createItemStack(CivData.GUNPOWDER, 1));
					} else {
						newItems.add(ItemManager.createItemStack(CivData.IRON_INGOT, 1));
					}
				}
			}
		}
		for (ItemStack cargo : newItems)
		{
			lastTrade.addReturnCargo(cargo);
		}

	}

	private int consumeFromInventory(int stacksToConsume) {
		int countConsumed = 0;
		if (stacksToConsume <= 0) {
			return countConsumed;
		}
		double monetaryValue = 0.0;

		for (ItemStack stack : source.getContents()) {
			if (stack == null) {
				continue;
			}
			if (stacksToConsume <= 0) {
				break;
			}

			AttributeUtil attrs = new AttributeUtil(stack);
			String tradeable = attrs.getCivCraftProperty("tradeable");
			if (tradeable != null) {
				if (tradeable.equalsIgnoreCase("true")) {
					if (stacksToConsume > 0) {
						Integer countInStack = stack.getAmount();
						String tradeValue = attrs.getCivCraftProperty("tradeValue");
						if (tradeValue != null) {
							double valueForStack = Double.parseDouble(tradeValue);
							double moneyForStack = countInStack * valueForStack;
							monetaryValue += moneyForStack;
						} else {
							CivLog.debug("tradeValue null for item");
						}
	
						processItemsFromStack(stack);
						countConsumed += countInStack;
						stacksToConsume--;
						/* Consume what we can */
						try {
							source.removeItem(stack);
						} catch (CivException e) {
							e.printStackTrace();
							return 0;
						}
					}
				}
			}

		}
		moneyEarned = monetaryValue;

		return countConsumed;

	}

	public TradeShipResults processConsumption(Integer updgradeLevel) {
		lastTrade = new TradeShipResults();
		moneyEarned = 0;
		cultureEarned = 0.0;
		int countConsumed = 0;

		Integer currentCountMax = levelCounts.get(this.level);

//		if (currentCountMax == null) {
//			CivLog.error("Couldn't get current level count, level was:"
//					+ this.level);
//			lastResult = Result.UNKNOWN;
//			lastTrade.setResult(lastResult);
//			return lastTrade;
//		}
		int stacksToConsume = hasCountToConsume((updgradeLevel*2));
		if (stacksToConsume >= 1) {
			countConsumed = consumeFromInventory(stacksToConsume);

			if ((this.upgradeTrade + countConsumed) >= currentCountMax) {
				// Level up?
				Integer nextCountMax = levelCounts.get(this.level + 1);
				if (nextCountMax == null) {
					lastResult = Result.MAXED;
					lastTrade.setResult(lastResult);
				} else {

					this.upgradeTrade = this.upgradeTrade + countConsumed
							- currentCountMax;
					this.level++;
					lastResult = Result.LEVELUP;
					lastTrade.setResult(lastResult);
				}
			} else if (countConsumed >= 1) {
				// Grow
				this.upgradeTrade += countConsumed;
				lastResult = Result.GROW;
				lastTrade.setResult(lastResult);
			} else {

				lastResult = Result.STAGNATE;
				lastTrade.setResult(lastResult);
			}
		} else {
			lastResult = Result.STAGNATE;
			lastTrade.setResult(lastResult);
			// return lastTrade;
		}
		Double currentCultureRate = culture.get(this.level);
		Double cultureUpgradeModifier = (updgradeLevel/5.0);

		cultureEarned = (currentCultureRate+cultureUpgradeModifier)*countConsumed;

		lastTrade.setMoney(moneyEarned);
		lastTrade.setConsumed(countConsumed);
		lastTrade.setCulture((int) Math.round(cultureEarned));
		return lastTrade;

	}

	public String getCountString() {
		String out = "(" + this.upgradeTrade + "/";
		Integer currentCountMax = levelCounts.get(this.level);
		if (currentCountMax != null) {
			out += currentCountMax + ")";
		} else {
			out += "?)";
		}

		return out;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getCount() {
		return upgradeTrade;
	}

	public void setCount(int count) {
		this.upgradeTrade = count;
	}

	public double getConsumeRate() {
		return consumeRate;
	}

	public void setConsumeRate(double consumeRate) {
		this.consumeRate = consumeRate;
	}

	public Result getLastResult() {
		return this.lastResult;
	}

}
