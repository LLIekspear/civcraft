
// $Id$
/*
 * Very bad image drawer
 * Copyright (C) 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

importPackage(Packages.java.io);
importPackage(Packages.java.awt);
importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);
importPackage(Packages.com.sk89q.worldedit.regions);

context.checkArgs(1, 3, "<filename>");

//var f = new File(argv[1]);

var session = context.remember();
var WALLSIGN = 68;
var SIGNPOST = 63;

var f = new FileWriter("templates/"+argv[1]);
var region = context.getSession().getRegion();

var r_x = region.getWidth();
var r_y = region.getHeight();
var r_z = region.getLength();

var iter = region.iterator()

try {
    f.write(r_x+";"+r_y+";"+r_z+"\n");
    for (var z = 0; z < r_z; z++) {
        for ( var y = 0; y < r_y; y++) {
            for (var x = 0; x < r_x; x++) {
                var blkVect = iter.next();
                var blk = session.getBlock(blkVect);
                var blkId = blk.getId();
                var blkData = blk.getData();
                
                f.write(x+":"+y+":"+z+",");
                f.write(blkId+":"+blkData);
                if (blkId === WALLSIGN || blkId == SIGNPOST) {
                    var line1 = blk.getNbtData().getString("Text1").replace("\"", "");
                    var line2 = blk.getNbtData().getString("Text2").replace("\"", "");
                    var line3 = blk.getNbtData().getString("Text3").replace("\"", "");
                    var line4 = blk.getNbtData().getString("Text4").replace("\"", "");
                    var text = "{text:";
                    var text2 = "}";
                    var text3 = "text:";
                    var extra = "{extra:[";
                    var extra2 = "]"
                    line1 = line1.replace(text, "");
                    line1 = line1.replace(text2, "");
                    line1 = line1.replace(text3, "");
                    line1 = line1.replace(extra, "");
                    line1 = line1.replace(extra2, "");
                    line2 = line2.replace(text, "");
                    line2 = line2.replace(text2, "");
                    line2 = line2.replace(text3, "");
                    line2 = line2.replace(extra, "");
                    line2 = line2.replace(extra2, "");
                    line3 = line3.replace(text, "");
                    line3 = line3.replace(text2, "");
                    line3 = line3.replace(text3, "");
                    line3 = line3.replace(extra, "");
                    line3 = line3.replace(extra2, "");
                    line4 = line4.replace(text, "");
                    line4 = line4.replace(text2, "");
                    line4 = line4.replace(text3, "");
                    line4 = line4.replace(extra, "");
                    line4 = line4.replace(extra2, "");
                    f.write(",");
                    f.write(((line1.equals("null"))? "": line1)+",");
                    f.write(((line2.equals("null"))? "": line2)+",");
                    f.write(((line3.equals("null"))? "": line3)+",");
                    f.write(((line4.equals("null"))? "": line4)+"\n");
                }
                else {
                    f.write("\n");

                }
            }
        }
    }



}
finally
{
    f.close();
}
