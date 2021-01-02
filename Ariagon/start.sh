#!/bin/sh
while true; do
    java -Xmx2G -jar Spigot.jar nogui
    pkill -f "Spigot"
    echo "5 seconds"
    sleep 5
done
