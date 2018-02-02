# HTTP server for SpeedTyper
### @Author Flavien Solt X2016

This is a server to manage parallel games of SpeedTyper.
Players are spread in lobbies. Once a player joins the lobby, the client has to download the associated word list. Once this done and the player ready, the client can signal itself ready.
Once all players are ready, the lobby is destroyed and the game is created. The game begins after the countdown has finished
Warning: once all players are ready, the word list is not available on the server anymore.
The game is destroyed once all players have left.



### To get the JSON list of all lobbies and of all the players inside
/lobbies

#####result example:
{
  "Amazing_lobby": [
    {
      "ip": "::1",
      "name": "Arthur",
      "score": 0,
      "ready": false
    }
  ],
  "Also_decent_lobby_try_this_one": [
    {
      "ip": "::2",
      "name": "Flavien",
      "score": 0,
      "ready": false
    },
    {
      "ip": "::3",
      "name": "PJ",
      "score": 0,
      "ready": true
    }
  ]
}


### To join or create a lobby (a lobby cannot have the name of a game currently playing)
/joinlobby/<lobbyname>/<yourname>



### To get the list of words associated to a lobby (you must have joined the lobby first)
/words/<lobbyname>

#####result example:
threshold,boxes,sad,donald,remind



### To leave a lobby or a game
/leave/<lobbyname>



### To get information about the current game (where <gamename> is the lobby name as well) (the answer is {} if the game is not created yet)
/game/<gamename>

##### Result example:
{
  "players": [
    {
      "ip": "::1",
      "name": "Flavien",
      "score": 0,
      "ready": true
    }
    {
      "ip": "::2",
      "name": "Arthur",
      "score": 1000,
      "ready": true
    }

  ],
  "creationTime": 1517489406481,
  "remainingTime": 12654, // remainingtime gives the time in milliseconds until the game begins
  "duration": 60000 // duration gives the game duration in milliseconds
}


### To update your score (to make it visible to the others, so do it regularly during the game)
/updatescore/<gamename>/<newscore>



### To stay in lobby or in game (after 5 seconds without this, you can get kicked automatically)
/stillinlobby/<lobbyname>


