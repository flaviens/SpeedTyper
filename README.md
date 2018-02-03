# Real-time multiplayer Speedtyper
### Arthur Toussaint, Flavien Solt, X2016
<https://www.github.com/pepperwind/speedtyper>

## Overview

This is a multiplayer game in which you must type given words faster than your opponents.

## Setup

To play, install node.js, then run the server with the command:

    node server/server.js
You can modify the parameters as the game duration of the game in the server source easily.
There is no limit in the number of clients/lobbies.

## Structure

The server is powered by Node.js and the client is written in Java 8 with the help of the org.json library. The client communicates with the server with HTTP Get requests. It may not be the most efficient way to communicate, but is easy to implement, is inherently language agnostic and gives instantaneous response for a reasonable number of clients.

## Server

The server is written in Node.js and provides an HTTP API. It returns either JSON or plain text, depending on the request. Players are identified by their IP address.
Security was not the main concern, for example any player reading our API documentation could give itself an arbitrary score with the right request at the right time. This can be solved without difficulty, but the concern is outside of the scope of the project.
The server's main services are:
* Clients can test if the server is online (ping).
* Clients can create or join a lobby.
* Clients can leave a lobby.
* Clients can get the list of lobbies and of the players inside.
* Clients can download a word list associated to a lobby.
* Clients can say themselves ready to play.
* Clients can download useful information about the game and upload their score.
* Clients are kicked when they don't give live signs.

A more precise documentation is available in the server folder.

## Client

The client can be in four states, each associated to a different window.

#### Disconnected

![](https://markdown.binets.fr/uploads/upload_446ba8055b6bfe3f1fc62cc8b4af4c88.png)

This state is the base state. When a disconnection is detected, the game returns to that state. When the button connect is pressed, the client checks if the server answers “Ping.” to the request “/”. This happens on UI Thread, providing to the user the feeling that something is happening when the button is being clicked.

If the “connection” is successful (there is no real long-term connection), the URL is being stored in the NetworkManager and the client enters the lobby browser.

#### Lobby browser

![](https://markdown.binets.fr/uploads/upload_7eb85e4d4bf9b3d9a84cf849d33bdd47.png)

To join a lobby, a player must provide a nickname and the name of the lobby. Both strings are being filtered to contain only alphanumeric and ‘_’ characters.

The lobby browser displays in real time the open lobbies (not yet playing), allows the player to join the lobby of his/her choice by clicking on the corresponding row. A new lobby can also be easily created. The only constraint is that the name of the new lobby must not be among the names of the playing games (else there could be two games with the same name, which would lead to a conflict as games are precisely identified by their name).

Two Threads, in addition to the UI Thread, are involved here. One is involved in keeping the lobby list up to date. The second is started when a lobby is being joined. This Thread switches to the Lobby state and then downloads the list of words. Once the list of words is downloaded, the player can press the “ready” button of the following window.
#### Lobby

![](https://markdown.binets.fr/uploads/upload_a87abfaa07057631475caa056d4bace4.png)

In this state, the player can see who is in his lobby, and if they are ready or not. When all players are ready, the game begins.

Three new Threads are required here. One keeps the list of the players in the lobby up to date. The second one periodically tells the server that the player is still in the lobby (else, after five seconds the player can get kicked). The last one periodically checks if the game has started.


#### Game

![](https://markdown.binets.fr/uploads/upload_7e7f9194cce4212eb9fabe6a3f433528.png)

The Game state has three sub-states. First, the countdown. Second, the game in itself, and finally the finished state. During this time, the text field is disabled.

The countdown is provided by the server, which means that the client does not have a clock for the countdown. More precisely, the server provides, among other fields, the remaining time before the game starts in milliseconds, and the game duration.

The game state is pretty straightforward: the user has to type the word in bold font as fast as possible. When the word is recognized, the text field is cleared and the next word (which was on the top of the text pane on the left). To enter in detail, the list of words is stored in an array, and the index of the current word is kept in memory. Each typed word then gives an amount of score and increases the index by one.

The finish state simply disables the text field again. The scores keep updating. As for the countdown, no clock is required in the client: after the game begins, the “remaining time” field given by the server keeps descending negatively. Once it reaches the value -duration, the game stops.







