public class TopHat implements Bot {
	
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects

	
	TopHat (BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board = board;
		this.player = player;
		this.dice = dice;
		this.hasRolled = false;
	}
	
	private BoardAPI board;
	private PlayerAPI player;
	private DiceAPI dice;
	
	private boolean hasRolled;
	private int balance;
	private int position;
	
	public String getName () {
		
		return "TopHat";
	}

	public String getCommand () {
		System.out.println(player.getTokenName() + " , " + balance);
		//Slow down game
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Update our data
		balance = player.getBalance();
		position = player.getPosition();
		
		//Roll if we havn't already
		if(!hasRolled){
			hasRolled = true;
			return "roll";
		}
		//After we've rolled ...
		else{
			if (board.isProperty(position)) {
				//On a property
				Property p = board.getProperty(position);
				if (!p.isOwned()) {

					return "buy";
				}
			}
		}
		hasRolled = false;
		return "done";
		
	}
	
	public String getDecision () {
		// Add your code here
		return "pay";
	}
	
}
