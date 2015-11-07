<h2>What is this?</h2>
For sure you know about the Votifier plugin Bukkit utilizes to track votes on sites like minecraft-server-list.com and reward users based on that. But what if you don't use Bukkit? What if you use the unmodified vanilla server hosting software provided by minecraft.net? Well, Vanilla votifier can help you with that! VanillaVotifier emulates the behaviour of votifier plugins, and sends commands to Minecraft through RCon whenever someone votes for your server!

<h2>Requisites</h2>
There are only two requisites to run VanillaVotifier: to have a server which is able to run the VanillaVotifier program (you can usually archive this with a VPS or dedicated server), and, of course, your Minecraft server has to have RCon enabled and set up.

<h2>Installation</h2>
You can download the lastest version of VanillaVotifier from <a href="https://github.com/VirtualDragon/VanillaVotifier/releases" target="_blank">here</a> and upload it to your server. If you're on Linux, you may like to do it through the following command: <code>wget $(curl -s https://api.github.com/repos/VirtualDragon/VanillaVotifier/releases | grep -m 1 '"browser_download_url": ' | sed 's/.*"browser_download_url": "//' | sed 's/.$//')</code>.

Once downloaded, you can startup the program with <code>java -jar Vanilla_votifier.jar</code>. The program will generate a configuration file and halt immediately after. Please open the file and configure its settings. If you need help with the configuration, set the password for the Minecraft RCon login, start Vanilla votifier again, type <code>manual</code>, then enter.

Please note that the port for VanillaVotifier configured has to be opened! In Linux, this can be achived with <code>iptables -I INPUT -p tcp --dport &lt;VanillaVotifier port&gt; -j ACCEPT</code>.

<h2>Any more questions? Did you find a bug or want a new feature?</h2>
If you have any questions, please read through the manual of VanillaVotifier first (you can do this by typing the <code>manual</code> command while the program is running): this may already helps you out!

If you find a bug or want a new feature, please post it on the <a href="https://github.com/VirtualDragon/VanillaVotifier/issues" target="_blank">issue tracker</a>.
