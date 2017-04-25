# TopHat - Monopoly Bot by Brian O'Leary, Daniel Graham, Conal O'Neill
## Bot algorithm description
### Rolling
Our bot contains a private boolean 'hasRolled' used for checking of we have already rolled this turn. If we havn't rolled yet, then roll and update our boolean. Otherwise continue through our algorithm.

### Buying
To decide whether to buy an unowned property we land on, we first check our balance to see if we can afford it. We also make sure that it isn't a utility as our strategy is to avoid these properties due to their low return on investment. We have 4 tiers of our bank balance to decide how much money we are willing to spend on a property. These are 300 - 500, 500 - 850, 850 - 1100 and > 1100. If we have greater than 1100, then we will buy any property. We then have further price restrictions depending on what tier of balance we are on. We will always buy station properties regardless of balance tier, as these have the best return on investment in the game.

### Houses and Hotels
Our strategy for building houses is to always aim to have 3 houses as this proves the best return on investment as shown by the following chart: ![alt text](http://www.amnesta.net/monopoly/map_1.gif)
   This strategy and chart was found at http://www.amnesta.net/monopoly/. We found this was a very thorough study of monopoly and so chose to follow elements of this strategy. If our balance is greater than 2500 then we will build more than 3 buildings.
   
### Getting out of negative balance
Our first method to get out of negative balance is to demolish any buildings we have as this will disrupt our income the least. Our next strategy is to check if we own any single property that we could mortgage to get out of debt. Our final resort is to begin mortgaging all of our properties starting at the cheapest and cycling through until we either have a positive balance or no more property to mortgage. If we have no more properties to mortgage and still have a negative balance, we will declare bankruptcy.

### Redeeming mortgaged properties
Our strategy for redeeming property is to simply redeem any property that we can afford with at least 100 spare. We always aim to avoid having balance below 100 as this allows us to avoid trivial fines or rents which would otherwise render us bankrupt.

### In Jail
When in jail, we first check if we have a 'Get out of Jail' card and if so then we use it. We next check if our balance is greater than 300 and if so, we pay the 50 fine and get out of jail. This is to minimise our jail time early in the game allowing us more chances to buy properties ahead of other players. If we fail the first two checks, then we roll for doubles to get out.

### Fine or Chance Decision
To decide whether or not to pay the fine or take a chance card when given the choice, we first calculate the worst case scenario for taking a chance card. If we can afford to pay the worst case scenario then we take the chance option. The worst case scenario is found by using our number of houses and hotels as the 'street repairs' chance card will cost the most.
