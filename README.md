<h2>What is this?</h2>
For sure you know about the Votifier plugin Bukkit utilizes to track votes on sites like minecraft-server-list.com and reward users based on that. But what if you don't use Bukkit? What if you use the unmodified vanilla server hosting software provided by minecraft.net? Well, VanillaVotifier can help you with that! VanillaVotifier is a <a href="https://raw.githubusercontent.com/VirtualDragon/VanillaVotifier/master/src/main/resources/co/virtualdragon/vanillaVotifier/impl/lang/license.txt" target="_blank">GPLv3 licensed</a> program which emulates the behaviour of votifier plugins, and sends commands to Minecraft through RCon whenever someone votes for your server!

<h2>Requisites</h2>
There are only two requisites to run VanillaVotifier: to have a server which is able to run the VanillaVotifier program (you can usually archive this with a VPS or dedicated server), and, of course, your Minecraft server has to have RCon enabled and set up.

<h2>Installation</h2>
You can download the lastest version of VanillaVotifier from <a href="https://github.com/VirtualDragon/VanillaVotifier/releases/latest" target="_blank">here</a> and upload it to your server. If you're on Linux, you may like to do it through the following command: <code>wget $(curl -s https://api.github.com/repos/VirtualDragon/VanillaVotifier/releases | grep -m 1 '"browser_download_url": ' | sed 's/.*"browser_download_url": "//' | sed 's/.$//')</code>.

Once downloaded, you can startup the program with <code>java -jar VanillaVotifier.jar</code>. Please open the file and configure its settings. Once done, type <code>reload</code> to reload the config. If you need help with the configuration, you can type the <code>manual</code> command.

Please note that the port for VanillaVotifier configured has to be opened! In Linux, this can be achived with <code>iptables -I INPUT -p tcp --dport &lt;VanillaVotifier port&gt; -j ACCEPT</code>.

<h2>Usage</h2>
For a list of all possible commands, you can use the <code>help</code> command. For a more detailed explanation of what every command does, you can type <code>manual</code> to read through VanillaVotifier's manual, however if you don't want to start VanillaVotifier to read it, you can go <a href="https://raw.githubusercontent.com/VirtualDragon/VanillaVotifier/master/src/main/resources/co/virtualdragon/vanillaVotifier/impl/lang/manual.txt" target="_blank">here</a> instead.

<h2>Any more questions? Did you find a bug or want a new feature?</h2>
If you have any questions, please read through the manual of VanillaVotifier first: this may already helps you out!

If you find a bug or want a new feature, please post it on the <a href="https://github.com/VirtualDragon/VanillaVotifier/issues" target="_blank">issue tracker</a>.
