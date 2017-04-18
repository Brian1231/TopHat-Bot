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
	private String tileName = "";
	
	private String[] shortNames = {
			null, "kent", null, "whitechapel", null , "kings", "angel", null, "euston", "pentonville", 
			null, "mall", "electric", "whitehall", "northumberland", "marylebone", "bow", null, "marlborough", "vine", 
			null, "strand", null, "fleet", "trafalgar", "fenchurch", "leicester", "coventry", "water", "piccadilly", 
			null, "regent", "oxford", null, "bond", "liverpool", null, "park", null, "mayfair"};
	
	
	public String getName () {
		return "TopHat";
	}

	public String getCommand () {

		//Slow down game
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Update our data
		balance = player.getBalance();
		position = player.getPosition();
		System.out.println(player.getTokenName() + " : " + balance);

		//Roll if we havn't already
		if(!hasRolled){

			hasRolled = true;
			return "roll";
		}
		//After we've rolled ...
		else{
			//Get current tiles short-name
			tileName = shortNames[position];
			//On a property, station or utility
			if (board.isProperty(position)) {
				Property p = board.getProperty(position);
				//Property is un-owned and not a utility
				if (!p.isOwned() && !board.isUtility(tileName)) {
					
					//Check balance is greater tan tile price
					if(balance < p.getPrice()){
						hasRolled = false;
						return "done";
					}

					if(balance >= 300){
						if(balance > 500){
							if(balance > 850){
								if(balance > 1100){
									// > 1100
									return "buy";
								}
								//850 - 1100
								else if(board.isStation(tileName) || (p.getPrice() >= 100 && p.getPrice() <= 280))return "buy";
							}
							//500 - 850
							else if(board.isStation(tileName) || (p.getPrice() >= 100 && p.getPrice() <= 200))return "buy";

						}
						//300 - 500
						else if(board.isStation(tileName) || (p.getPrice() >= 100 && p.getPrice() <= 120))return "buy";

					}
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
