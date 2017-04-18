public class TopHat implements Bot {
	
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects

	
	TopHat (BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board = board;
		this.player = player;
		this.dice = dice;
	}
	
	private BoardAPI board;
	private PlayerAPI player;
	private DiceAPI dice;
	
	
	public String getName () {
		
		return "TopHat";
	}

	public String getCommand () {
		
		

		// Add your code here
		return "roll";
	}
	
	public String getDecision () {
		// Add your code here
		return "pay";
	}
	
}
