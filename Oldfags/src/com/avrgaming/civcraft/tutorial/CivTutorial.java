package com.avrgaming.civcraft.tutorial;

import gpl.AttributeUtil;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMaterial;
import com.avrgaming.civcraft.config.ConfigMaterialCategory;
import com.avrgaming.civcraft.lorestorage.LoreCraftableMaterial;
import com.avrgaming.civcraft.lorestorage.LoreGuiItem;
import com.avrgaming.civcraft.lorestorage.LoreGuiItemListener;
import com.avrgaming.civcraft.lorestorage.LoreMaterial;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.civcraft.util.ItemManager;

public class CivTutorial {

	public static Inventory tutorialInventory = null;
	public static Inventory craftingHelpInventory = null;
	public static Inventory guiInventory = null;
	public static final int MAX_CHEST_SIZE = 6;
	
	public static void showTutorialInventory(Player player) {	
		if (tutorialInventory == null) {
			tutorialInventory = Bukkit.getServer().createInventory(player, 9*3, CivSettings.localize.localizedString("tutorial_gui_heading"));
		
	
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_workbench_heading"), ItemManager.getId(Material.WORKBENCH), 0, 
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line1"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line2"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line3"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line4"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line5"),
				ChatColor.RESET+CivColor.LightGreen+CivSettings.localize.localizedString("tutorial_workbench_Line6")
				));
		
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_compass_heading"), ItemManager.getId(Material.COMPASS), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line5")
					));
			
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_diamondOre_heading"), ItemManager.getId(Material.DIAMOND_ORE), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("var_tutorial_diamondOre_Line5",CivSettings.CURRENCY_NAME),
					ChatColor.RESET+CivSettings.localize.localizedString("var_tutorial_diamondOre_Line6",CivSettings.CURRENCY_NAME)
					));
			
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_Fence_heading"), ItemManager.getId(Material.FENCE), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("var_tutorial_Fence_Line4",CivSettings.CURRENCY_NAME),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line5"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line6"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line7")
					));
			
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_goldHelmet_heading"), ItemManager.getId(Material.GOLD_HELMET), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line5"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line6"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line7")
					));
			
			if (CivGlobal.isCasualMode()) {
				tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_firework_heading"), ItemManager.getId(Material.FIREWORK), 0, 
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line1"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line2"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line3"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line4"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line5")
						));
			} else {
				tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_ironSword_heading"), ItemManager.getId(Material.IRON_SWORD), 0, 
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line1"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line2"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line3"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line4"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line5"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line6")
						));
			}
			
			tutorialInventory.setItem(8, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_bookAndQuill_heading"), ItemManager.getId(Material.BOOK_AND_QUILL), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line2"),
					ChatColor.RESET+CivColor.LightGreen+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line4")
					));
			
			tutorialInventory.setItem(9, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_campQuest_heading"), ItemManager.getId(Material.BOOK_AND_QUILL), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line4")
					));
						
			tutorialInventory.setItem(10, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_civQuest_heading"), ItemManager.getId(Material.BOOK_AND_QUILL), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line5")
					));
			
			
			tutorialInventory.setItem(11, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_needRecipe_heading"), ItemManager.getId(Material.WORKBENCH), 0, 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line5")
					));
			
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
			for (ConfigMaterial mat : cat.materials.values()) {
				if (mat.id.equals("mat_found_civ"))
				{
				ItemStack stack = getInfoBookForItem(mat.id);
				if (stack != null) {
					stack = LoreGuiItem.setAction(stack, "TutorialRecipe");
					tutorialInventory.setItem(19,LoreGuiItem.asGuiItem(stack));
				}
				} else if (mat.id.equals("mat_found_camp")) {
					ItemStack stack = getInfoBookForItem(mat.id);
					if (stack != null) {
						stack = LoreGuiItem.setAction(stack, "TutorialRecipe");
						tutorialInventory.setItem(18,LoreGuiItem.asGuiItem(stack));
					}
				}
			}
			}
			
			/* Add back buttons. */
			ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getId(Material.MAP), 0, CivSettings.localize.localizedString("tutorial_lore_backToCategories"));
			backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
			backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
			backButton = LoreGuiItem.setActionData(backButton, "invName", guiInventory.getName());
			tutorialInventory.setItem(26, backButton);
		
			LoreGuiItemListener.guiInventories.put(tutorialInventory.getName(), tutorialInventory);
		}
		
		if (player != null && player.isOnline() && player.isValid()) {
			player.openInventory(tutorialInventory);	
		}
	}
	
	public static ItemStack getInfoBookForItem(String matID) {
		LoreCraftableMaterial loreMat = LoreCraftableMaterial.getCraftMaterialFromId(matID);
		ItemStack stack = LoreMaterial.spawn(loreMat);
							
		if (!loreMat.isCraftable()) {
			return null;
		}
		
		AttributeUtil attrs = new AttributeUtil(stack);
		attrs.removeAll(); /* Remove all attribute modifiers to prevent them from displaying */
		LinkedList<String> lore = new LinkedList<String>();
		
		lore.add(""+ChatColor.RESET+ChatColor.BOLD+ChatColor.GOLD+CivSettings.localize.localizedString("tutorial_clickForRecipe"));
		
		attrs.setLore(lore);				
		stack = attrs.getStack();
		return stack;
	}
	
	public static void showCraftingHelp(Player player) {
		if (craftingHelpInventory == null) {
			craftingHelpInventory = Bukkit.getServer().createInventory(player, MAX_CHEST_SIZE*9, CivSettings.localize.localizedString("tutorial_customRecipesHeading"));

			/* Build the Category Inventory. */
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
				if (cat.craftableCount == 0) {
					continue;
				}

				int identifier;
				if (cat.name.contains("Fish")) {
					identifier = ItemManager.getId(Material.RAW_FISH);
				}
				else if (cat.name.contains("Catalyst")) {
					identifier = ItemManager.getId(Material.BOOK);
				}
				else if (cat.name.contains("Gear")) {
					identifier = ItemManager.getId(Material.IRON_SWORD);
				}
				else if (cat.name.contains("Materials")) {
					identifier = ItemManager.getId(Material.WOOD_STEP);
				}
				else if (cat.name.contains("Tools")) {
					identifier = ItemManager.getId(Material.IRON_SPADE);
				}
				else if (cat.name.contains("Eggs")) {
					identifier = ItemManager.getId(Material.MONSTER_EGG);
				}
				else {
					identifier = ItemManager.getId(Material.WRITTEN_BOOK);
				}
				ItemStack infoRec = LoreGuiItem.build(cat.name, 
						identifier, 
						0, 
						CivColor.LightBlue+cat.materials.size()+" "+CivSettings.localize.localizedString("tutorial_lore_items"),
						CivColor.Gold+CivSettings.localize.localizedString("tutorial_lore_clickToOpen"));
						infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
						infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showGuiInv");
						infoRec = LoreGuiItem.setActionData(infoRec, "invName", cat.name+" "+CivSettings.localize.localizedString("tutorial_lore_recipes"));
						
						craftingHelpInventory.addItem(infoRec);
						
						
				Inventory inv = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, cat.name+" "+CivSettings.localize.localizedString("tutorial_lore_recipes"));
				for (ConfigMaterial mat : cat.materials.values()) {
					ItemStack stack = getInfoBookForItem(mat.id);
					if (stack != null) {
						stack = LoreGuiItem.setAction(stack, "ShowRecipe");
						inv.addItem(LoreGuiItem.asGuiItem(stack));
					}
				}
				
				/* Add back buttons. */
				ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getId(Material.MAP), 0, CivSettings.localize.localizedString("tutorial_lore_backToCategories"));
				backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
				backButton = LoreGuiItem.setActionData(backButton, "invType", "showCraftingHelp");
				inv.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
				
				LoreGuiItemListener.guiInventories.put(inv.getName(), inv);
			}
			
			/* Add back buttons. */
			ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getId(Material.MAP), 0, CivSettings.localize.localizedString("tutorial_lore_backToCategories"));
			backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
			backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
			backButton = LoreGuiItem.setActionData(backButton, "invName", guiInventory.getName());
			craftingHelpInventory.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
			
			LoreGuiItemListener.guiInventories.put(craftingHelpInventory.getName(), craftingHelpInventory);
		}
		
		player.openInventory(craftingHelpInventory);
	}
	
	public static void spawnGuiBook(Player player) {
		if (guiInventory == null) {
			guiInventory = Bukkit.getServer().createInventory(player, 3*9, CivSettings.localize.localizedString("tutorial_lore_CivcraftInfo"));

			ItemStack infoRec = LoreGuiItem.build(CivSettings.localize.localizedString("tutorial_lore_civInfoShort"), 
					ItemManager.getId(Material.WRITTEN_BOOK), 
							0, CivColor.Gold+CivSettings.localize.localizedString("tutorial_lore_clicktoView"));
			infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
			infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showTutorialInventory");
			guiInventory.addItem(infoRec);
			
			ItemStack craftRec = LoreGuiItem.build(CivSettings.localize.localizedString("tutorial_lore_craftingRecipes"), 
					ItemManager.getId(Material.WORKBENCH), 
					0, CivColor.Gold+CivSettings.localize.localizedString("tutorial_lore_clicktoView"));
			craftRec = LoreGuiItem.setAction(craftRec, "OpenInventory");
			craftRec = LoreGuiItem.setActionData(craftRec, "invType", "showCraftingHelp");
			guiInventory.addItem(craftRec);
			
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
		}
		
		player.openInventory(guiInventory);

	}
	
	
}
