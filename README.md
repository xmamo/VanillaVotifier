## What is this? ##
For sure you know about the Votifier plugin Bukkit utilizes to track votes on sites like minecraft-server-list.com and reward users based on that. But what if you don't use Bukkit? What if you use the unmodified vanilla server hosting software provided by minecraft.net? Well, VanillaVotifier can help you with that! VanillaVotifier is a [GPLv3 licensed](https://raw.githubusercontent.com/VirtualDragon/VanillaVotifier/master/src/main/resources/co/virtualdragon/vanillaVotifier/impl/lang/license.txt)GPLv3 licensed program which emulates the behaviour of votifier plugins, and sends commands to Minecraft through RCon whenever someone votes for your server!

## Requisites ##
There are only two requisites to run VanillaVotifier: to have a server which is able to run the VanillaVotifier program (you can usually archive this with a VPS or dedicated server), and, of course, your Minecraft server has to have RCon enabled and set up.

## Installation ##
You can download the lastest version of VanillaVotifier from [here](https://github.com/VirtualDragon/VanillaVotifier/releases/latest) and upload it to your server. If you're on Linux, you may like to do it through the following command: `wget $(curl -s https://api.github.com/repos/VirtualDragon/VanillaVotifier/releases | grep -m 1 '"browser_download_url": ' | sed 's/.*"browser_download_url": "//' | sed 's/.$//')`.

Once downloaded, you can startup the program with `java -jar VanillaVotifier.jar`. Please open the file and configure its settings. Once done, type `reload` to reload the config. If you need help with the configuration, you can type the `manual` command.

Please note that the port for VanillaVotifier configured has to be opened! In Linux, this can be achived with `iptables -I INPUT -p tcp --dport <VanillaVotifier port> -j ACCEPT`.

## Usage ##
For a list of all possible commands, you can use the `help` command. For a more detailed explanation of what every command does, you can type `manua` to read through VanillaVotifier's manual, however if you don't want to start VanillaVotifier to read it, you can go [here](https://raw.githubusercontent.com/VirtualDragon/VanillaVotifier/master/src/main/resources/co/virtualdragon/vanillaVotifier/impl/lang/manual.txt) instead.

## Any more questions? Did you find a bug or want a new feature? ##
If you have any questions, please read through the manual of VanillaVotifier first: this may already helps you out!

If you find a bug or want a new feature, please post it on the [issue tracker](https://github.com/VirtualDragon/VanillaVotifier/issues).
