REMOVE
======


RE-FACTOR
=========

*  add    Road::isEndpointPair(Flag, Flag) and remove ugly if (.. && ..)   




TEST
====

*  Test that it's possible to build a new house on a destroyed house that is no longer burning

*  Test houses are removed completely after a while

*  Test signs can be placed outside border

*  Test geologist has an ok movement pattern

*  Test geologist goes directly back to storage if flag is removed

*  Test geologist doesn't investigate points with house, tree, stone, flag, water

*  Test that the pig farmer goes to the right place when he feeds the pigs

*  Exception caused by    org.appland.settlers.test.TestFarm.testFarmerReturnsAfterHarvesting(TestFarm.java:422)    

*  test re-routing of cargos when a road is removed 

*  test re-routing of cargos when a road is added

*  getClosestStorage

*  farm puts crops on road
 
*  farm seems to always place crop on its flag

*  worker takes exactly 10 ticks to reach next step

*  worker    isExactlyAtPoint()    seems wrong

*  test that barracks can only be built close to border

*  farmer doesn't pass by the flag on the way back from planting/harvesting

*  do storages get a worker traveling from the hq or is it magically available directly?

*  Q: how often does the storages/headquarter assign new workers?

*  Q: does tree conservation program also apply to stones?

*  fix exception:    (ALLVARLIG: null
org.appland.settlers.model.DeliveryNotPossibleException: This building does not accept deliveries.
	at org.appland.settlers.model.Building.putCargo(Building.java:254)
	at org.appland.settlers.model.Courier.onArrival(Courier.java:263)
	at org.appland.settlers.model.Worker.handleArrival(Worker.java:154)
	at org.appland.settlers.model.Worker.stepTime(Worker.java:69)
	at org.appland.settlers.model.GameMap.stepTime(GameMap.java:119)
	at org.appland.settlers.test.Utils.fastForward(Utils.java:71)
	at org.appland.settlers.test.TestFarm.testFarmerReturnsAfterHarvesting(TestFarm.java:405))   

*  Test the initial amount of all materials

*  It should not be possible to place building that overlaps the border - verify occupied points in game

*  Check that road cannot be built in bad angle to a building's flag (directly to left?) - check with game

*  Verify that destroyed buildings are removed completely after a suitable timeout

*  Verify that a cargo being delivered to a building that is completely gone is re-routed to the closest storage

*  Verify that cargos are delivered to new building when it is placed so that its flag splits a road



TO IMPLEMENT
============

*  Add constructHouse method to Test Utils to replace the construct[Small|Medium|Large]House methods

*  Consider changing stepTime to throw an Exception so erros will reach the top level and not get missed

*  Add ability to stop production

*  Make pig pink in the app

*  Change drawing in app to go from back to front instead of drawing all elements of each type

*  Add list of all possible building types to the model

*  Add construction of pig farm to the app

*  Draw cargo in front of the flagpole

*  Don't draw suggestions for next road connection over houses, verify limits next to small, medium and large houses

*  Implement support for several players

*  Implement option to set delivery priority for materials

*  Change cost of producing a barracks to only two plancks

*  Merge construct[Small|Medium|Large]House into one method that handles all buildings dynamically

*  Adjust the price for constructing foresterhut, woodcutter, quarry and sawmill

*  Make measurement used for tree conservation program consider all storages

*  Make tree conservation program optional

*  Fix so workers go back to _closest_ storage when their building is destroyed

*  Implement DonkeyFarm, GuardHouse, WatchTower, Fortress, Hunter, MetalWorks, Shipyard, Harbour

*  Add messages

*  Add wild animals

*  Add scout

*  fix building to use a single state variable

*  Change name of test.Utils::surroundPointWithWater

*  extract app's canvas to its own class

*  change    Storage::retrieveWorker()    to use a Map<Material, ? extends Worker>

*  add to app that 'S' dumps both state and code

*  adjust the defense radius of barracks and the headquarter

*  possible road connections returns straight down|up. Fix this

*  worker setTarget and setTargetOffroad should share much more code

*  house should not be possible to place if its flag will be too close to another flag

*  limit number of cargos on a flag





DONE
====

*  burning a barracks can cause the border to split into two areas. Add tests and handle this - DONE

*  should not be possible to place a flag at a stone (building with flag..) - DONE
    -  same thing with a tree  - DONE
    -  same thing with a house - DONE

*  farmer should return via the flag after harvesting - DONE

*  forester, stonemason, farmer should go all the way home instead of stopping at the flag - DONE

*  worker super class should have a return home method - DONE

*  woodcutter cuts, rests and doesn't come out again - DONE

*  woodcutter, farmer and stonemason should use get|setCargo instead of their own member - DONE

*  stones are finite and will disappear when all stone has been harvested - DONE

*  create road between houses and their flags. Make couriers deliver all the way to the house - DONE

*  roads should not be possible to make so short that the courier has nowhere to stand - DONE

*  create house to existing flag - DONE

*  record game ticks - DONE

*  worker walking to its assigned building should go all the way to the door - DONE

*  check that buildings, roads, flags can only be placed within the border - DONE

*  getPossibleRoadConnections (for new road) should stop at border - DONE

*  the simplistic border is broken - verify this and add the graham scan algorithm instead - DONE

*  farmer and stonemason should put their cargos on the flag themselves - DONE

*  Remove initiateCollectionOfNewProduce and getBuildingsWithNewProduce - DONE

*  Sawmill should need a worker - DONE

*  change placeWorker(Flag) to placeWorker(Point) - DONE

*  f-o and w doesn't work in the app - DONE

*  fix app so that workers always have the cargo shown if they are carrying something - DONE

*  should not be able to place house if the flag does not already exist and can not be placed - DONE

*  splitting a road can destroy it if one of the new roads is too short. Test - DONE

*  building has mapped its methods to the EndPoint interface's methods. Remove the indirection - DONE

*  remove need for gamelogic methods - DONE

*  draw the cargo with different colors depending on type - DONE

*  Courier can get stuck in WALKING_TO_ROAD when the road is split - DONE

*  headquarter should have a worker that carries cargos to its flag - DONE

*  make sure splitting roads works - DONE

*  make it possible to remove flags and roads - DONE

*  make it possible to destroy houses - DONE

*  clean up TestWell, TestFarm, TestForesterHut, TestQuarry similar to TestWoodcutter - DONE

*  clean up TestBakery, TestSawmill similar to TestWoodcutter - DONE

*  fix NPE in Fisherman    (ALLVARLIG: null
java.lang.NullPointerException
	at org.appland.settlers.model.Worker.setOffroadTarget(Worker.java:282)
	at org.appland.settlers.model.Worker.setOffroadTarget(Worker.java:251)
	at org.appland.settlers.model.Fisherman.onIdle(Fisherman.java:98)
	at org.appland.settlers.model.Worker.stepTime(Worker.java:93)
	at org.appland.settlers.model.GameMap.stepTime(GameMap.java:119)
	at org.appland.settlers.javaview.App$GameCanvas$2.run(App.java:878)
	at java.lang.Thread.run(Thread.java:745))    - DONE

*  move app's "new Miller()" to API recorder - DONE

*  record better names for houses - DONE

*  add recording of the terrain changes to the app - DONE

*  Remove spamming debug print "VARNING: Failed to find a way from (7, 9) to (6, 4)
aug 24, 2014 12:59:19 EM org.appland.settlers.model.GameMap findWayWithExistingRoads" - DONE

*  remove one of the Building::needsWorker() methods - DONE

*  add fishery and fisherman - DONE

*  remove hut variable from foresterhut - DONE

*  record the correct house type - DONE

*  make sure quarry and others produce the right type of cargo

*  add storage worker - DONE

*  make it possible to remove flags and roads in the app - DONE

*  make it possible to destroy houses in the app - DONE

*  drawing of tiles in application - DONE

*  change miner and terrain tiles to make the amount of gold go down when the miner mines - DONE

*  change storage countdowns to use the proper Countdown - DONE

*  record removal of roads, flags and houses - DONE

*  make api recording pre-indented - DONE

*  Generalize Mine and Miner - DONE

*  fix so that fisherman don't go to places where there are no more resources. Verify this! - DONE

*  change fisherman and terrain tiles to make the amount of fish go down when the fisherman fishes - DONE

*  Check that the stonemason gets a cargo of the right type - DONE

*  split road can cause couriers to stand at correct positions but refuse to pick up cargo - DONE

*  clean up method names in TestIronSmelter - DONE

*  Test amount of military in barracks - DONE

*  test that it's not possible to build a road across a lake (it is now...) - DONE

*  Test that barracks can hold one coin - DONE

*  Test military promotion in barracks - DONE

*  Remove Building::isWorkerRequired() - DONE

*  Remove Military(Rank) - DONE

*  new barracks should not enlarge border until it's occupied by a military - DONE

*  Remove empty constructor in all workers - DONE

*  Fix enterBuilding in Baker - DONE

*  Remove un-necessary try-catch in Baker, SamillWorker - DONE

*  Remove hut variable from farm - DONE

*  Make countdown in farm final - DONE

*  Remove un-needed try-catch in farmer - DONE

*  Use constants instead of magic numbers in farm - DONE

*  Test evacuating barracks without connected roads works - DONE

*  Test that evacuated soldier returns to storage - DONE

*  Change findWayWithExistingRoads to use a* (if appropriate) - DONE

*  Add evacuateMilitary() method to building - DONE

*  Add that military buildings can be emptied - DONE

*  Test geologist that returns to storage is stored properly - DONE

*  Connect goldmine button to actual action in app - DONE

*  add recording of calling geologist - DONE

*  Color gold ore cargos correctly in the app - DONE

*  Correct the spelling of fishery in the app's button - DONE

*  Add geologist - DONE

*  Test that a flag can be placed on a sign - DONE

*  Test that a mine can be placed on a sign - DONE

*  Test geologist places sign even if it didn't find anything - DONE

*  remove new Flag(int, int) - DONE

*  Test for all workers except well worker that it will produce cargos with no storage attached and that the cargos will get delivered once the building is connected - DONE

*  workers produce cargos and place on flag even if there is no road connected to it - DONE

*  cargos produced when no storage is available should resume delivery when a storage can be reached - DONE

*  Better indicators of possible road points in the app - DONE

*  Add buttons to create iron, coal and stone mines to the app - DONE

*  Send home workers and militaries when buildings are torn down - DONE

*  it should not be possible to create cargo without a position and a map reference - DONE

*  it should not be possible to create a courier without a position and a map reference - DONE

*  Test courier goes back to storage when its road is destroyed - DONE

*  Fix road building in the app so that connecting with a new flag in an existing road works - DONE

*  Draw signs nicer in the app - DONE

*  Remove exception when there is no geologist to retrieve. This is normal - DONE

*  Set Control tab to default tab in app - DONE

*  Re-add button to tear down building to the app - DONE

*  Fix so that the headquarter cannot be torn down - DONE

*  Make sure courier enters the storage instead of standing on top - DONE

*  Add "unoccupied" label to app drawing - DONE

*  Fix app to not suggest using a previous part of the road-to-be as the next step - DONE

*  Fix so a road cannot connect to a flag that is placed on itself (creating a loop and placing a new flag on the road-to-be) - DONE

*  draw stones with offset - DONE

*  scale dimensions of game objects - DONE

*  draw houses so they don't overlap their point - DONE

*  implement tree conservation program - DONE

*  Test that several geologists can be called concurrently - DONE

*  water from the well seems to not get delivered the whole way to the headquarter - DONE

*  Verify that pig farm doesn't produce anything if it doesn't have both wheat and water - DONE

*  Test pigfarm consumes water and wheat - DONE

*  Handle cargo whose target building is torn down - DONE

*  Move replanning logic for cargos to Cargo from GameMap - DONE

*  Change number of initial miners to three - DONE

*  Change info view to show more of inventory in the app - DONE

*  Add ability to create mints to the app - DONE

*  Change barracks to only require two plancks for construction - DONE

*  Test signs expire and eventually disappear - DONE





