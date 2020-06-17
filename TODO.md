LARGER CHANGES
==============

* Add the two missing states for house construction - digging planned, building planned

* Add builders and diggers

* Add ships, shipyard, harbour

* Add ability to configure military parameters

* Make production quotas of different tools configurable

* Improve available construction compared to map information through analytical regression

* Correct the production times for buildings

* Change so that military buildings cannot be placed too close together / only close to the border

* Add small boats

* Make discovered and owned land separate from defended land, and make sure it's correct for all military buildings

* Make it possible to configure allocation of planks

* Correct range for workers


SMALLER CHANGES
===============

* Test that each military type goes back to headquarter (one missing)


RE-FACTOR
=========

*  Fix TestGameMap

*  Fix ugly heuristic in Land::<init>

*  Clean up getAvailableHousePoints!

*  add    Road::isEndpointPair(Flag, Flag) and remove ugly if (.. && ..)   

*  Fix TestCourier and TestDonkey to use occupyRoad() instead of manually placing courier or donkey

*  Rename Road::setCourier(Courier) to reflect that it's used both for couriers and donkeys

*  Figure out how to make Size::contains(Size, Size) non-static




TEST
====

*  Test one gold mine, one iron mine, one coal mine, and one un-occupied coal mine. All mines should get food

*  Test that all mines want all types of food!!

*  Test when worker is ordered to go offroad to a place but cannot (e.g. when surrounded by stones). Test for all workers 

*  Test expanding computer player that it recovers when a barracks under construction is destroyed

*  Test Size::contains(Size, Size)

*  Test that all deployed soldiers in a military building return to the storehouse if it's torn down

*  Add tests for available buildings close to the border

*  Test that when a barracks is destroyed, the worker on roads that get removed return to their storehouse

*  Test who wins according to rank and experience and health

*  Test for the exact time of a fight

*  Test that the number of soldiers in a building goes down when a military has been retrieved

*  Test that the closest building with available soldiers is used in attacking

*  Test get attack radius for each military building

*  Add Utils.ageRoad() and update tests in donkey to use it before assigning donkeys

*  Test that the scout takes another route if it's called again to the same flag

*  Test that the radius of the land a scout discovers is correct

*  Test that roads cannot be built too close to buildings (farm is wrong)

*  Test that production can|cannot be stopped for unoccupied building

*  Test that production can't be stopped twice

*  Test that production can't be resumed unless it's stopped

*  Test that splitting a main road results in two main roads

*  Test signs can be placed outside border

*  Test geologist has an ok movement pattern

*  Test geologist does not go directly back to storehouse if flag is removed

*  Test geologist doesn't investigate points with house, tree, stone, flag, water

*  Test that the pig farmer goes to the right place when he feeds the pigs

*  farm puts crops on road
 
*  farm seems to always place crop on its flag

*  do storages get a worker traveling from the hq or is it magically available directly?

*  Q: how often does the storages/headquarter assign new workers?

*  Test the initial amount of all materials

*  It should not be possible to place building that overlaps the border - verify occupied points in game

*  Verify that a cargo being delivered to a building that is completely gone is re-routed to the closest storehouse

*  Geologist gets stuck when its flag is removed




TO IMPLEMENT
============

*  Adjust attack radius in military buildings

*  Make sure all tests for stopped production have more material than necessary

*  Add list of all possible building types to the model

*  worker setTarget and setTargetOffroad should share much more code






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

*  add storehouse worker - DONE

*  make it possible to remove flags and roads in the app - DONE

*  make it possible to destroy houses in the app - DONE

*  drawing of tiles in application - DONE

*  change miner and terrain tiles to make the amount of gold go down when the miner mines - DONE

*  change storehouse countdowns to use the proper Countdown - DONE

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

*  Remove un-necessary try-catch in Baker, SawmillWorker - DONE

*  Remove hut variable from farm - DONE

*  Make countdown in farm final - DONE

*  Remove un-needed try-catch in farmer - DONE

*  Use constants instead of magic numbers in farm - DONE

*  Test evacuating barracks without connected roads works - DONE

*  Test that evacuated soldier returns to storehouse - DONE

*  Change findWayWithExistingRoads to use a* (if appropriate) - DONE

*  Add evacuateMilitary() method to building - DONE

*  Add that military buildings can be emptied - DONE

*  Test geologist that returns to storehouse is stored properly - DONE

*  Connect goldmine button to actual action in app - DONE

*  add recording of calling geologist - DONE

*  Color gold ore cargos correctly in the app - DONE

*  Correct the spelling of fishery in the app's button - DONE

*  Add geologist - DONE

*  Test that a flag can be placed on a sign - DONE

*  Test that a mine can be placed on a sign - DONE

*  Test geologist places sign even if it didn't find anything - DONE

*  remove new Flag(int, int) - DONE

*  Test for all workers except well worker that it will produce cargos with no storehouse attached and that the cargos will get delivered once the building is connected - DONE

*  workers produce cargos and place on flag even if there is no road connected to it - DONE

*  cargos produced when no storehouse is available should resume delivery when a storehouse can be reached - DONE

*  Better indicators of possible road points in the app - DONE

*  Add buttons to create iron, coal and stone mines to the app - DONE

*  Send home workers and soldiers when buildings are torn down - DONE

*  it should not be possible to create cargo without a position and a map reference - DONE

*  it should not be possible to create a courier without a position and a map reference - DONE

*  Test courier goes back to storehouse when its road is destroyed - DONE

*  Fix road building in the app so that connecting with a new flag in an existing road works - DONE

*  Draw signs nicer in the app - DONE

*  Remove exception when there is no geologist to retrieve. This is normal - DONE

*  Set Control tab to default tab in app - DONE

*  Re-add button to tear down building to the app - DONE

*  Fix so that the headquarter cannot be torn down - DONE

*  Make sure courier enters the storehouse instead of standing on top - DONE

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

*  Change barracks to only require two planks for construction - DONE

*  Test signs expire and eventually disappear - DONE

*  Test houses are removed completely after a while - DONE

*  Verify that destroyed buildings are removed completely after a suitable timeout - DONE

*  Add constructHouse method to Test Utils to replace the construct[Small|Medium|Large]House methods - DONE

*  Make pig pink in the app - DONE

*  Make roads age - DONE

*  Draw main roads with a darker color in the app - DONE

*  Add button and key combo to build slaughterhouse in the app - DONE

*  Removing a building doesn't seem to remove the driveway to its flag - DONE

*  possible road connections returns straight down|up. Fix this - DONE

*  Remove Material argument to retrieveDonkey in Storage - DONE

*  Test the count of donkeys gets decreased when a donkey is assigned. - DONE

*  Test that no donkey gets assigned if there are no donkeys available - DONE

*  Draw donkeys in the app - DONE

*  Make sure donkeys get assigned to main roads - DONE

*  Remove the static list of available flag/house sites and calculate it dynamically - DONE

*  Remove terrainIsUpdated() method - DONE

*  Remove isPointCovered() - DONE

*  Remove GameMap::getRoadsThatNeedCouriers() - DONE

*  Test that there are no available flags or buildings on water - DONE

*  Q: When is the driveway of a building removed if the building is torn down? - DONE

*  Change name of test.Utils::surroundPointWithWater - DONE

*  Sometimes cargos for the headquarter are left by the flag instead of being delivered the whole way. Verify and fix! - DONE

*  Test that there are no available flags or buildings on stones or trees or roads - DONE

*  Rename Building::getHouseSize() to getSize() - DONE

*  Clean up in courier to reduce duplication - DONE

*  Add ability to stop production in well - DONE

*  Test that production can be stopped in the farm - DONE

*  Replicate additional tests in TestWell to the other buildings - DONE

*  Test GameMap::get(Width|Height)() - DONE

*  Q: Do donkeys get carried or do they walk themselves to the HQ when they have been produced? - DONE

*  Implement DonkeyFarm, GuardHouse, WatchTower, Fortress, Hunter, MetalWorks, Shipyard, Harbour - DONE

*  Fix so donkeys are only added once - DONE

*  Make fields in Cargo final - DONE

*  Test that signs are removed when a building is placed on top of the sign - DONE

*  Fix so that there is no available spot for road at a crop - DONE

*  Add ability to stop production for other buildings than Well - DONE

*  Fix so that it's not possible to create a road through crops - DONE

*  Change cost of producing a barracks to only two planks - DONE

*  Add construction of pig farm to the app - DONE

*  Add scout - DONE

*  Remove geologistsPromised in Flag and align with called scouts - DONE

*  Add extra scout tests to geologist - DONE

*  Test that both a courier and a donkey can be assigned to a road - DONE

*  Test that courier and donkey can't be assigned to a road twice - DONE

*  Test GameMap::getDiscoveredLand() - DONE

*  Fix "courier" comments in TestDonkey - DONE

*  Add comments to TestDonkey - DONE

*  Test that two couriers or donkeys cannot be assigned to a road - DONE

*  Test that production can't be stopped or resumed for barracks - DONE

*  Test that it's possible to build a new house on a destroyed house that is no longer burning - DONE

*  Remove house in the app doesn't seem to work - DONE

*  Problem creating a gold mine next to where a flag and signs were before - DONE

*  farmer doesn't pass by the flag on the way back from planting/harvesting - DONE

*  Implement GuardHouse, WatchTower, Fortress - DONE

*  Remove storages from Player - DONE

*  Remove Game, Controller - DONE

*  Remove GameMap::placeFlag(Flag) - DONE

*  Remove backward compatible mapping methods for pre-multiplayer tests - DONE

*  Remove try-catch in Worker::stepTime - DONE

*  Add common interface for all actors to get getPosition() unified - DONE

*  Verify that cargos are delivered to new building when it is placed so that its flag splits a road - DONE

*  Test that Player::attack(Building) throws an exception if the building cannot be attacked - DONE

*  Test creating invalid point throws an exception - DONE

*  Add @Override to all getPosition and stepTime and all overriding the on* methods in Worker - DONE

*  Test that evacuating soldiers updates getHostedSoldiers() - DONE

*  FIX: building never stops needing coins, even when it's full. Causes exception when courier tries to deliver coin - DONE

*  house should not be possible to place if its flag will be too close to another flag - DONE

*  Merge construct[Small|Medium|Large]House into one method that handles all buildings dynamically - DONE

*  Implement support for several players - DONE

*  Consider changing stepTime to throw an Exception so errors will reach the top level and not get missed - DONE

*  Add button to stop production in building to the sideview in the app - DONE

*  Scale drawing of workers and trees in the app - DONE

*  Change app to create player - DONE

*  Add shortcut to get more material for active player - DONE

*  Only show attack button for enemy buildings that can be attacked (military and within range) - DONE

*  Update recorder to explicitly create player list - DONE

*  Add buttons for stopping coin production and evacuation of military buildings - DONE

*  Add button to empty military buildings to the sideview in the app - DONE

*  Add method to see if a military building is evacuated - DONE

*  Test border is drawn for inside donut shaped land - DONE

*  Test that the new GameMap::place*(Player, ...) still places objects directly - DONE

*  Make sure there is a test that there cannot be more than one headquarter per player - DONE

*  Remove constructor in worker that doesn't take a player - DONE

*  Test that woodcutter worker (and others) can't go through buildings - DONE

*  Test GameMap::isAvailableFlagSpot(...) - DONE

*  Test GameMap::isAvailableHouseSpot(...) - DONE

*  Test that new roads are not populated with couriers from an opponent's storehouse if it happens to be the closest - DONE

*  Test that headquarter is destroyed when it's taken by an opponent - DONE

*  Test that storages only deliver to houses of their own type - DONE

*  Remove Utils.occupySawmill - DONE

*  Test that cargo on the way to be delivered is re-routed if the planned way breaks - DONE

*  Test that all workers return to their own storehouse when their house is torn down - DONE

*  Test geologist does not place signs on flags (and on roads???) - DONE

*  test re-routing of cargos when a road is removed - DONE 

*  worker takes exactly 10 ticks to reach next step - DONE

*  worker    isExactlyAtPoint()    seems wrong - DONE

*  Fix in app so that it's not possible to choose non-available road points when building road - DONE

*  Fix so that farmer can't walk through his own house when he goes out to plan or harvest - DONE

*  Implement HunterHut - DONE

*  Add wild animals - DONE

*  Adjust the price for constructing foresterhut, woodcutter, quarry and sawmill - DONE

*  Change drawing in app to go from back to front instead of drawing all elements of each type - DONE

*  Improve the drawing of the selected spot - DONE

*  Don't draw suggestions for next road connection over houses - DONE

*  test re-routing of cargos when a road is added - DONE

*  Test that player is set correctly in workers retrieved from storehouse - DONE

*  Test that storages only deliver on existing roads - DONE

*  There is too little stone on the map - DONE

*  Use information about available flags and buildings to show the right buttons in the app - DONE

*  Add method to see whether a military building is accepting coins - DONE

*  Change PLANCK, PLANCK, etc in @HouseSize to planks=2 - IGNORE

*  Test farmed crop fields eventually disappear - DONE

*  Test that player is set correctly in soldiers - DONE

*  Implement option to set delivery priority for materials - DONE 

*  Fix so workers go back to _closest_ storehouse when their building is destroyed - DONE

*  extract app's canvas to its own class - DONE

*  add to app that 'S' dumps both state and code - IGNORE

*  Quarry seems to always get its stone late. Does the tree conservation program stop it? It seems very likely - RESOLVED (it was because of the tree conservation program)

*  getClosestStorage - DONE

* Fix so headquarter can stop storehouse of material and force output - DONE

* Limit how many cargos can be placed on flag - DONE

* Correct the possible military ranks - DONE

* Make the tree conservation program configurable - DONE

* Add game message and monitoring for when a player wins or loses - DONE

* Add metalworks - DONE

