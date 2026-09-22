# **SETTLERS II COMBAT SYSTEM SPECIFICATION**

## **Status**

This document describes the combat system of Settlers II, incorporating corrections to pathfinding, combat math, logistical mechanics, and routing logic to accurately reflect the original engine's behavior.

## **Definitions**

**Military Building**  
Barracks, Guardhouse, Watchtower, Fortress (and to some degree Headquarters).  
**Attacker**  
Soldier participating in an attack against an enemy military building.  
**Defender**  
Soldier defending an attacked military building.  
**Garrison**  
Soldiers currently stationed inside a military building.  
**Combat Slot**  
A simultaneous one-on-one fight between an attacker and a defender.

## **Overview**

Combat in Settlers II is building-centric rather than unit-centric.  
The player never directly commands military units.  
The player can:

* Select an enemy military building.  
* Choose the quantity of soldiers to commit to the attack.  
* Choose to send strong attackers or weak attackers.

Combat consists of:

* Soldiers traveling cross-country to the target.  
* Defenders being gathered based on distance and global military settings.  
* Multiple simultaneous one-on-one duels at the target's flag.  
* Capture or defense of the military building.

## **Attack Preconditions**

A building may be attacked only if:

* It belongs to an enemy.  
* It is a military building.  
* It is constructed and has been occupied.  
* It is within attack range of the player's territory.  
* At least one friendly military building can provide attackers.

## **Starting an Attack**

1. Player selects an enemy military building.  
2. Game determines how many attackers are available in range.  
3. Player chooses the number of attackers and strong or weak attackers.  
4. Eligible soldiers are selected automatically based on distance and the chosen attack strength.  
5. Selected soldiers leave their military buildings.  
6. Soldiers physically walk toward the target.

The originating military building immediately loses those soldiers.

## **Movement and Pathfinding**

When a soldier leaves a building for a combat assignment (attacking or defending), they **completely ignore the road network**.  
During movement:

* Soldiers use a direct, cross-country pathfinding algorithm toward the target flag.  
* They navigate around geographical obstacles but do not snap to road nodes.  
* Soldiers may be attacked by nobody while en route.  
* Soldiers do not fight until reaching the target.  
* Soldiers do not passively heal while traveling.

Attackers may arrive at different times due to path length and terrain obstacles.

## **Attacker Placements & Combat Slots**

An attacker will go to the target building's flag and wait for a defender from the target building's garrison to come out to fight.

* There is a physical space limitation around the flag.  
* Excess arriving attackers will cluster around the flag and stand idle, waiting for a combat slot to open up, or go directly to meet defenders from nearby military buildings arriving as reinforcements.

## **Defender Selection**

The target building's own garrison defends against attackers that walk up to its flag. Additional defenders may be requested from nearby military buildings.  
A military building may provide defenders only if:

* It belongs to the defending player.  
* It is within military support range.  
* It contains soldiers above its required minimum garrison.

**Note:** If the player sets the global "Minimum Defenders" slider to 0, buildings *can* empty themselves completely to aid in the defense of a neighboring structure.

## **Defender Selection Priority**

Defender source buildings and the specific soldiers chosen are evaluated by two main factors:

1. **Distance:** Closest military buildings are prioritized.  
2. **Global Military Settings:** The game checks the ranks of available soldiers and selects them based on whether the player has prioritized defending with weaker or stronger soldiers.

## **Defender Reinforcement Logic**

Combat does not use fixed reinforcement waves. The game continuously evaluates defensive strength.  
Conceptually:

Plaintext  
while attack active:  
    evaluate defenders needed  
    dispatch additional defenders if required

Additional defenders may be requested when a defender dies, too few defenders are available, or new attackers arrive. Defenders already traveling toward the target count as pending reinforcements.

## **Chapter: The Soldier Combat & Duel Subsystem**

### **1\. Overview**

The combat system in *The Settlers II* operates via discrete, localized 1v1 duels outside military buildings. Combat is probabilistic, turn-based, and heavily reliant on a single mathematical evaluation per strike. Individual soldier states (including wounds) persist only while a soldier is actively spawned in the world; entering any garrison or logistical storage instantly resets their state.

### **2\. Soldier Statistics by Rank**

Soldiers possess two core attributes: Hit Points (HP) and Max Combat Roll. Every rank scales linearly, with each rank gaining exactly 1 additional HP and 1 additional maximum value to their random number generation.

| Rank                    | Base Hit Points (HP) | Max Combat Roll |
|:------------------------| :---- | :---- |
| **Private**             | 3 | 3 |
| **Private First Class** | 4 | 4 |
| **Sergeant**            | 5 | 5 |
| **Officer**             | 6 | 6 |
| **General**             | 7 | 7 |

### **3\. Step-by-Step Duel Logic**

When an attacking army targets an enemy military building, defending soldiers exit one by one to engage in sequential 1v1 duels. A single duel loops through alternating strikes until one soldier's HP reaches zero.

The **attacking soldier always performs the opening strike**. Thereafter, the two soldiers alternate turns until the duel ends.

#### **Execution Steps Per Strike:**

* **Step 1: Assign Turn Roles**
  The engine designates one soldier as the ActiveStriker and the other as the PassiveDefender. The attacking soldier is the initial ActiveStriker when the duel begins. After every completed strike attempt, regardless of whether it hits or is blocked, the roles swap.
* **Step 2: Generate Random Combat Rolls**
  Both soldiers independently generate a random integer. The range is inclusive of zero up to their specific rank maximum:

$$Roll_{\text{striker}} = \text{random}(0,\text{MaxCombatRoll}_{\text{striker}})$$

$$Roll_{\text{defender}} = \text{random}(0,\text{MaxCombatRoll}_{\text{defender}})$$

New random values are generated for every strike. Previous rolls have no influence on future strikes.
* **Step 3: Evaluate Strike Outcome**
  The engine compares the two values using a single conditional check. There are no secondary roll phases:
  * **Hit Condition:** If $Roll\_{\\text{striker}} \> Roll\_{\\text{defender}}$, the strike is successful. The defender's HP is decremented by exactly $1$, and the engine triggers the strike\_success and take\_damage visual animations.
  * **Block Condition (Ties & Defenses):** If $Roll\_{\\text{striker}} \\le Roll\_{\\text{defender}}$, the attack fails. The defender suffers $0$ damage, and the engine triggers the strike\_blocked and shield\_block animations. Note that any tie automatically results in a successful block.
* **Step 4: Check Vitality & Loop**
  * If the defender’s updated $\\text{HP} \> 0$, the roles swap (PassiveDefender becomes the new ActiveStriker), and the logic loops back to **Step 1**.
  * If the defender’s updated $\\text{HP} \= 0$, the loop terminates. The defeated soldier entity is destroyed, and the victor remains in the world.

### **4\. Continuous Combat and Inter-Duel HP Mechanics**

* **No Multi-Opponent Healing:** If an attacker defeats a defender and additional defenders remain inside the military building, the attacker immediately begins a new duel against the next defender using their current HP. No HP is recovered between consecutive outdoor duels.
* **Defender Recovery:** If the attacker is defeated, the surviving defender returns to the military building. Upon entering the building, the defender's HP is immediately restored to the maximum value for their rank before they may participate in any future battle.
* **State Persistence:** While a soldier remains outside any building, their current HP persists unchanged between all combat encounters. HP is the only combat state carried between duels; no other combat state or modifiers persist.

### **5\. Garrison Transitions & Health Recovery Logic**

"Healing" in the engine is modeled as an instant state reset triggered by a spatial transition event. The engine does not track a "wounded" boolean or a fractional regeneration timer.

#### **Military Buildings (Garrisoning)**

A soldier's HP is fully restored whenever they enter a building capable of housing soldiers.

This applies equally to:

* Returning to their home military building after successfully defending it.
* Occupying a newly captured enemy military building after eliminating its final defender.
* Entering a Storehouse.
* Entering the Headquarters.

In all cases:

$$\\text{CurrentHP} \= \\text{MaxHP}\_{\\text{rank}}$$

HP never persists while a soldier is inside any building. Only soldiers currently present in the world retain their current HP.

#### **Storehouses and Headquarters (Logistical Storage)**

When a wounded soldier walks into a Storehouse or Headquarters, the engine handles the entity via inventory serialization:

1. **Entity Destruction:** The active soldier entity in the world is completely purged from memory.
2. **Inventory Increment:** The target building increments its flat integer count for that specific rank (e.g., storehouse.general\_count++). Because hit point states are not serialized into building inventory arrays, all structural damage information is permanently discarded.
3. **Re-Spawning:** When a soldier of that rank is later requested to march out to a new military building or boundary extension, a completely new soldier entity is instantiated. This new entity initializes with default max HP according to its rank, completing an implicit full recovery.

## **Winner Behavior & Successive Duels**

The winner survives with their current remaining health.  
Example:

Plaintext  
General starts with 7 HP  
Takes 5 damage during the duel  
Wins duel  
Result: General remains alive with 2 HP

If surviving attackers/defenders remain, the soldier enters subsequent duels with this current remaining health. Health does NOT reset between duels.

## **Promotion and Logistics**

Soldiers do not gain experience from combat. **Promotions are strictly driven by logistics.**

* A soldier is promoted when the logistics network has delivered a **Gold Coin** to their military building and a certain amount of time has passed. Promotion **does not happen instantly** when the Gold Coin is received.

## **Routing and Evacuation**

Soldiers must react to changes in the combat state while they are en route:

* **Target Captured/Destroyed:** If a building is captured by an enemy, or destroyed (e.g., by a catapult), any attackers or defenders currently walking to that target will abort their mission, turn around, and walk back to their originating building.  
* **Evacuation:** The player can click "Evacuate" on a military building. Garrisoned units inside will leave and retreat to the nearest available military building. However, units actively engaged in a duel at the flag cannot retreat.

## **Building Capture**

If all defenders are defeated and the building's garrison reaches 0:

1. Defending force collapses.  
2. **All surviving attackers** will enter and occupy the captured building, up to its maximum garrison capacity.
3. Any excess surviving attackers will turn around and walk back to their originating military building.  
4. The building immediately changes ownership and territory is recalculated.

## **Building Defense Success**

If all attackers are defeated:

1. Attack ends.  
2. Building remains under defender ownership.  
3. Surviving defenders return inside the building, keeping their remaining health.
4. Any excess surviving defenders go back to their originating military buildings.

## **Catapults**

Catapults operate outside the duel system but are critical to military interaction:

* Catapults throw stones at enemy military buildings within range.  
* A successful hit instantly kills one garrisoned soldier inside the target building, completely bypassing the duel system and health mechanics.  
* If a catapult kills the last defender, the building is destroyed.

# **Picking Targets**
The catapult follows a strict, deterministic prioritization rule: it always fires at the closest valid enemy military building within its range. It never picks randomly.

# **Catapult Accuracy**

The probability that a catapult hits an enemy military building depends **only on the distance** between the catapult and the target. Distance is measured in **journeys** (road segments). The closest legal placement is **4 journeys**, and the maximum range is **12 journeys**.

## **Hit Probability by Distance**

| Distance (journeys) | Hit probability |
| ----- | ----- |
| 4 | 50.0% |
| 5 | 44.4% |
| 6 | 38.9% |
| 7 | 33.3% |
| 8 | 27.8% |
| 9 | 22.2% |
| 10 | 16.7% |
| 11 | 11.1% |
| 12 | 5.6% |

The hit probability follows the linear formula:

P(hit) \= (13 \- d) / 18

where `d` is the distance in journeys.

Examples:

* `d = 4` → `9/18 = 50.0%`
* `d = 5` → `8/18 = 44.4%`
* `d = 8` → `5/18 = 27.8%`
* `d = 12` → `1/18 = 5.6%`

## **Shot Resolution**

Each catapult shot is resolved as follows:

1. Select an enemy military building within range.
2. Determine whether the shot is a direct hit using the distance-dependent probability.
3. If the shot hits:
    * One soldier inside the building is killed.
    * If no soldiers remain, the military building is destroyed.
4. If the shot misses:
    * The stone lands on one of the surrounding tiles.
    * The shot has no gameplay effect.

The probability is purely distance-based; no other known factors influence a catapult's accuracy.

## **Territory Recalculation**

After a successful capture:

* Territory ownership is recalculated and borders move.  
* Enemy structures (e.g., woodcutters, mines) outside valid territory collapse.
* Roads may become disconnected.