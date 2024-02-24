# Minotaur-Birthday-Party
This includes two programs using multithreading in java that simulate a game involving minotaur inviting people to into his labyrinth or to see his special vase.

# Problem 1
The method I had the guests use in this problem was to have one Chosen guest to count whenever he saw a missing cupcake then replace it. The other guests will enter the labyrinth when told and only eat the cupcake if it is there and they haven't eaten one yet. The other guests will not replace, only the Chosen will replace so they know that specifically one new guest has eaten since he last replaced the cupcake. The program has a verification check at the end to ensure that all visitors went in the maze at least once. The number of visitors can be changed by changing the variable, it is hard-coded to 10, and there are some commented print statements that give a more detailed order of events.

# Problem 2
I chose to implement the third option, as it felt like the most organized way to chose and allow the next visitor to enter the vase room. This way the visitors take the lock only when its their turn and release it once they've left the room, other visitors can still enter the queue while one is viewing and passing off access to the room. There are print statements that print an exact order of events to the log, you can follow the order at which visitors join the queue, view the vase, and pass to the next visitor to verify the process runs correctly. I also added a bit of randomness when it comes to how many times the viewer wants to view the vase, and for how long they feel like waiting before entering queue again, and for how long they want to view the vase just for a little immersion. 
