package com.avrgaming.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.exception.CivTaskAbortException;
import com.avrgaming.civcraft.lorestorage.LoreMaterial;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.object.StructureChest;
import com.avrgaming.civcraft.structure.Structure;
import com.avrgaming.civcraft.structure.MobGrinder;
import com.avrgaming.civcraft.structure.MobGrinder.Crystal;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.avrgaming.civcraft.util.MultiInventory;

public class MobGrinderAsyncTask extends CivAsyncTask {

	MobGrinder mobGrinder;
	
	public static HashSet<String> debugTowns = new HashSet<String>();
	public static void debug(MobGrinder mobGrinder, String msg) {
		if (debugTowns.contains(mobGrinder.getTown().getName())) {
			CivLog.warning("GrinderDebug:"+mobGrinder.getTown().getName()+":"+msg);
		}
	}	
	
	public MobGrinderAsyncTask(Structure mobGrinder) {
		this.mobGrinder = (MobGrinder)mobGrinder;
	}
	
	public void processMobGrinderUpdate() {
		if (!mobGrinder.isActive()) {
			debug(mobGrinder, "mobGrinder inactive...");
			return;
		}
		
		debug(mobGrinder, "Processing mobGrinder...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = mobGrinder.getAllChestsById(1);
		ArrayList<StructureChest> destinations = mobGrinder.getAllChestsById(2);
		
		if (sources.size() != 2 || destinations.size() != 2) {
			CivLog.error("Bad chests for Mob Grinder in town:"+mobGrinder.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();

		try {
			for (StructureChest src : sources) {
				//this.syncLoadChunk(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getZ());				
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Mob Grinder:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					mobGrinder.skippedCounter++;
					return;
				}
				source_inv.addInventory(tmp);
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				//this.syncLoadChunk(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getZ());
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Mob Grinder:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					mobGrinder.skippedCounter++;
					return;
				}
				dest_inv.addInventory(tmp);
				
				for (ItemStack stack : tmp.getContents()) {
					if (stack == null) {
						full = false;
						break;
					}
				}
			}
			
			if (full) {
				/* Mob Grinder destination chest is full, stop processing. */
				return;
			}
			
		} catch (InterruptedException e) {
			return;
		}
		
		debug(mobGrinder, "Processing mobGrinder:"+mobGrinder.skippedCounter+1);
		ItemStack[] contents = source_inv.getContents();
		for (int i = 0; i < mobGrinder.skippedCounter+1; i++) {
		
			for(ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				if (!LoreMaterial.isCustom(stack)) {
					continue;
				}
				String itemID = LoreMaterial.getMaterial(stack).getId();
				try {
					ItemStack newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get(itemID));
					this.updateInventory(Action.REMOVE, source_inv, newItem);
				} catch (InterruptedException e) {
					return;
				}

				Random rand = new Random();
				int rand1 = rand.nextInt(10000);
				ArrayList<ItemStack> newItems = new ArrayList<ItemStack>();
				if (itemID.contains("_egg_4")) {				
					if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.HUGEPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4"),2));
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.BIGPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4")));
						if (itemID.contains("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_chestplate")));
						} else if (itemID.contains("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_leggings")));
						} else if (itemID.contains("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_helmet")));
						} else if (itemID.equals("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_boots")));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_sword")));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_sword")));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_leggings")));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_chestplate")));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_helmet")));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_composite_leather_boots")));
						}
						
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.PACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4"),3));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4"),3));
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T4))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4"),2));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T3))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4"),2));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T2))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_4"),5));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_4"),5));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T1))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_4"),3));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_4"),3));
	
					}  else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_4"), 2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_4"), 2));
					}
				} else if (itemID.contains("_egg_3")) {				
					if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.HUGEPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3"),2));
						if (itemID.contains("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_creeper_egg_4"),2));
						} else if (itemID.contains("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_skeleton_egg_4"),2));
						} else if (itemID.contains("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_spider_egg_4"),2));
						} else if (itemID.equals("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_zombie_egg_4"),2));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_slime_egg_4"),2));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_enderman_egg_4"),2));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pig_egg_4"),2));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_cow_egg_4"),2));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_chicken_egg_4"),2));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_sheep_egg_4"),2 ));
						}
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.BIGPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
						if (itemID.contains("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_creeper_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_chestplate")));
						} else if (itemID.contains("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_skeleton_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_leggings")));
						} else if (itemID.contains("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_spider_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_helmet")));
						} else if (itemID.equals("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_zombie_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_boots")));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_slime_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_sword")));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_enderman_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_carbide_steel_sword")));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pig_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_leggings")));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_cow_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_chestplate")));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_chicken_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_helmet")));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_sheep_egg_4")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_hardened_leather_boots")));
						}
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.PACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_4")));
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T4))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3")));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T3))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T2))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_4")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_3"), 2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_3"), 2));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T1))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_3"),3));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_3"), 3));
	
					}  else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_3")));
					}
					
				} else if (itemID.contains("_egg_2")) {
						
					if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.HUGEPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2"),2));
						if (itemID.equals("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_creeper_egg_3"), 2));
						} else if (itemID.equals("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_skeleton_egg_3"), 2));
						} else if (itemID.equals("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_spider_egg_3"), 2));
						} else if (itemID.equals("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_zombie_egg_3"), 2));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_slime_egg_3"), 2));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_enderman_egg_3"), 2));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pig_egg_3"), 2));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_cow_egg_3"), 2));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_chicken_egg_3"), 2));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_sheep_egg_3"), 2));
						}
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.BIGPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
						if (itemID.contains("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_creeper_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_chestplate")));
						} else if (itemID.contains("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_skeleton_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_leggings")));
						} else if (itemID.contains("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_spider_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_helmet")));
						} else if (itemID.contains("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_zombie_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_boots")));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_slime_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_sword")));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_enderman_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_steel_sword")));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pig_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_leggings")));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_cow_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_chestplate")));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_chicken_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_helmet")));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_sheep_egg_3")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_refined_leather_boots")));
						}
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.PACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
						
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T4))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T3))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T2))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_3")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_2"), 2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_2"), 2));
	
					} else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T1))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_2"), 3));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_2"), 3));
	
					}  else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_2")));
					}
					
				} else if (itemID.contains("_egg")) {
					if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.HUGEPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1"),2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1"),2));

						if (itemID.contains("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_creeper_egg_2"), 2));
						} else if (itemID.contains("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_skeleton_egg_2"), 2));
						} else if (itemID.contains("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_spider_egg_2"), 2));
						} else if (itemID.contains("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_zombie_egg_2"), 2));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_slime_egg_2"), 2));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_enderman_egg_2"), 2));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pig_egg_2"), 2));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_cow_egg_2"), 2));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_chicken_egg_2"), 2));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_sheep_egg_2"), 2));
						}
					}
					else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.BIGPACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
						if (itemID.contains("creeper")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_creeper_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_chestplate")));
						} else if (itemID.contains("skeleton")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_skeleton_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_leggings")));
						} else if (itemID.contains("spider")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_spider_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_helmet")));
						} else if (itemID.contains("zombie")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_zombie_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_boots")));
						} else if (itemID.contains("slime")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_slime_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_sword")));
						} else if (itemID.contains("enderman")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_enderman_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_iron_sword")));
						} else if (itemID.contains("pig")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pig_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_leggings")));
						} else if (itemID.contains("cow")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_cow_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_chestplate")));
						} else if (itemID.contains("chicken")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_chicken_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_helmet")));
						} else if (itemID.contains("sheep")) {
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_sheep_egg_2")));
							newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_leather_boots")));
						}
					} 
					else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.PACK))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_2")));
					}
					else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T4))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_1")));
					}
					else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T3))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_1")));
					}
					else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T2))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_2")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_1"), 2));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_1") ,2));
					}
					else if (rand1 < ((int)((mobGrinder.getMineralChance(Crystal.T1))*10000))) {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_1"), 3));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_1"), 3));
					}  else {
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_metallic_crystal_fragment_1")));
						newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_ionic_crystal_fragment_1")));
					}
				} else {
					continue;
				}
				if (newItems.size() >= 1)
				{
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						for (ItemStack item : newItems)
						{
							debug(mobGrinder, "Updating inventory:"+item);
							this.updateInventory(Action.ADD, dest_inv, item);
						}
					} catch (InterruptedException e) {
						return;
					}
				} else {

					debug(mobGrinder, "Didn't add any items. Perhaps ["+itemID+"] is an invalid item name?");
				}
				break;
			}	
		}
		mobGrinder.skippedCounter = 0;
	}
	
	
	
	@Override
	public void run() {
		if (this.mobGrinder.lock.tryLock()) {
			try {
				try {
					processMobGrinderUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.mobGrinder.lock.unlock();
			}
		} else {
			debug(this.mobGrinder, "Failed to get lock while trying to start task, aborting.");
		}
	}

}
