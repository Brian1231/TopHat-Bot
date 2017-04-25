# TopHat Bot Monopoly Bot by Brian O'Leary, Daniel Graham, Conal O'Neill
## Bot algorithm description
### Rolling
Our bot contains a private boolean 'hasRolled' used for checking of we have already rolled this turn. If we havn't yet rolled, then roll and update our boolean. Otherwise continue through our algorithm.

### Buying
To decide whether to buy an unowned property we land on, we first check our balance to see if we can afford it. We also make sure that it isn't a utility as our strategy is to avoid these properties due to their low return on investment. We have 4 tiers of balance to decide how much money we are willing to spend on a property. These are 300 - 500, 500 - 850, 850 - 1100 and > 1100. If we have greater than 1100, then we will buy any property. We have further price restrictions depending on what tier of balance we are on. We will always buy station properties regardless of balance tier as these have the best return of investment in the game.

### Houses and Hotels
Our strategy for building houses is to always aim to have 3 houses as this proves the best return on investment as shown by the following chart: ![alt text](http://www.amnesta.net/monopoly/map_1.gif)
   This strategy and chart was found at http://www.amnesta.net/monopoly/. We found this was a very thorough study of monopoly and so chose to follow elements of this strategy.
