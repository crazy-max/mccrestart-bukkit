# MCCRestart [![Download](https://img.shields.io/badge/download-1.2.5-brightgreen.svg)](https://github.com/crazy-max/mccrestart-bukkit/releases/download/1.2.5/MCCRestart.jar) [![Donate Paypal](https://img.shields.io/badge/donate-paypal-blue.svg)](https://www.paypal.me/crazyws)

> More info on [Bukkit](https://bukkit.org/threads/inactive-admn-mccrestart-v1-2-5-scheduled-and-delayed-server-restarts-677.12165).

![](https://raw.githubusercontent.com/crazy-max/mccrestart-bukkit/master/resources/mccrestart.png)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [About](#about)
- [Features](#features)
- [OS Compatibility](#os-compatibility)
- [Commands](#commands)
- [Permission-nodes](#permission-nodes)
- [config.yml example](#configyml-example)
- [messages.yml example](#messagesyml-example)
- [Issues](#issues)
- [TODO](#todo)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## About

**MCCRestart** is a tool to schedule server restarts. It saves player and chunk data before each restarts at specified times or delays and gives a warning message to each players before restart. You can also give a reason for the restart. (see commands section)<br />
If a problem occurs during restarts, post the **mccrestart.log** in a new issue.<br />

In your config.yml file you can specify a batch file for the launch parameter. Example :

```batch
@echo off

set memory=512

if /I "%PROCESSOR_ARCHITECTURE:~-2%"=="64" "%ProgramFiles(x86)%\Java\jre6\bin\java.exe" -Xms%memory%M -Xmx%memory%M -jar "%~dp0craftbukkit.jar"
if /I "%PROCESSOR_ARCHITECTURE:~-2%"=="86" java -Xms%memory%M -Xmx%memory%M -jar "%~dp0craftbukkit.jar"
```

If the Permissions plugin is not detected, only OPs can use restart and mccrestart commands.

## Features

* Restarts server at specified time (**24-hour format**)
* Or at delayed time (**24-hour format**)
* Add a manual restart with a delay and/or a reason for.
* Cancel a manual restart.
* Warning message to players before restart.
* Saves player and chunk data.
* Multi-world compatible.
* Add delay between stop and restart server.
* Use **config.yml** and **messages.yml** to configure the plugin.
* Don't modify the paramaters **{n}** in each YAML files.
* Automatically creates config.yml and messages.yml in the plugin folder.

## OS Compatibility

* Windows XP, Vista, 7 (x86 and x64 editions)

## Commands

* **/mccrestart help** - List MCCRestart commands
* **/mccrestart reload** - Reload the plugin configuration
* **/mccrestart next** - Give the next time to restart
* **/restart** - Restart the server immediately
* **/restart 59:59** - Restart after a delay (minutes:seconds)
* **/restart 59:59 reason** - Restart after a delay with a reason
* **/restart cancel** - Cancel a manual restart

## Permission-nodes

* mccrestart.use

## config.yml example

```
config:
    # Enable/disable plugin
    enable: 'true'
 
    # Active/deactivate restart at times or delay (true or false)
    autorestart: 'true'
 
    # Args to launch the server (must be placed in your root server folder)
    launcher: 'java -Xms512M -Xmx512M -jar craftbukkit.jar'
    # If you want to launch a batch file (must be placed in your root server folder)
    #launcher: 'run.bat'
 
    # How many seconds before restart to show warning message to players. Separate seconds with a comma.
    warn: '30,10'
 
    # Scheduled times when the server is restarting. Use 24-hour time and separate times with a comma.
    times: '12:00:00,23:00:00'
 
    # Delay time between each restart. Use 24-hour time.
    delay: '02:00:00'
 
    # Choose type of restart (times or delay)
    type: 'delay'
 
    # Delay between stop and restart server if you have a lot of plugins (use minutes:secondes)
    stoptime: '00:10'
```

## messages.yml example

```
messages:
    warn: 'The server is being restarted...'
    warnTime: 'The server restarts in {0} seconds...'
    restart: 'The server is restarting...'
    disabled: 'MCCRestart is disabled'
    reload: 'MCCRestart reloaded'
    next: 'Next restart scheduled at {0}'
    reason: 'Reason: {0}'
    cancel: 'Manual restart cancelled'
    norestart: 'No restart is scheduled'
```

## Issues

Please submit a complete bug report with :
- OS where the server is deployed.
- Bukkit version.
- Submit the mccrestart.log if there and the server.log.

## TODO

* [ ] Add OSX/Linux compatibility

## License

MIT. See `LICENSE` for more details.
