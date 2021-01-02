package com.avrgaming.civcraft.util;

import org.bukkit.ChatColor;

public class CivColor {
	public static final String Black = "\u00A70";
    public static final String Navy = "\u00A71";
    public static final String Green = "\u00A72";
    public static final String Blue = "\u00A73";
    public static final String Red = "\u00A74";
    public static final String Purple = "\u00A75";
    public static final String Gold = "\u00A76";
    public static final String LightGray = "\u00A77";
    public static final String Gray = "\u00A78";
    public static final String DarkPurple = "\u00A79";
    public static final String LightGreen = "\u00A7a";
    public static final String LightBlue = "\u00A7b";
    public static final String Rose = "\u00A7c";
    public static final String LightPurple = "\u00A7d";
    public static final String Yellow = "\u00A7e";
    public static final String White = "\u00A7f";
    public static final String BOLD = "" + ChatColor.BOLD;
    public static final String ITALIC = "" + ChatColor.ITALIC;
    public static final String RESET = "" + ChatColor.RESET;
    public static final String UNDERLINE = "" + ChatColor.UNDERLINE;
    
    public static final String BlackBold = "\u00A70" + ChatColor.BOLD;
    public static final String NavyBold = "\u00A71" + ChatColor.BOLD;
    public static final String GreenBold = "\u00A72" + ChatColor.BOLD;
    public static final String BlueBold = "\u00A73" + ChatColor.BOLD;
    public static final String RedBold = "\u00A74" + ChatColor.BOLD;
    public static final String PurpleBold = "\u00A75" + ChatColor.BOLD;
    public static final String GoldBold = "\u00A76" + ChatColor.BOLD;
    public static final String LightGrayBold = "\u00A77" + ChatColor.BOLD;
    public static final String GrayBold = "\u00A78" + ChatColor.BOLD;
    public static final String DarkPurpleBold = "\u00A79" + ChatColor.BOLD;
    public static final String LightGreenBold = "\u00A7a" + ChatColor.BOLD;
    public static final String LightBlueBold = "\u00A7b" + ChatColor.BOLD;
    public static final String RoseBold = "\u00A7c" + ChatColor.BOLD;
    public static final String LightPurpleBold = "\u00A7d" + ChatColor.BOLD;
    public static final String YellowBold = "\u00A7e" + ChatColor.BOLD;
    public static final String WhiteBold = "\u00A7f" + ChatColor.BOLD;
    
    public static final String BlackItalic = "\u00A70" + ChatColor.ITALIC;
    public static final String NavyItalic = "\u00A71" + ChatColor.ITALIC;
    public static final String GreenItalic = "\u00A72" + ChatColor.ITALIC;
    public static final String BlueItalic = "\u00A73" + ChatColor.ITALIC;
    public static final String RedItalic = "\u00A74" + ChatColor.ITALIC;
    public static final String PurpleItalic = "\u00A75" + ChatColor.ITALIC;
    public static final String GoldItalic = "\u00A76" + ChatColor.ITALIC;
    public static final String LightGrayItalic = "\u00A77" + ChatColor.ITALIC;
    public static final String GrayItalic = "\u00A78" + ChatColor.ITALIC;
    public static final String DarkPurpleItalic = "\u00A79" + ChatColor.ITALIC;
    public static final String LightGreenItalic = "\u00A7a" + ChatColor.ITALIC;
    public static final String LightBlueItalic = "\u00A7b" + ChatColor.ITALIC;
    public static final String RoseItalic = "\u00A7c" + ChatColor.ITALIC;
    public static final String LightPurpleItalic = "\u00A7d" + ChatColor.ITALIC;
    public static final String YellowItalic = "\u00A7e" + ChatColor.ITALIC;
    public static final String WhiteItalic = "\u00A7f" + ChatColor.ITALIC;
    
    public static final String YellowBoldItalic = "\u00A7e" + ChatColor.BOLD + ChatColor.ITALIC;
    
	
	/*
	 * Takes an input from a yaml and converts 'Essentials' style color codes into 
	 * in game color codes.
	 * XXX this is slow, so try not to do this at runtime. Just when configs load.
	 */
	public static String colorize(String input) {
		String output = input;
		
		output = output.replaceAll("<red>", Red);
		output = output.replaceAll("<rose>", Rose);
		output = output.replaceAll("<gold>", Gold);
		output = output.replaceAll("<yellow>", Yellow);
		output = output.replaceAll("<green>", Green);
		output = output.replaceAll("<lightgreen>", LightGreen);
		output = output.replaceAll("<lightblue>", LightBlue);
		output = output.replaceAll("<blue>", Blue);
		output = output.replaceAll("<navy>", Navy);
		output = output.replaceAll("<darkpurple>", DarkPurple);
		output = output.replaceAll("<lightpurple>", LightPurple);
		output = output.replaceAll("<purple>", Purple);
		output = output.replaceAll("<white>", White);
		output = output.replaceAll("<lightgray>", LightGray);
		output = output.replaceAll("<gray>", Gray);
		output = output.replaceAll("<black>", Black);
		output = output.replaceAll("<b>", ""+ChatColor.BOLD);
		output = output.replaceAll("<u>", ""+ChatColor.UNDERLINE);
		output = output.replaceAll("<i>", ""+ChatColor.ITALIC);
		output = output.replaceAll("<magic>", ""+ChatColor.MAGIC);
		output = output.replaceAll("<s>", ""+ChatColor.STRIKETHROUGH);
		output = output.replaceAll("<r>", ""+ChatColor.RESET);
		
		return output;
	}
	
	public static String strip(String line) {

		for (ChatColor cc : ChatColor.values())
			line.replaceAll(cc.toString(), "");
		return line;
	}

	public static String valueOf(String color) {
		switch (color.toLowerCase()) {
		case "black":
			return Black;
		case "navy":
			return Navy;
		case "green":
			return Green;
		case "blue":
			return Blue;
		case "red":
			return Red;
		case "purple":
			return Purple;
		case "gold":
			return Gold;
		case "lightgray":
			return LightGray;
		case "gray":
			return Gray;
		case "darkpurple":
			return DarkPurple;
		case "lightgreen":
			return LightGreen;
		case "lightblue":
			return LightBlue;
		case "rose":
			return Rose;
		case "lightpurple":
			return LightPurple;
		case "yellow":
			return Yellow;
		case "white":
			return White;
		default:
			return White;
		}		
	}

	public static String stripTags(String input) {
		String output = input;
		
		output = output.replaceAll("<red>", "");
		output = output.replaceAll("<rose>", "");
		output = output.replaceAll("<gold>", "");
		output = output.replaceAll("<yellow>", "");
		output = output.replaceAll("<green>", "");
		output = output.replaceAll("<lightgreen>", "");
		output = output.replaceAll("<lightblue>", "");
		output = output.replaceAll("<blue>", "");
		output = output.replaceAll("<navy>", "");
		output = output.replaceAll("<darkpurple>", "");
		output = output.replaceAll("<lightpurple>", "");
		output = output.replaceAll("<purple>", "");
		output = output.replaceAll("<white>", "");
		output = output.replaceAll("<lightgray>", "");
		output = output.replaceAll("<gray>", "");
		output = output.replaceAll("<black>", "");
		output = output.replaceAll("<b>", "");
		output = output.replaceAll("<u>", "");
		output = output.replaceAll("<i>", "");
		output = output.replaceAll("<magic>", "");
		output = output.replaceAll("<s>", "");
		output = output.replaceAll("<r>", "");
		
		return output;
	}
	
}
