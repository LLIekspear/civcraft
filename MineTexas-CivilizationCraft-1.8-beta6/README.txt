You simply need to extract the templates and plugins directories into the folder with your spigot jar.
In other words, templates goes in the same folder as plugins normally does for a spigot server.

To run a server, you will need the following additional plugins:
dynmap-civcraft (Optional)
Dynmap v2.3+ (Required if using included dynmap-civcraft)
HeroChat (Optional)
CustomMobs 4.16 (Required, included)
NoCheatPlus (Optional)
VanishNoPacket (Optional)
Vault v1.4.1+
WorldBorder
WorldEdit 6.13+
WorldGuard 6.12+
TitleAPI (Optional, included)

You will also need a mysql server. The mysql configuration must be entered in the plugins/Civcraft/config.yml
MySQL schemas: game, global (Must match what the config.yml shows)
Please be sure to create your own unique MySQL user and password.

You will also need the Spigot-1.11.2.jar, which you will need to build yourself:
https://hub.spigotmc.org/jenkins/job/BuildTools/

---------------------------

teleportsOn.txt
Each line in this file will be executed by the console at the time set by war.yml
Use this file to disable teleportation permissions during war time if so desired.
Otherwise, leave it empty.

teleportsOff.txt
Each line in this file will be executed by the console when war ends
Use this file to undo the commands from teleportsOn.txt
Otherwise, leave it empty.

---------------------------

If you run into any issues, feel free to contact ataranlen in one of the following ways:
http://www.minetexas.com/minecraft-server-forum/
Skype: ataranlen
Email: help@MineTexas.com