## What is this? ##
For sure you know about the Votifier plugin Bukkit utilizes to track votes on sites like minecraft-server-list.com and reward users based on that. But what if you don't use Bukkit? What if you use the unmodified vanilla server hosting software provided by minecraft.net? Well, VanillaVotifier can help you with that! VanillaVotifier is a [GPLv3 licensed](https://raw.githubusercontent.com/xMamo/VanillaVotifier/master/src/main/resources/mamo/vanillaVotifier/impl/lang/license.txt) program which emulates the behaviour of votifier plugins, and sends commands to Minecraft through RCon whenever someone votes for your server!

## Requisites ##
There are only two requisites to run VanillaVotifier: to have a server which is able to run the VanillaVotifier program (you can usually archive this with a VPS or dedicated server), and, of course, your Minecraft server has to have RCon enabled and set up.

## Installation ##
You can download the latest version of VanillaVotifier from [here](https://github.com/xMamo/VanillaVotifier/releases/latest) and upload it to your server. If you're on Linux, you may like to do it through the following command: `wget $(wget -q -O - https://api.github.com/repos/xMamo/VanillaVotifier/releases | grep -Pio -m 1 '"browser_download_url":\s*?"\K(\\"|[^"])*')`.

Once downloaded, you can startup the program with `java -jar VanillaVotifier.jar`. Please open the file and configure its settings. Once done, type `reload` to reload the config. If you need help with the configuration, you can type the `manual` command.

Please note that the port for VanillaVotifier configured has to be opened! In Linux, this can be achieved with `iptables -I INPUT -p tcp --dport <VanillaVotifier port> -j ACCEPT`.

## Usage ##
For a list of all possible commands, you can use the `help` command. For a more detailed explanation of what every command does, you can type `manual` to read through VanillaVotifier's manual, however if you don't want to start VanillaVotifier to read it, you can go [here](https://raw.githubusercontent.com/xMamo/VanillaVotifier/master/src/main/resources/mamo/vanillaVotifier/impl/lang/manual.txt) instead.

## Note for Minecraft 1.9+ ##
Due to [#11](https://github.com/xMamo/VanillaVotifier/issues/11), some commands may produce a server crash. Take the suggestion of Iffy and Morpheus and use scoreboard commands instead. For example, if you want to give an apple to voters, create a dummy score called `voted` and increment it with `scoreboard players add ${user-name} voted 1` every time a user votes; also add an always loaded clock to your world which constantly executes the two following commands: `give @a[score_voted_min=1] minecraft:apple` and `scoreboard players remove @a[score_voted_min=1] voted 1`.

## Any more questions? Did you find a bug or want a new feature? ##
If you have any questions, please read through the manual of VanillaVotifier first: this may already helps you out!

If you find a bug or want a new feature, please post it on the [issue tracker](https://github.com/xMamo/VanillaVotifier/issues).
