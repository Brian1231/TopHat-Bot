/*
 *---Tophat---
 * Brian O'Leary - 13475468
 * Conal O'Neill - 13315756
 * Daniel Graham - 15319536
 * 
 * This class is our implementation of our bot.
 * 
 * We have used a strategy outlined in the readme.md file to control the bot. 
 * The bot "issues" command to the game by returning the command in string 
 * format to the main game logic.
 *
 * */

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
	private int position;

	//team name
	public String getName () {
		return "TopHat";
	}

	public String getCommand () {

		//Slow down game, for testing needed for game UI to not be lagging behind. 
		//Remove below try catch, to run game at full speed with no UI or have a lagging UI.
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Update our data
		balance = player.getBalance();
		position = player.getPosition();

		//If we're in Jail
		if(player.isInJail()){
			if(player.hasGetOutOfJailCard())
				return "card";
			//if weve more than 300 balance
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
				if (!p.isOwned() && !board.isUtility(p.getShortName()) && balance > p.getPrice()) {

					//Price restrictions
					if(balance >= 300){
						if(balance > 500){
							if(balance > 850){
								if(balance > 1100){
									// > 1100
									return "buy";
								}
								//850 - 1100
								else if(board.isStation(p.getShortName()) || (p.getPrice() >= 100 && p.getPrice() <= 280))return "buy";
							}
							//500 - 850
							else if(board.isStation(p.getShortName()) || (p.getPrice() >= 100 && p.getPrice() <= 200))return "buy";
						}
						//300 - 500
						else if(board.isStation(p.getShortName()) || (p.getPrice() >= 100 && p.getPrice() <= 120))return "buy";

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

							if (!siteInColourGroup.isMortgaged()) {
								//Build 3 houses or less
								if ((maxToBuild == 0 || buildsNeeded == 0) && balance < 2500) {
									continue;
								}
								else{
									//if we are rich then keep building past our ideal of 3 up to the max of 5 (4 houses + 1 hotel)
									if(balance > 2500 && currentBuildings < 5){
										int buildNum = 5 - currentBuildings;
										//System.out.println(player.getTokenName() + " built " + buildNum + " on " + siteShortName);
										return "build " + siteShortName + " " + buildNum;
									}
									
									//Build as many as we can afford up to 3
									else if (maxToBuild <= buildsNeeded && currentBuildings < 5) {
										//System.out.println(player.getTokenName() + " built " + maxToBuild + " on " + siteShortName);
										return "build " + siteShortName + " " + maxToBuild;
									} 
									
									//if we can afford more than our ideal of 3, then build more up to the max
									else if(currentBuildings < 5 && buildsNeeded > 0){
										//System.out.println(player.getTokenName() + " built " + buildsNeeded + " on " + siteShortName);
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
					//count how many properties are not mortgaged
					unmortgagedCount++;
				}	
			}
			balance = player.getBalance();

			//if we have no properties available to mortgage and still a negative balance then declare bankruptcy
			if (unmortgagedCount == 0 && balance < 0) {
				//System.out.println("Bankrupt");
				return "bankrupt";
			}


			//First demolish buildings, 1 at a time
			for(Property p : player.getProperties()){
				Site site = null;
				try {
					//cast the property to a site variable
					site = (Site) p;
					if(site.hasBuildings()){ 
						String name = site.getShortName();
						//System.out.println(player.getTokenName() + " dem 1 on " + name);
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
					//System.out.println(player.getTokenName() + " mortgaged " + name);
					return "mortgage " + name;
				}
			}

			//Mortgage cheapest first
			for(Property p : player.getProperties()){
				if(!p.isMortgaged()){
					String name = p.getShortName();
					//System.out.println(player.getTokenName() + " mortgaged " + name);
					return "mortgage " + name;
				}
			}
		}

		//Redeem our mortgaged properties
		for(Property p : player.getProperties()){
			//If p is mortgaged and we can afford to redeem it
			if(p.isMortgaged() && balance > p.getMortgageRemptionPrice() + 100){ 
				String name = p.getShortName();
				//System.out.println(player.getTokenName() + " redeemed " + name);
				return "redeem " + name;
			}
		}

		hasRolled = false;
		return "done";

	}

		//used for fine or chance option
	public String getDecision () {


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

		fine = houses*40 + hotels*115; //Worst case scenario (worst fine chance could give us)

		//if we have enough of a balance and we would be left with more than 200 after paying the fine 
		if(balance > fine && (balance - fine) > 200){
			return "chance";
		}

		else{
			return "pay";
		}

	}

}
