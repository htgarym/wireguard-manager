# Wireguard Manager
A cli tool for managing a central wireguard server that has multiple peers that connect to it, and gives access to a 10.0.0.0/8 network.
This tool is very much a work in progress and while the database structure supports mutliple servers there are multiple places
in the business logic that assumes only a single server has been set up.

## Features
- **Server Management**: Create and manage a single WireGuard server on interface wg0
- **Peer Management**: Add, configure, and manage WireGuard peers for a single server

## Prerequisites
This has only been tested on an Amazon Linux 2023 server

### Packages
You will need installed on your server to get this to work
- Java 21
- wireguard-tools

## Running

This not a native application and does require the Java Runtime to operate.  This means that you will have to run
`java -jar` for all these command with the `-jar` pointed at the location of the wireguard-manager.jar.  For example
if you have saved the jar to _/usr/local/lib/wireguard-manager.jar_ your command to get the root help message `java -jar /usr/local/lib/wireguard-manager.jar --help`

## Available Commands
* initialize, init - Initialize the database and setup the server.
* peer
  * add **USER** **FOURTH_OCTET** - add a new peer with **USER** being the name of the new user and **FOURTH_OCTET** being an integral value between 2 and 254 inclusive
* render
  * server - render the WireGuard wg0.conf configuration to _/etc/wireguard/wg0.conf_
  * peer - render to the console the peer's configuration
* database, db - gives you a SQL prompt into the database. This is really only useful for trouble shooting bad data.

## Usage Process

You should follow these steps to use this tool.  All commands assume that the wireguard-manager.jar is located at _/usr/local/lib/_
You will also need to run these commands as root or with sudo as the tool is configured to write its files to _/etc/wireguard/_

### Server Initialization

Run `java -jar /usr/local/lib/wireguard-manager.jar initialize` to initialize the database and set up the server.
You will be prompted first for the interface to use and then for the server's public IP address that you want WireGuard to listen on.
This will create the database file _wireguard-manager.db_ in the current directory so you should run this command
from a directory where you want that file to be created.  If you are running this in EC2 it will make an attempt to 
get the public IP address through the metadata service and will use that as the default value.

**THIS ONLY HAS TO BE DONE ONCE UNLESS YOU DELETE THE DATABASE FILE**

### Adding Peers

Run `java -jar /usr/local/lib/wireguard-manager.jar peer add USER FOURTH_OCTET` to add a new peer.

* USER is the name of the new user.  This is used in the peer configuration file.
* FOURTH_OCTET is an integral value between 2 and 254 inclusive that will be used to create the peer's IP address in the peer configurations

### Rendering Configurations

#### Server Configuration

Run `java -jar /usr/local/lib/wireguard-manager.jar render server` to render the server configuration to _/etc/wireguard/wg0.conf_.
This will need to be executed each time a new peer is added to update the server's configuration file.

#### Peer Configuration

Run `java -jar /usr/local/lib/wireguard-manager.jar render peer` to render the peer configuration to the console.  You will be prompted for the user name of the peer you want to render.
This will output the configuration to the console.  You will need to copy this output to a file on the peer device and then start the WireGuard interface on the peer device.

### Starting WireGuard

Once you have rendered the server configuration file to _/etc/wireguard/wg0.conf_ you can start the WireGuard interface with the commands 
```bash
systemctl enable wg-quick@wg0
systemctl start wg-quick@wg0
```

### Stopping WireGuard

You can stop the WireGuard interface with the command
```bash
systemctl stop wg-quick@wg0
```
