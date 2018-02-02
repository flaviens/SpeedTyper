package game;

import java.util.Comparator;

public class Player {
	public static class PlayerComparator implements Comparator<Player> {
		@Override
		public int compare(Player o1, Player o2) {
			if(o1.score > o2.score)
				return -1;
			return 1;
		}
	}
	
	public String name;
	public String ip;
	public boolean ready;
	public int score;
}
