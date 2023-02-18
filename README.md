Discord-TCSBuf
======
 
## About
 
A discord music playing bot, that can control a queue of songs, and play music for you in your discord server. Supports slash commands, and is very simple to use.
 
## Public Instance
 
If you don't wish to setup the bot yourself, you can added it [here](https://tylerm.dev/henry).
 
## Installation
 
This bot needs a [Discord API token](https://discord.com/developers/) and a [Youtube API token](https://console.cloud.google.com/apis/dashboard) to run. To compile make sure you have [Maven 3](https://maven.apache.org/), and [Java](https://www.java.com/en/) installed.
 
To comple the bot run
```sh
mvn clean install
```
and that should create a jar file in a target foler.
 
## Setup
 
The first time you run the jar file, it will create a config folder called `config`. In that folder it will create a file called `config.yml`, and it will look something like this:
 
```yaml
botToken: ""
youtubeAPIKey: ""
```
Place your Discord API token and Youtube API token in there, and then run the bot again. Everything should now work!
 
