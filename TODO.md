LARGER CHANGES
==============

* Fix upgrade cost for barracks, guard house, watch tower

* Add the missing "needs planing" state for house construction and add planer

* Add ability to configure soldier parameters

* Improve available construction compared to map information through analytical regression

* Correct the production times for buildings

* Change so that soldier buildings cannot be placed too close together / only close to the border

* Add small boats

* Make it possible to configure allocation of planks

* Correct range for workers - forester, fisherman, hunter, stonemason

* Reduce the amount of wild animals and make sure new wild animals only appear in forests


SMALLER CHANGES
===============

* Test each soldier type goes back to headquarter (one missing)


RE-FACTOR
=========
*  worker setTarget and setTargetOffroad should share much more code
*  Fix ugly heuristic in Land::<init>
*  Fix TestCourier and TestDonkey to use occupyRoad() instead of manually placing courier or donkey
*  Rename Road::setCourier(Courier) to reflect that it's used both for couriers and donkeys


TEST
====

*  Test one gold mine, one iron mine, one coal mine, and one un-occupied coal mine. All mines should get food

*  Test that all mines want all types of food!!

*  Test when worker is ordered to go offroad to a place but cannot (e.g. when surrounded by stones). Test for all workers 

*  Test that all deployed soldiers in a soldier building return to the storehouse if it's torn down

*  Add tests for available buildings close to the border

*  Test that when a barracks is destroyed, the worker on roads that get removed return to their storehouse

*  Test who wins according to rank and experience and health

*  Test for the exact time of a fight

*  Test that the number of soldiers in a building goes down when a soldier has been retrieved

*  Test that the closest building with available soldiers is used in attacking

*  Test get attack radius for each soldier building

*  Add Utils.ageRoad() and update tests in donkey to use it before assigning donkeys

*  Test that the scout takes another route if it's called again to the same flag

*  Test that the radius of the land a scout discovers is correct

*  Test that roads cannot be built too close to buildings (farm is wrong)

*  Test that production can|cannot be stopped for unoccupied building

*  Test that production can't be stopped twice

*  Test that production can't be resumed unless it's stopped

*  Test signs can be placed outside border

*  Test geologist has an ok movement pattern

*  Test geologist does not go directly back to storehouse if flag is removed

*  Test geologist doesn't investigate points with house, tree, stone, flag, water

*  Test that the pig farmer goes to the right place when he feeds the pigs

*  Test farmer doesn't put crops on road
 
*  farm seems to always place crop on its flag

*  do storages get a worker traveling from the hq or is it magically available directly?

*  Q: how often does the storages/headquarter assign new workers?

*  Test the initial amount of all materials

*  It should not be possible to place building that overlaps the border - verify occupied points in game

*  Verify that a cargo being delivered to a building that is completely gone is re-routed to the closest storehouse

*  Geologist gets stuck when its flag is removed




TO IMPLEMENT
============

*  Adjust attack radius in soldier buildings

*  Make sure all tests for stopped production have more material than necessary
