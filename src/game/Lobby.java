package game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Lobby {
	private static class LobbyComparator implements Comparator<Lobby> {
		@Override
		public int compare(Lobby o1, Lobby o2) {
			if(o1.players.size() < o2.players.size())
				return -1;
			return 1;
		}
	}
	
	public ArrayList<Player> players;
	public String name;
	
	// Only the LobbiesRefresher will acces this PriorityQueue
	public static PriorityQueue<Lobby> lobbies = new PriorityQueue<Lobby>(new LobbyComparator());
	
	public Lobby(String name) {
		this.name = name;
		players = new ArrayList<Player>();
	}
}
