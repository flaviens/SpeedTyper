var http = require('http');
var url = require('url');

var lobbies = [];

// TODO Verifier periodiquement que les gens sont encore dans les lobbies (en gros les gens doivent rappeler qu'ils sont bien la toutes les 500ms par exemple).

class Player {
	constructor(ip, name) {
		this.ip = ip;
		this.name = name;
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

var server = http.createServer(function(req, res) {
	var page = url.parse(req.url).pathname;

	var pageElems = page.split("/").slice(1);

	console.log(lobbies);

	if(pageElems[0] == "lobbies") {
		res.writeHead(200);
		res.end(Object.keys(lobbies).join());
	}
	else if(pageElems[0] == "joinlobby" && pageElems.length > 2) {
		if(pageElems[1] in lobbies) { // Si le lobby existe deja
			if(!containsIp(lobbies[pageElems[1]], req.connection.remoteAddress)) {//lobbies[pageElems[1]].indexOf(req.connection.remoteAddress) == -1) {
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
			res.writeHead(400);
			res.end("Error: lobby does not exist.");
		}
		res.writeHead(400);
		res.end("Error: was not in lobby.");
	}
	else if(pageElems[0] == "getlobbymembers" && pageElems.length > 1) {

	}
	else if(pageElems[0] == "quitLobby" && pageElems.length > 1) {
		lobbies.push(pageElems[1]);

		res.writeHead(200);
		res.end("Success.");
	}
	else {
		res.writeHead(200);
		res.end('Bienvenue.');
	}
});
server.listen(8080);