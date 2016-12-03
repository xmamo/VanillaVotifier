# VanillaVotifier #
VanillaVotifier is a Java application which listens for votes made on Minecraft server lists for your server. Inspired by Bukkit's Votifier, VanillaVotifier aids to provide the plugins same functionality — to execute some kind of custom action on vote — but without requiring a Bukkit server. VanillaVotifier creates a liteweight server listening for votes; Minecraft commands and/or scripts can be executed when such events occur.

## Requirements ##
VanillaVotifier is a standalone application, meaning it has to be executed on a virtual or dedicated server instance. It is highly recommended to run both your Minecraft server and VanillaVotifier on the same server instance.

## Installation ##
You can download the latest version of VanillaVotifier [here](https://github.com/xMamo/VanillaVotifier/releases/latest). Once downloaded, just run the application using `java -jar VanillaVotifier.jar` (it is recommended to run VanillaVotifier in an empty directory). On first startup, VanillaVotifier automatically creates a new configuration file in the current working directory (called `config.yaml`) and two additional key files referenced by the config (`public.pem` and `private.pem`). These key files are needed in order to communicate between you (the server) and a Minecraft server list. Once VanillaVotifier finishes starting up up, type `showkey pub`: you will get the public key string most Minecraft server lists require you to provide when you register your server.

## Commands ##
While VanillaVotifier is running, some built-in commands can be executed. You can get a list of these commands using `help`. Each command is explained in greater detail below.

### `help` ###
Displays a list of all available commands, with a short description explaining their usage.

### `info` ###
Displays information about VanillaVotifier, such as its version, the author, and the libraries being used.

### `stop` ###
Stops the VanillaVotifier server. Using `stop` should be preferred to quitting using `^C`.

### `restart` ###
Restarts the VanillaVotifier server. Useful for reloading the configuration after changes have been made to `config.yaml`. Using this command is equivalento to `stop`ping and starting the server again.

### `genkeypair [key-size]` ###
Generates a new key-pair for the VanillaVotifier server. `key-size` defaults to 2048.

### `showkey <(pub|priv)>` ###
Displays the current public/private key. Don't share your private key with anyone!

### `testquery <query>` and `testvote <IGN>` ###
Both commands aid to emulate a vote event, as if it was sent by an external Minecraft server list website. `testvote <IGN>` is a wrapper of the `testquery <query>` command, and it is sufficient for testing in most cases.

## Configuration ##
VanillaVotifier can be configured by editing the `config.yaml` file. The following configuration is applied by default on first startup:

```YAML
# The config version. Used internally by VanillaVotifier. Please don't touch!
config-version: 4

# The relative path to the directory in which to save the log files.
# If the directory doesn't exist, it will be created.
log-directory: 'logs'

# The IP address and port of the VanillaVotifier server.
server:
  ip: '0.0.0.0'
  port: 8192

# The relative path to the public and private key files.
# If both files don't exist, a new 2048-bit key pair will be generated.
key-pair-files:
  public: 'public.pem'
  private: 'private.pem'

# A list of actions to perform as soon as somebody votes for your server.
on-vote:
  # Sends one or more commands to a Minecraft RCon server.
  - action: 'rcon'
    # The IP address, port, and password of the RCon server.
    # Using a local IP address should be preferred, since the RCon protocol requires passwords to be sent as plaintext.
    server:
      ip: '0.0.0.0'
      port: 25575
      password: 'password'

    # The commands to send to the RCon server.
    # "${service-name}" will be replaced with the service the player has voted on (for example MCSL).
    # "${user-name}" will be replaced with the IGN of the player.
    # "${address}" will be replaced with the player's IP address.
    # "${timestamp}" will be replaced with the time stamp in which the player has voted. Format may vary depending on voting service.
    #
    # It is not recommended to use commands such as "give", "effect", etc., since they wouldn't work if the player is offline.
    # Instead, set a certain score (using the "scoreboard players set <player> <objective> <score> [dataTag]" command) and handle rewarding through an ingame Command Block clock which is always loaded.
    commands:
      - 'tellraw @a {"text":"${user-name} has just voted for this server on ${service-name}. Thanks!","color":"yellow"}'
      - 'scoreboard players add ${user-name} voted 1'

    regex-replace: {}

  # Executes one or more programs/commands.
  # The following environment variables will be set: "voteServiceName" to ${service-name}, "voteUserName" to ${user-name}, "voteAddress" to ${address}, "voteTimestamp" to ${timestamp}.
  - action: 'shell'
    commands:
      - 'test -x onvote.sh && ./onvote.sh' # "CMD /C IF EXIST onvote.bat onvote.bat" on Windows.

    regex-replace: {}
```

The default configuration is extensively documented, such that all configuration sections should be self-explanatory. Still, all options are explained below.

### `config-version` ###
The current config version. Used internally by VanillaVotifier to automatically upgrade configuration in future versions of the program. Please don't change this section!

### `log-directory` ###
The relative path to the directory in which to save the log files. If the directory doesn't exist, it will be created.

### `server` ###
Has two subsections: `ip` and `port`. They determine the IP and port of the VanillaVotifier server.

### `key-pair-files` ###
Has two subsections: `public` and `private`. They determine the relative path to the public and private key files. If both files don't exist, a new 2048-bit key-pair will be generated. You can regenerate the key-pair by executing VanillaVotifier's `genkeypair [key-size]` command or by using OpenSSL or similar programs.

### `on-vote` ###
Contains a list of actions to perform as soon as somebody votes for your server. Actions can be of type `rcon` or of type `shell`. In the default configuration, `on-vote` has both actions, once per action type; however the list can contain an indefinite amount of elements, if needed.

#### `rcon` action ####
Sends one or more commands to a Minecraft RCon server.

##### `server` #####
Has three subsections: `ip`, `port`, and `password`. They determine the internet address and the passwod of the RCon server to connect to. It is highly recommended to connect only to local RCon servers, since the RCon protocol requires passwords to be sent as plaintext.

##### `commands` #####
A list of commands to send to the RCon server. Before execution, the command will be parsed and checked against special sets of characters:
 * `${service-name}` will be replaced with the service the player has voted on (for example MCSL).
 * `${user-name}` will be replaced with the IGN of the player.
 * `${address}` will be replaced with the player's IP address.
 * `${timestamp}` will be replaced with the time stamp in which the player has voted. Format may vary depending on voting service.

It is not recommended to use commands such as `give`, `effect`, etc., since they wouldn't work if the player is offline. Instead, set a certain score (using the `scoreboard players set <player> <objective> <score> [dataTag]` command) and handle rewarding through an ingame Command Block clock which is always loaded.

##### `regex-replace` #####
Contains a list of regex replacements to perform on `${service-name}`, `${user-name}`, `${address}`, `${timestamp}`. Can be used to sanitize input.

Example:
```YAML
    regex-replace:
      'Hi': 'Hello'
      '[dD]\s*[iI1]\s*[cC]\s*[kK]': '****'
```
 
#### `shell` action ####
Executes a list of commands, scripts, or programs. The following environment variables will be set: `voteServiceName` to `${service-name}`, `voteUserName` to `${user-name}`, `voteAddress` to `${address}`, `voteTimestamp` to `${timestamp}`. As with `rcon` actions, a list of regex replacements can be specified through the `regex-replace` section.

###  Configuration examples ###
#### `rcon` action example ####
This example shows how to set up a simple "get a diamond for voting" system using RCon. Here's how to do it:

1. Log in to your Minecraft server and execute the following command: `/scoreboard objectives add voted dummy`.

2. Create a Command Block chain, as shown in the following picture below.

   You have to make sure that the chain is inside a spawn chunk, and thus always loaded. To verify that this condition is met, you can place a Repeating Command Block near the chain, set it to be Always Active, set the command to `tellraw <your IGN> {"text":"I'm loaded!"}`, teleport really far away, and verify that your chat keeps getting spammed with `I'm loaded!` text messages.

   If you find out the Command Block chain is not inside a spawn chunk, and you aren't sure where spawn chunks are, here is a simple solution in case you are using vanilla Minecraft: select a random spot around you, dig three blocks deep, go inside the hole and execute `/spawnpoint`, fill the hole up again and execute `/kill`: you will respawn in a spawn chunk.

   ![Command Block chain example](https://raw.githubusercontent.com/xMamo/VanillaVotifier/master/example.png)

   Starting from the Repeating Command Block, insert the following commands:
   1. `tellraw @a[score_voted_min=1] {"text":"Thanks for voting for our server! Here's your diamond!","color":"green"}` (Repeat, Always Active);
   2. `give @a[score_voted_min=1] minecraft:diamond` (Chain, Always Active);
   3. `scoreboard players remove @a[score_voted_min=1] voted 1` (Chain, Always Active);
   4. `scoreboard players reset @a[score_voted=0] voted` (Chain, Always Active).

3. In the `config.yaml`, change the `on-vote` section to:
   ```YAML
   on-vote:
     - action: 'rcon'
       server:
         ip: <RCon IP>
         port: <RCon port>
         password: <RCon password>
       commands:
         - 'scoreboard players add ${user-name} voted 1'
       regex-replace: {}
   ```

4. If VanillaVotifier is already running, reload the configuration using using VanillaVotifier's `restart` command; otherwise, just start up VanillaVotifier.

#### `shell` action example ####
This example aims to show exactly what the previous example did, but using a shell script instead of sending RCon commands. It is assumed that Linux is being used as OS, that the Minecraft server and Votifier are running on the same machine, and that the Minecraft server is running using `screen` (more precisely, that it starts up using something like `screen -dmS <screen name> java -jar minecraft_server.jar`). Here's how to do it:

1. Add a `voted` dummy score and create a Command Block clock to deal with votes, as explained in the steps 1–2 of the previous example.

2. Create bash file called `onvote.sh` in the same directory as `config.yaml`. Copy the following code in the newly created file:
   ```Shell
   #!/bin/bash
   screen -S <screen name> -X stuff 'scoreboard players add $voteUserName voted 1
   '
   ```

3. In the `config.yaml`, change the `on-vote` section to:
   ```YAML
   on-vote:
     - action: 'shell'
       commands:
         - './onvote.sh'
       regex-replace: {}
   ```

4. If VanillaVotifier is already running, reload the configuration using VanillaVotifier's `restart` command; otherwise, just start up VanillaVotifier.
