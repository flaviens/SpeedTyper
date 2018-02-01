var http = require('http');
var url = require('url');

var lobbies = {};
var games  = {};

var forbiddenNames = [];

const countdown = 15000;
const gameDuration = 60000; // TODO

// TODO gérer la fonction leave en jeu.

class Player {
	constructor(ip, name) {
		this.ip = ip;
		this.name = name;
		this.score = 0;
		this.ready = false;
		// ready has 2 usefulnesses:
		//  - In the lobby, to express readiness.
		//	- In game, to express that the client has finished the session.
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
}

function shouldCreateGame(lobbyName) {
	for(playerId in lobbies[lobbyName]) {
		console.log(lobbies[lobbyName][playerId]);
		if(!lobbies[lobbyName][playerId].ready)
			return false;
	}
	return true;
}

function getRemainingTime(gameName) {
	return games[gameName]["creationTime"] + countdown - Date.now();
}

var server = http.createServer(function(req, res) {
	var page = url.parse(req.url).pathname;

	var pageElems = page.split("/").slice(1);

	console.log(lobbies);

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
			res.writeHead(200);
			res.end("Created.");
		}		
	}
	else if(pageElems[0] == "ready" && pageElems.length > 1) { // "ready"/lobby
		if(pageElems[1] in lobbies) {
			if(containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {
				var index = ipIndex(lobbies[pageElems[1]], req.connection.remoteAddress);
				
				console.log(lobbies[pageElems[1]][index]);
				if(lobbies[pageElems[1]][index].ready) {
					res.writeHead(200);
					res.end("Warning: already ready.");
				}
				else {
					lobbies[pageElems[1]][index].ready = true;

					if(shouldCreateGame(pageElems[1])) {
						createGame(pageElems[1]);
						console.log("Creating game");
					}
					else
						console.log("Not creating game");

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
				
				console.log(lobbies[pageElems[1]][index]);
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

	else if(pageElems[0] == "leavelobby" && pageElems.length > 1) {
		if(pageElems[1] in lobbies) {
			var index = ipIndex(lobbies[pageElems[1]], req.connection.remoteAddress);

			if(index == -1) {
				res.writeHead(400);
				res.end("Error: was not in lobby.");
			}
			else {
				lobbies[pageElems[1]].splice(index, 1);
				if(lobbies[pageElems[1]].length == 0)
					delete lobbies[pageElems[1]];
				res.writeHead(200);
				res.end("Success.");
			}			
		}
		else {
			if(pageElems[1] in games) {
				res.writeHead(400);
				res.end("Error: lobby already playing."); // TODO
			}
			else {
				res.writeHead(400);
				res.end("Error: lobby does not exist.");
			}
		}
		res.writeHead(400);
		res.end("Error: was not in lobby.");
	}
	else if(pageElems[0] == "game" && pageElems.length > 1) {
		if(pageElems[1] in games) {
			games[pageElems[1]]["remainingTime"] = getRemainingTime(pageElems[1]);
			res.writeHead(200, {"Content-Type": "application/json"});
			res.end(JSON.stringify(games[pageElems[1]]));
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
	else if(pageElems[0] == "updatescore" && pageElems.length > 2) { // "updatescore"/"game"/score
		if(pageElems[1] in games) { // TODO Regarder si la partie est terminee et si elle a deja commencé
			index = ipIndex(games[pageElems[1]], req.connection.remoteAddress);
			games[pageElems[1]]["players"][index] = pageElems[2];
			res.writeHead(200);
			res.end("Success");
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
		res.end('Bienvenue.');
	}
});
server.listen(8080);