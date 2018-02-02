var http = require('http');
var url = require('url');
var fs = require('fs');

const fileName = "words.txt";
var fileContent = "";
const carriageReturn = "\r\n"

var lobbies = {};
var games  = {};
var dictionaries = {}; // Will contain csv words that the players will have to type

var forbiddenNames = [];

const countdown = 5000; // Parameters
const gameDuration = 60000;
const connectionTimeout = 5000;
const inactivityCheckInterval = 1000;

var nextInactiveCheckTimeStamp = 0;

class Player {
	constructor(ip, name) {
		this.ip = ip;
		this.name = name;
		this.score = 0;
		this.ready = false;
		this.lastConnection = Date.now();
		// ready has 2 uses:
		//  - In the lobby, to express readiness.
		//	- In game, to express that the client has finished the session.
	}
}

function genRandInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function readWordsFromFile() {
	fs.readFile(fileName, 'utf-8', function(err, data) {
		if(err) throw err;
		fileContent = data.split('\r\n');
	});
}

function generateWordList(lobbyName) {
	dictionaries[lobbyName] = [];
	for(var i = 0; i < gameDuration/10; i++) { // Suppose no one will type more than one word every 10ms.
		dictionaries[lobbyName].push(fileContent[genRandInt(0, fileContent.length-1)]);
	}
}

function containsIp(t, ip) {
	var contient = false;
	t.forEach(function(element) {
		if(element.ip === ip) {
			contient = true;
			return;
		}
	});
	return contient;
}

function ipIndex(t, ip) {
	var index = -1;
	var tempIndex = -1;
	t.forEach(function(element) {
		tempIndex++;
		if(element.ip === ip) {
			index = tempIndex
			return;
		}
	});
	return index;
}

function createGame(lobbyName) {
	games[lobbyName] = {};
	games[lobbyName]["players"] = lobbies[lobbyName];
	games[lobbyName]["creationTime"] = Date.now();
	games[lobbyName]["remainingTime"] = Date.now();
	games[lobbyName]["duration"] = gameDuration;
	delete lobbies[lobbyName];
	delete dictionaries[lobbyName];
}

function shouldCreateGame(lobbyName) {
	for(playerId in lobbies[lobbyName]) {
		if(!lobbies[lobbyName][playerId].ready)
			return false;
	}
	return true;
}

function getRemainingTime(gameName) {
	return games[gameName]["creationTime"] + countdown - Date.now();
}

function shouldCheckInactive() {
	if(nextInactiveCheckTimeStamp <= Date.now()) {
		nextInactiveCheckTimeStamp = Date.now() + inactivityCheckInterval;
		return true;
	}
	return false;
}

// -------------------------------------------------------
// 					Initialization
// -------------------------------------------------------

readWordsFromFile();

// -------------------------------------------------------
// 					Node.js server
// -------------------------------------------------------


var server = http.createServer(function(req, res) {
	var page = url.parse(req.url).pathname;

	var pageElems = page.split("/").slice(1);



	// Check for inactive players in lobbies and games

	if(shouldCheckInactive()) {
		Object.keys(lobbies).map(function(lobby, index, object) {
			Object.keys(lobbies[lobby]).forEach(function(player) {
				if(!(lobbies[lobby][player] === undefined) && lobbies[lobby][player].lastConnection + connectionTimeout <= Date.now())
					lobbies[lobby].splice(player, 1);
			});
			if(lobbies[lobby].length == 0)
				delete lobbies[lobby];
		});
		Object.keys(games).map(function(game, index, object) {
			games[game].players.forEach(function(player) {
				if(player.lastConnection + connectionTimeout <= Date.now())
					games[game].players.splice(player, 1);
			});
			if(games[game].players.length == 0)
				delete games[game];
		});
	}



	// Manage requests

	if(pageElems[0] == "lobbies") {
		res.writeHead(200, {"Content-Type": "application/json"});
		res.end(JSON.stringify(lobbies));
	}
	if(pageElems[0] == "games") {
		res.writeHead(200, {"Content-Type": "application/json"});
		res.end(JSON.stringify(games));
	}
	else if(pageElems[0] == "joinlobby" && pageElems.length > 2) {
		if(pageElems[1] in games) {
			res.writeHead(400);
			res.end("Error: lobby already playing.");
		}
		else if(pageElems[1] in lobbies) { // Si le lobby existe deja
			if(!containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {
				lobbies[pageElems[1]].push(new Player(req.connection.remoteAddress, pageElems[2]));
				res.writeHead(200);
				res.end("Joined.");
			}
			else {
				res.writeHead(400);
				res.end("Warning: already in lobby.");
			}
		}
		else {
			lobbies[pageElems[1]] = [new Player(req.connection.remoteAddress, pageElems[2])];
			generateWordList(pageElems[1]);
			res.writeHead(200);
			res.end("Created.");
		}		
	}
	else if(pageElems[0] == "words" && pageElems.length > 1) { // "words"/lobby
		if(pageElems[1] in lobbies) {
			if(containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {
				res.writeHead(200);
				res.end(dictionaries[pageElems[1]].join());
			}
			else {
				res.writeHead(400);
				res.end("Error: client not in lobby.");
			}
		}
		else {
			if(pageElems[1] in games) {
				res.writeHead(400);
				res.end("Error: lobby already playing.");
			}
			else {
				res.writeHead(400);
				res.end("Error: lobby does not exist.");
			}
		}
	}
	else if(pageElems[0] == "stillinlobby" && pageElems.length > 1) { // "stillinlobby"/lobby
		if(pageElems[1] in lobbies) {
			if(containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {
				var index = ipIndex(lobbies[pageElems[1]], req.connection.remoteAddress);

				lobbies[pageElems[1]][index].lastConnection = Date.now();
				res.writeHead(200);
				res.end("Success.");
			}
			else {
				res.writeHead(400);
				res.end("Error: client not in lobby.");
			}
		}
		else if(pageElems[1] in games) {
			if(containsIp(games[pageElems[1]].players, req.connection.remoteAddress)) {
				var index = ipIndex(games[pageElems[1]]["players"], req.connection.remoteAddress);

				games[pageElems[1]]["players"][index].lastConnection = Date.now();
				res.writeHead(200);
				res.end("Success.");
			}
			else {
				res.writeHead(400);
				res.end("Error: client not in lobby.");
			}
		}
		else {
			if(pageElems[1] in games) {
				res.writeHead(400);
				res.end("Error: lobby already playing.");
			}
			else {
				res.writeHead(400);
				res.end("Error: lobby does not exist.");
			}
		}
	}
	else if(pageElems[0] == "ready" && pageElems.length > 1) { // "ready"/lobby
		if(pageElems[1] in lobbies) {
			if(containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {
				var index = ipIndex(lobbies[pageElems[1]], req.connection.remoteAddress);

				if(lobbies[pageElems[1]][index].ready) {
					res.writeHead(200);
					res.end("Warning: already ready.");
				}
				else {
					lobbies[pageElems[1]][index].ready = true;

					if(shouldCreateGame(pageElems[1]))
						createGame(pageElems[1]);


					res.writeHead(200);
					res.end("Success.");
				}
			}
			else {
				res.writeHead(400);
				res.end("Error: client not in lobby.");
			}
		}
		else {
			if(pageElems[1] in games) {
				res.writeHead(400);
				res.end("Error: lobby already playing.");
			}
			else {
				res.writeHead(400);
				res.end("Error: lobby does not exist.");
			}
		}
	}
	else if(pageElems[0] == "notready" && pageElems.length > 1) { // "ready"/lobby
		if(pageElems[1] in lobbies) {
			if(containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {
				var index = ipIndex(lobbies[pageElems[1]], req.connection.remoteAddress);
				
				if(!lobbies[pageElems[1]][index].ready) {
					res.writeHead(200);
					res.end("Warning: already not ready.");
				}
				else {
					lobbies[pageElems[1]][index].ready = false;
					res.writeHead(200);
					res.end("Success.");
				}
			}
			else {
				res.writeHead(400);
				res.end("Error: client not in lobby.");
			}
		}
		else {
			if(pageElems[1] in games) {
				res.writeHead(400);
				res.end("Error: lobby already playing.");
			}
			else {
				res.writeHead(400);
				res.end("Error: lobby does not exist.");
			}
		}
	}

	else if(pageElems[0] == "leave" && pageElems.length > 1) {
		if(pageElems[1] in lobbies) {
			var index = ipIndex(lobbies[pageElems[1]], req.connection.remoteAddress);

			if(index == -1) {
				res.writeHead(400);
				res.end("Error: was not in lobby.");
			}
			else {
				lobbies[pageElems[1]].splice(index, 1);
				if(lobbies[pageElems[1]].length == 0) {
					delete lobbies[pageElems[1]];
					delete dictionaries[pageElems[1]];
				}
				res.writeHead(200);
				res.end("Success.");
			}			
		}
		else if(pageElems[1] in games) {
			var index = ipIndex(games[pageElems[1]]["players"], req.connection.remoteAddress);
			if(index == -1) {
				res.writeHead(400);
				res.end("Error: was not in game.");
			}
			else {
				games[pageElems[1]]["players"].splice(index, 1);
				if(games[pageElems[1]]["players"].length == 0)
					delete games[pageElems[1]];
				res.writeHead(200);
				res.end("Success.");
			}			

		}
		else {
			res.writeHead(400);
			res.end("Error: lobby or game does not exist.");
		}
	}
	else if(pageElems[0] == "game" && pageElems.length > 1) {
		if(pageElems[1] in games) {
			games[pageElems[1]]["remainingTime"] = getRemainingTime(pageElems[1]);
			res.writeHead(200, {"Content-Type": "application/json"});
			res.end(JSON.stringify(games[pageElems[1]]));
		}
		else {
			if(pageElems[1] in lobbies) {
				res.writeHead(200);
				res.end("{}");
			}
			else {
				res.writeHead(400);
				res.end("Error: game does not exist.");
			}
		}
	}
	else if(pageElems[0] == "updatescore" && pageElems.length > 2) { // "updatescore"/"game"/score
		if(pageElems[1] in games) { // TODO Regarder si la partie est terminee et si elle a deja commencé
			index = ipIndex(games[pageElems[1]]["players"], req.connection.remoteAddress);
			
			if(index == -1) {
				res.writeHead(400);
				res.end("Error: Player not in game.");
			}
			else {
				games[pageElems[1]]["players"][index].score = pageElems[2];
				res.writeHead(200);
				res.end("Success");
			}
		}
		else {
			if(pageElems[1] in lobbies) {
				res.writeHead(400);
				res.end("Error: still in lobby.");
			}
			else {
				res.writeHead(400);
				res.end("Error: game does not exist.");
			}
		}
	}

	else {
		res.writeHead(200);
		res.end('Ping');
	}
});
server.listen(8080);