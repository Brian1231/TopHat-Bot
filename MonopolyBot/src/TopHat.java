public class TopHat implements Bot {

	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects


	TopHat (BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board = board;
		this.player = player;
		this.hasRolled = false;
	}

	private BoardAPI board;
	private PlayerAPI player;

	private boolean hasRolled;
	private int balance;
	private int assets;
	private int position;
	private String tileName = "";
	private int turnCount = 0;


	public String getName () {
		return "TopHat";
	}

	public String getCommand () {

		//Slow down game
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Update our data
		balance = player.getBalance();
		position = player.getPosition();
		assets = player.getAssets();
		System.out.println(player.getTokenName() + " balance: " + balance + ", assets: " + assets);

		//If we're in Jail
		if(player.isInJail()){
			if(player.hasGetOutOfJailCard())
				return "card";
			else if(balance > 300) 
				return "pay";
			else if(!hasRolled){
				hasRolled = true;
				return "roll";
			}

		}

		//Roll if we havn't already
		if(!hasRolled){

			hasRolled = true;
			return "roll";
		}

		//After we've rolled ...
		else{
			//Buying property
			//On a property, station or utility
			if (board.isProperty(position)) {
				Property p = board.getProperty(position);
				//Property is un-owned and not a utility and check balance is greater than property price
				if (!p.isOwned() && !board.isUtility(tileName) && balance > p.getPrice()) {

					//Price restrictions
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


		if (player.getBalance() > 100) {
			//Buildings
			//Search for fully owned color groups
			for (Property p : player.getProperties()) {
				Site site = null;
				try {

					//Check if p is a site and we own all the color group
					site = (Site) p;//Current site we are checking
					int numberInColorGroup = site.getColourGroup().size();//Number of sites with this color
					int numberOfColourOwned = 0; //Count sites we own with the same color
					//Count property owned in color group
					for (Property prop : player.getProperties()) {
						try {
							Site site2 = (Site) prop;
							if (site2.getColourGroup().equals(site.getColourGroup()))
								numberOfColourOwned++;
						} catch (Exception e) {
						}
					}
					//If we own the full color group
					//Strategy is to always aim for 3 houses
					if (numberOfColourOwned == numberInColorGroup) {
						for (Site siteInColourGroup : site.getColourGroup().getMembers()) {
							//System.out.println(siteInColourGroup.getName() + " , has: " + siteInColourGroup.getNumBuildings());
							int currentBuildings = siteInColourGroup.getNumBuildings();//Current number of buildings on site
							int housePrice = siteInColourGroup.getBuildingPrice(); //Building cost
							int maxToBuild = (balance - 100) / housePrice;// Number of houses we can afford with 100 spare
							int buildsNeeded = 3 - currentBuildings;//Buildings needed to reach goal of 3

							//Get short name of site
							String siteShortName = siteInColourGroup.getShortName();
							//System.out.println(site.getName() + " , " + siteShortName);


							if (!site.isMortgaged()) {
								//Build 3 houses or less
								if (maxToBuild == 0 || buildsNeeded == 0) {
									continue;
								}
								else{
									if (maxToBuild <= buildsNeeded) {
										System.out.println(player.getTokenName() + " built " + maxToBuild + " on "
												+ siteShortName);
										return "build " + siteShortName + " " + maxToBuild;
									} else {
										System.out.println(player.getTokenName() + " built " + buildsNeeded + " on "
												+ siteShortName);
										return "build " + siteShortName + " " + buildsNeeded;
									} 
								}
							} 
						} 
					}

				}catch (Exception e) {
				}
			} 
		}
		//Get out of negative balance
		balance = player.getBalance();
		while(balance < 0){
			int debt = Math.abs(balance);//Our debt we need to make up

			//Check for bankruptcy
			int unmortgagedCount = 0;
			for(Property p : player.getProperties()){
				if(!p.isMortgaged()){
					unmortgagedCount++;
				}	
			}
			balance = player.getBalance();
			if (unmortgagedCount == 0 && balance < 0) {
				return "bankrupt";
			}


			//First demolish buildings, 1 at a time
			for(Property p : player.getProperties()){
				Site site = null;
				try {
					site = (Site) p;
					if(site.hasBuildings()){ 
						String name = site.getShortName();
						return "demolish " + name + " 1";
					}
				} catch (Exception e) {}
				balance = player.getBalance();
				if(balance >= 0)break;
			}

			//Mortgaging
			//See if one property will do
			for(Property p : player.getProperties()){
				if(p.getMortgageValue() > debt && !p.isMortgaged()){
					String name = p.getShortName();
					System.out.println(player.getTokenName() + " mortgaged " + name);
					return "mortgage " + name;
				}
			}

			//Mortgage cheapest first
			for(Property p : player.getProperties()){
				if(!p.isMortgaged()){
					String name = p.getShortName();
					System.out.println(player.getTokenName() + " mortgaged " + name);
					return "mortgage " + name;
				}
			}



		}

		//Redeem our mortgaged properties
		for(Property p : player.getProperties()){
			//If p is mortgaged and we can afford to redeem it
			if(p.isMortgaged() && balance > p.getMortgageRemptionPrice() + 100){ 
				String name = p.getShortName();
				System.out.println(player.getTokenName() + " redeemed " + name);
				return "redeem " + name;
			}
		}

		if(turnCount == 1000){
			return "quit";
		}
		turnCount++;
		hasRolled = false;
		return "done";

	}

	public String getDecision () {
		//Update our data
		balance = player.getBalance();
		position = player.getPosition();

		int houses = 0;
		int hotels = 0;
		int fine;
		//Get number of buildings
		for(Property property : player.getProperties()){
			String siteName = property.getShortName();
			if (board.isSite(siteName)) {
				Site site = (Site) property;
				houses += site.getNumHouses();
				hotels += site.getNumHotels();
			}
		}
		fine = houses*40 + hotels*115; //Worst case scenario

		if(balance > fine && balance > 200){
			return "chance";
		}

		else{
			return "pay";
		}

	}

}
