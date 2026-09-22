package org.appland.settlers.computer;

import org.appland.settlers.assets.Nation;

import java.util.List;
import java.util.Map;

public final class Gabs {

    public enum Mood {
        CALM,
        HAPPY,
        CONFIDENT,
        WORRIED,
        ANGRY,
        AGGRESSIVE, CURIOUS
    }

    public record ChatLine(
            int weight,
            Mood mood,
            String text) {
    }

    private Gabs() { }

    static Map<GamePlayEvent, List<ChatLine>> forNation(Nation nation) {
        return switch (nation) {
            case ROMANS -> ROMAN;
            case VIKINGS -> VIKING;
            case AFRICANS -> AFRICAN;
            case JAPANESE -> JAPANESE;
        };
    }

    public static final Map<GamePlayEvent, List<ChatLine>> ROMAN = Map.ofEntries(
            Map.entry(GamePlayEvent.STARTING_GAME, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Rome shall bring order to these lands."),
                    new ChatLine(20, Mood.CONFIDENT, "A new province awaits the glory of Rome."),
                    new ChatLine(20, Mood.CALM, "Let us build an empire worthy of Caesar."),
                    new ChatLine(1, Mood.CONFIDENT, "Senators speak. Legions act.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_OTHER_PLAYER, List.of(
                    new ChatLine(20, Mood.CALM, "Another nation has entered Rome's sphere of influence."),
                    new ChatLine(20, Mood.CALM, "We are not alone. Let us observe these neighbors carefully."),
                    new ChatLine(20, Mood.CONFIDENT, "Interesting... another people inhabit these lands."),
                    new ChatLine(1, Mood.CONFIDENT, "Every empire begins with its neighbors.")
            )),

            Map.entry(GamePlayEvent.RESPOND_TO_OTHER_PLAYERS_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "Rome hears your words."),
                    new ChatLine(20, Mood.CALM, "An interesting statement."),
                    new ChatLine(20, Mood.CALM, "Your message has been noted.")
            )),

            Map.entry(GamePlayEvent.IDLE_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "A patient empire endures."),
                    new ChatLine(20, Mood.CALM, "Stone upon stone, Rome grows."),
                    new ChatLine(20, Mood.CALM, "Order and discipline are the foundations of greatness."),
                    new ChatLine(20, Mood.CALM, "Even in peace, Rome prepares."),
                    new ChatLine(2, Mood.CALM, "The roads we build today shall serve generations.")
            )),

            Map.entry(GamePlayEvent.PLANKS_AND_STONES_READY, List.of(
                    new ChatLine(20, Mood.HAPPY, "Excellent. Construction may begin in earnest."),
                    new ChatLine(20, Mood.HAPPY, "Our builders now have what they require."),
                    new ChatLine(20, Mood.CONFIDENT, "The foundations of our empire are secured.")
            )),

            Map.entry(GamePlayEvent.FOUND_GOLD, List.of(
                    new ChatLine(20, Mood.HAPPY, "Gold! Rome's treasury shall prosper."),
                    new ChatLine(20, Mood.HAPPY, "The gods smile upon our miners."),
                    new ChatLine(20, Mood.CONFIDENT, "Our wealth grows with every nugget."),
                    new ChatLine(1, Mood.HAPPY, "Even the tax collectors will celebrate today.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE, List.of(
                    new ChatLine(20, Mood.HAPPY, "These hills shall serve Rome well."),
                    new ChatLine(20, Mood.CONFIDENT, "A promising mountain. We shall exploit its riches."),
                    new ChatLine(20, Mood.HAPPY, "Our surveyors have found valuable ore.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_SEA, List.of(
                    new ChatLine(20, Mood.HAPPY, "The sea opens new possibilities for Rome."),
                    new ChatLine(20, Mood.CONFIDENT, "Roman ships shall one day sail these waters."),
                    new ChatLine(20, Mood.HAPPY, "The coast is ours to command.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_TREES, List.of(
                    new ChatLine(20, Mood.WORRIED, "Our forests have been exhausted."),
                    new ChatLine(20, Mood.WORRIED, "The lumber supply dwindles."),
                    new ChatLine(20, Mood.CALM, "We must cultivate new forests.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_STONE, List.of(
                    new ChatLine(20, Mood.WORRIED, "The quarries have fallen silent."),
                    new ChatLine(20, Mood.WORRIED, "Rome requires fresh sources of stone."),
                    new ChatLine(20, Mood.CALM, "Our builders must seek new quarries.")
            )),

            Map.entry(GamePlayEvent.ECONOMY_STALLED, List.of(
                    new ChatLine(20, Mood.WORRIED, "Production has slowed. This is unacceptable."),
                    new ChatLine(20, Mood.WORRIED, "Our economy requires decisive action."),
                    new ChatLine(20, Mood.ANGRY, "Rome must restore its industry at once.")
            )),

            Map.entry(GamePlayEvent.EXPANDED_TERRITORY, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Rome's borders advance."),
                    new ChatLine(20, Mood.HAPPY, "Another province joins our realm."),
                    new ChatLine(20, Mood.CONFIDENT, "Our influence continues to spread.")
            )),

            Map.entry(GamePlayEvent.LOST_TERRITORY, List.of(
                    new ChatLine(20, Mood.WORRIED, "We have yielded ground."),
                    new ChatLine(20, Mood.ANGRY, "This setback shall not stand."),
                    new ChatLine(20, Mood.CONFIDENT, "Rome shall reclaim what is hers.")
            )),

            Map.entry(GamePlayEvent.BUILT_FORTRESS, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "A mighty fortress now guards our frontier."),
                    new ChatLine(20, Mood.CONFIDENT, "Our defenses grow stronger."),
                    new ChatLine(20, Mood.CONFIDENT, "No barbarian shall pass these walls.")
            )),

            Map.entry(GamePlayEvent.ATTACKING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Advance in the name of Rome!"),
                    new ChatLine(20, Mood.CONFIDENT, "Legions, forward!"),
                    new ChatLine(20, Mood.CONFIDENT, "Let our enemies witness Roman discipline."),
                    new ChatLine(1, Mood.CONFIDENT, "Fortune favors the bold.")
            )),

            Map.entry(GamePlayEvent.BEING_ATTACKED, List.of(
                    new ChatLine(20, Mood.ANGRY, "We are under attack!"),
                    new ChatLine(20, Mood.ANGRY, "To arms! Defend the empire!"),
                    new ChatLine(20, Mood.CONFIDENT, "Hold your ground!")
            )),

            Map.entry(GamePlayEvent.WON_BATTLE, List.of(
                    new ChatLine(20, Mood.HAPPY, "Victory belongs to Rome!"),
                    new ChatLine(20, Mood.HAPPY, "Our legions have prevailed."),
                    new ChatLine(20, Mood.CONFIDENT, "Another triumph for the empire."),
                    new ChatLine(2, Mood.HAPPY, "Veni, vidi... almost vici.")
            )),

            Map.entry(GamePlayEvent.LOST_BATTLE, List.of(
                    new ChatLine(20, Mood.WORRIED, "A regrettable defeat."),
                    new ChatLine(20, Mood.WORRIED, "Rome has suffered a setback."),
                    new ChatLine(20, Mood.CONFIDENT, "We shall learn from this loss.")
            )),

            Map.entry(GamePlayEvent.CAPTURED_BUILDING, List.of(
                    new ChatLine(20, Mood.HAPPY, "This settlement now serves Rome."),
                    new ChatLine(20, Mood.CONFIDENT, "Another outpost has fallen to the empire."),
                    new ChatLine(20, Mood.HAPPY, "Our territory grows stronger.")
            )),

            Map.entry(GamePlayEvent.PROMOTED_FIRST_GENERAL, List.of(
                    new ChatLine(20, Mood.HAPPY, "A worthy commander rises through the ranks."),
                    new ChatLine(20, Mood.CONFIDENT, "Rome now marches under a great general."),
                    new ChatLine(20, Mood.CONFIDENT, "Our armies are led by proven leadership.")
            )),

            Map.entry(GamePlayEvent.DOMINATING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "The empire stands unrivaled."),
                    new ChatLine(20, Mood.CONFIDENT, "Our enemies cannot withstand Rome."),
                    new ChatLine(20, Mood.CONFIDENT, "Victory is becoming inevitable."),
                    new ChatLine(1, Mood.CONFIDENT, "The Senate will remember this campaign.")
            )),

            Map.entry(GamePlayEvent.FALLING_BEHIND, List.of(
                    new ChatLine(20, Mood.WORRIED, "These are difficult times for Rome."),
                    new ChatLine(20, Mood.CONFIDENT, "We must rebuild our strength."),
                    new ChatLine(20, Mood.CALM, "Discipline and perseverance shall see us through.")
            ))
    );

    public static final Map<GamePlayEvent, List<ChatLine>> VIKING = Map.ofEntries(

            Map.entry(GamePlayEvent.STARTING_GAME, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "A fine day to found a kingdom!"),
                    new ChatLine(20, Mood.HAPPY, "May Odin smile upon our deeds!"),
                    new ChatLine(20, Mood.CONFIDENT, "Let's see who's brave enough to face us!"),
                    new ChatLine(1, Mood.HAPPY, "I forgot my drinking horn... oh well.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_OTHER_PLAYER, List.of(
                    new ChatLine(20, Mood.HAPPY, "Ah! Someone to trade with... or raid!"),
                    new ChatLine(20, Mood.CONFIDENT, "New neighbors! I wonder what treasures they keep."),
                    new ChatLine(20, Mood.HAPPY, "Excellent! This won't be a lonely voyage.")
            )),

            Map.entry(GamePlayEvent.RESPOND_TO_OTHER_PLAYERS_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "Ha! Well spoken."),
                    new ChatLine(20, Mood.CALM, "We'll see about that."),
                    new ChatLine(20, Mood.HAPPY, "Words are cheap. Axes are convincing.")
            )),

            Map.entry(GamePlayEvent.IDLE_CHAT, List.of(
                    new ChatLine(20, Mood.HAPPY, "Who's making the stew tonight?"),
                    new ChatLine(20, Mood.CALM, "A little peace makes the next battle sweeter."),
                    new ChatLine(20, Mood.HAPPY, "Our longships deserve finer harbors."),
                    new ChatLine(20, Mood.CONFIDENT, "A Viking is never truly idle."),
                    new ChatLine(2, Mood.HAPPY, "I hope someone remembered the mead.")
            )),

            Map.entry(GamePlayEvent.PLANKS_AND_STONES_READY, List.of(
                    new ChatLine(20, Mood.HAPPY, "Now we're building something worthy of song!"),
                    new ChatLine(20, Mood.CONFIDENT, "Bring the timber! Bring the stone!"),
                    new ChatLine(20, Mood.HAPPY, "Our craftsmen can finally get to work.")
            )),

            Map.entry(GamePlayEvent.FOUND_GOLD, List.of(
                    new ChatLine(20, Mood.HAPPY, "Gold! That's always welcome."),
                    new ChatLine(20, Mood.HAPPY, "The treasure grows!"),
                    new ChatLine(20, Mood.CONFIDENT, "We'll need a bigger treasure chest."),
                    new ChatLine(1, Mood.HAPPY, "Don't tell the tax collector.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE, List.of(
                    new ChatLine(20, Mood.HAPPY, "These mountains hide good steel!"),
                    new ChatLine(20, Mood.CONFIDENT, "Excellent! More iron for our axes."),
                    new ChatLine(20, Mood.HAPPY, "The miners will earn their ale today.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_SEA, List.of(
                    new ChatLine(20, Mood.HAPPY, "Now *this* feels like home!"),
                    new ChatLine(20, Mood.CONFIDENT, "The sea calls to us!"),
                    new ChatLine(20, Mood.HAPPY, "Longships belong on these waters.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_TREES, List.of(
                    new ChatLine(20, Mood.WORRIED, "We've chopped every decent tree!"),
                    new ChatLine(20, Mood.WORRIED, "No forest lasts forever."),
                    new ChatLine(20, Mood.CALM, "Time to plant what we've taken.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_STONE, List.of(
                    new ChatLine(20, Mood.WORRIED, "The quarry has gone quiet."),
                    new ChatLine(20, Mood.WORRIED, "Looks like we've emptied this hill."),
                    new ChatLine(20, Mood.CALM, "We'll find more stone.")
            )),

            Map.entry(GamePlayEvent.ECONOMY_STALLED, List.of(
                    new ChatLine(20, Mood.WORRIED, "What's everyone standing around for?"),
                    new ChatLine(20, Mood.ANGRY, "Move! We have work to do!"),
                    new ChatLine(20, Mood.WORRIED, "Even Vikings need supplies.")
            )),

            Map.entry(GamePlayEvent.EXPANDED_TERRITORY, List.of(
                    new ChatLine(20, Mood.HAPPY, "More land for our people!"),
                    new ChatLine(20, Mood.CONFIDENT, "Our banners fly farther today."),
                    new ChatLine(20, Mood.HAPPY, "Another fine piece of the world is ours.")
            )),

            Map.entry(GamePlayEvent.LOST_TERRITORY, List.of(
                    new ChatLine(20, Mood.ANGRY, "We'll take it back!"),
                    new ChatLine(20, Mood.WORRIED, "That land wasn't theirs to keep."),
                    new ChatLine(20, Mood.CONFIDENT, "Nothing stolen from Vikings stays stolen for long.")
            )),

            Map.entry(GamePlayEvent.BUILT_FORTRESS, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "A proper stronghold!"),
                    new ChatLine(20, Mood.HAPPY, "Let them come!"),
                    new ChatLine(20, Mood.CONFIDENT, "These walls will stand firm.")
            )),

            Map.entry(GamePlayEvent.ATTACKING, List.of(
                    new ChatLine(20, Mood.HAPPY, "Charge!"),
                    new ChatLine(20, Mood.CONFIDENT, "For Odin!"),
                    new ChatLine(20, Mood.HAPPY, "Let's see if they can fight!"),
                    new ChatLine(2, Mood.HAPPY, "Save me something to smash!")
            )),

            Map.entry(GamePlayEvent.BEING_ATTACKED, List.of(
                    new ChatLine(20, Mood.ANGRY, "Finally! A proper fight!"),
                    new ChatLine(20, Mood.CONFIDENT, "Hold the line!"),
                    new ChatLine(20, Mood.ANGRY, "You'll regret coming here!")
            )),

            Map.entry(GamePlayEvent.WON_BATTLE, List.of(
                    new ChatLine(20, Mood.HAPPY, "Ha! That was glorious!"),
                    new ChatLine(20, Mood.CONFIDENT, "Another victory for our clan!"),
                    new ChatLine(20, Mood.HAPPY, "Skål! We have earned it!"),
                    new ChatLine(1, Mood.HAPPY, "Valhalla will have to wait another day!")
            )),

            Map.entry(GamePlayEvent.LOST_BATTLE, List.of(
                    new ChatLine(20, Mood.WORRIED, "A hard lesson."),
                    new ChatLine(20, Mood.ANGRY, "We'll meet them again."),
                    new ChatLine(20, Mood.CONFIDENT, "A Viking never stays down for long.")
            )),

            Map.entry(GamePlayEvent.CAPTURED_BUILDING, List.of(
                    new ChatLine(20, Mood.HAPPY, "This hall belongs to us now!"),
                    new ChatLine(20, Mood.CONFIDENT, "Another prize for our people."),
                    new ChatLine(20, Mood.HAPPY, "A fine addition to our lands.")
            )),

            Map.entry(GamePlayEvent.PROMOTED_FIRST_GENERAL, List.of(
                    new ChatLine(20, Mood.HAPPY, "Now there's a warrior worth following!"),
                    new ChatLine(20, Mood.CONFIDENT, "Lead us to glory!"),
                    new ChatLine(20, Mood.HAPPY, "Our champions grow stronger.")
            )),

            Map.entry(GamePlayEvent.DOMINATING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "No one can stop us now!"),
                    new ChatLine(20, Mood.HAPPY, "Victory smells sweeter than mead!"),
                    new ChatLine(20, Mood.CONFIDENT, "The sagas will remember this day."),
                    new ChatLine(2, Mood.HAPPY, "I almost feel sorry for them... almost.")
            )),

            Map.entry(GamePlayEvent.FALLING_BEHIND, List.of(
                    new ChatLine(20, Mood.WORRIED, "We've weathered worse storms."),
                    new ChatLine(20, Mood.CONFIDENT, "A Viking's courage is strongest when hope is weakest."),
                    new ChatLine(20, Mood.ANGRY, "This story isn't over yet.")
            ))
    );

    public static final Map<GamePlayEvent, List<ChatLine>> JAPANESE = Map.ofEntries(

            Map.entry(GamePlayEvent.STARTING_GAME, List.of(
                    new ChatLine(20, Mood.CALM, "May our people prosper in harmony."),
                    new ChatLine(20, Mood.CALM, "Patience lays the foundation for greatness."),
                    new ChatLine(20, Mood.CONFIDENT, "A steady hand builds a lasting nation."),
                    new ChatLine(1, Mood.CALM, "The tallest cedar begins as a small seedling.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_OTHER_PLAYER, List.of(
                    new ChatLine(20, Mood.CALM, "Our neighbors reveal themselves."),
                    new ChatLine(20, Mood.CALM, "Every meeting offers an opportunity to learn."),
                    new ChatLine(20, Mood.CALM, "May wisdom guide our dealings.")
            )),

            Map.entry(GamePlayEvent.RESPOND_TO_OTHER_PLAYERS_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "Your words have been heard."),
                    new ChatLine(20, Mood.CALM, "An interesting thought."),
                    new ChatLine(20, Mood.CALM, "Time will reveal the truth.")
            )),

            Map.entry(GamePlayEvent.IDLE_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "Even silence has purpose."),
                    new ChatLine(20, Mood.CALM, "The patient craftsman creates the finest work."),
                    new ChatLine(20, Mood.CALM, "Every season brings new opportunities."),
                    new ChatLine(2, Mood.CALM, "A quiet village is often the strongest.")
            )),

            Map.entry(GamePlayEvent.PLANKS_AND_STONES_READY, List.of(
                    new ChatLine(20, Mood.HAPPY, "Our craftsmen may begin."),
                    new ChatLine(20, Mood.CONFIDENT, "Strong foundations endure."),
                    new ChatLine(20, Mood.HAPPY, "Preparation bears fruit.")
            )),

            Map.entry(GamePlayEvent.FOUND_GOLD, List.of(
                    new ChatLine(20, Mood.HAPPY, "Fortune smiles upon us."),
                    new ChatLine(20, Mood.HAPPY, "Prosperity strengthens the nation."),
                    new ChatLine(1, Mood.CALM, "Gold has value only when used wisely.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE, List.of(
                    new ChatLine(20, Mood.HAPPY, "The mountain offers its gifts."),
                    new ChatLine(20, Mood.CALM, "Nature provides for those who respect it."),
                    new ChatLine(20, Mood.CONFIDENT, "These resources shall serve us well.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_SEA, List.of(
                    new ChatLine(20, Mood.HAPPY, "The sea broadens our horizons."),
                    new ChatLine(20, Mood.CALM, "These waters invite exploration."),
                    new ChatLine(20, Mood.CONFIDENT, "The tide carries new possibilities.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_TREES, List.of(
                    new ChatLine(20, Mood.WORRIED, "The forest asks for renewal."),
                    new ChatLine(20, Mood.WORRIED, "We have taken much from these woods."),
                    new ChatLine(20, Mood.CALM, "Let new trees grow.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_STONE, List.of(
                    new ChatLine(20, Mood.WORRIED, "This mountain has given all it could."),
                    new ChatLine(20, Mood.WORRIED, "We must seek new stone."),
                    new ChatLine(20, Mood.CALM, "Nothing lasts forever.")
            )),

            Map.entry(GamePlayEvent.ECONOMY_STALLED, List.of(
                    new ChatLine(20, Mood.WORRIED, "Our work has slowed."),
                    new ChatLine(20, Mood.WORRIED, "Balance must be restored."),
                    new ChatLine(20, Mood.CALM, "Careful planning will overcome this.")
            )),

            Map.entry(GamePlayEvent.EXPANDED_TERRITORY, List.of(
                    new ChatLine(20, Mood.HAPPY, "Our lands flourish."),
                    new ChatLine(20, Mood.CONFIDENT, "Our people have room to grow."),
                    new ChatLine(20, Mood.CALM, "Step by step, we advance.")
            )),

            Map.entry(GamePlayEvent.LOST_TERRITORY, List.of(
                    new ChatLine(20, Mood.WORRIED, "We have yielded ground."),
                    new ChatLine(20, Mood.CALM, "We must recover with patience."),
                    new ChatLine(20, Mood.CONFIDENT, "The next opportunity will come.")
            )),

            Map.entry(GamePlayEvent.BUILT_FORTRESS, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Strength is best shown through preparedness."),
                    new ChatLine(20, Mood.CONFIDENT, "This fortress protects our future."),
                    new ChatLine(20, Mood.CALM, "Well-built walls preserve peace.")
            )),

            Map.entry(GamePlayEvent.ATTACKING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Advance with discipline."),
                    new ChatLine(20, Mood.CONFIDENT, "Strike only when the moment is right."),
                    new ChatLine(20, Mood.CALM, "Remain focused."),
                    new ChatLine(2, Mood.CONFIDENT, "The swiftest victory is won before the battle begins.")
            )),

            Map.entry(GamePlayEvent.BEING_ATTACKED, List.of(
                    new ChatLine(20, Mood.ANGRY, "Stand firm."),
                    new ChatLine(20, Mood.CONFIDENT, "Do not lose your composure."),
                    new ChatLine(20, Mood.CONFIDENT, "Meet force with resolve.")
            )),

            Map.entry(GamePlayEvent.WON_BATTLE, List.of(
                    new ChatLine(20, Mood.HAPPY, "Victory was earned."),
                    new ChatLine(20, Mood.CONFIDENT, "Discipline has prevailed."),
                    new ChatLine(20, Mood.HAPPY, "Our training has borne fruit."),
                    new ChatLine(1, Mood.CALM, "The sharpest blade is seldom drawn.")
            )),

            Map.entry(GamePlayEvent.LOST_BATTLE, List.of(
                    new ChatLine(20, Mood.WORRIED, "There is wisdom in defeat."),
                    new ChatLine(20, Mood.CONFIDENT, "We shall improve."),
                    new ChatLine(20, Mood.CALM, "Perseverance brings strength.")
            )),

            Map.entry(GamePlayEvent.CAPTURED_BUILDING, List.of(
                    new ChatLine(20, Mood.HAPPY, "This place now serves our people."),
                    new ChatLine(20, Mood.CONFIDENT, "Our influence grows."),
                    new ChatLine(20, Mood.CALM, "The campaign progresses.")
            )),

            Map.entry(GamePlayEvent.PROMOTED_FIRST_GENERAL, List.of(
                    new ChatLine(20, Mood.HAPPY, "Leadership has found a worthy bearer."),
                    new ChatLine(20, Mood.CONFIDENT, "Our warriors are guided by experience."),
                    new ChatLine(20, Mood.CONFIDENT, "Honor has been rewarded.")
            )),

            Map.entry(GamePlayEvent.DOMINATING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Our preparations have been rewarded."),
                    new ChatLine(20, Mood.CONFIDENT, "Steady effort brings lasting success."),
                    new ChatLine(20, Mood.CALM, "Confidence should never become arrogance."),
                    new ChatLine(1, Mood.CALM, "The bamboo bends, yet does not break.")
            )),

            Map.entry(GamePlayEvent.FALLING_BEHIND, List.of(
                    new ChatLine(20, Mood.WORRIED, "Patience."),
                    new ChatLine(20, Mood.CONFIDENT, "Every setback teaches."),
                    new ChatLine(20, Mood.CALM, "The next move is more important than the last."),
                    new ChatLine(1, Mood.CALM, "Even winter gives way to spring.")
            ))
    );

    public static final Map<GamePlayEvent, List<ChatLine>> AFRICAN = Map.ofEntries(

            Map.entry(GamePlayEvent.STARTING_GAME, List.of(
                    new ChatLine(20, Mood.HAPPY, "May our people prosper together."),
                    new ChatLine(20, Mood.CALM, "The land provides for those who care for it."),
                    new ChatLine(20, Mood.CONFIDENT, "Together we shall build a thriving village."),
                    new ChatLine(1, Mood.CALM, "A single seed may one day become a mighty tree.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_OTHER_PLAYER, List.of(
                    new ChatLine(20, Mood.HAPPY, "New neighbors! May wisdom guide us."),
                    new ChatLine(20, Mood.CALM, "Every stranger has a story."),
                    new ChatLine(20, Mood.HAPPY, "Let us see what these people bring.")
            )),

            Map.entry(GamePlayEvent.RESPOND_TO_OTHER_PLAYERS_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "Your words are heard."),
                    new ChatLine(20, Mood.CALM, "There is wisdom in every conversation."),
                    new ChatLine(20, Mood.HAPPY, "Perhaps we understand each other better now.")
            )),

            Map.entry(GamePlayEvent.IDLE_CHAT, List.of(
                    new ChatLine(20, Mood.CALM, "A peaceful village is a prosperous village."),
                    new ChatLine(20, Mood.HAPPY, "The harvest rewards patient hands."),
                    new ChatLine(20, Mood.CALM, "Strong communities are built one day at a time."),
                    new ChatLine(20, Mood.HAPPY, "The children laugh, and that is a good sign."),
                    new ChatLine(2, Mood.CALM, "Even the tallest baobab began as a small seed.")
            )),

            Map.entry(GamePlayEvent.PLANKS_AND_STONES_READY, List.of(
                    new ChatLine(20, Mood.HAPPY, "Now our builders can shape the future."),
                    new ChatLine(20, Mood.CONFIDENT, "Together we build something lasting."),
                    new ChatLine(20, Mood.HAPPY, "Good work deserves strong foundations.")
            )),

            Map.entry(GamePlayEvent.FOUND_GOLD, List.of(
                    new ChatLine(20, Mood.HAPPY, "Fortune has smiled upon us."),
                    new ChatLine(20, Mood.HAPPY, "May this wealth serve all our people."),
                    new ChatLine(20, Mood.CONFIDENT, "Prosperity grows."),
                    new ChatLine(1, Mood.CALM, "Gold shines brightest when shared wisely.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE, List.of(
                    new ChatLine(20, Mood.HAPPY, "The mountain has revealed its gifts."),
                    new ChatLine(20, Mood.CONFIDENT, "These riches will strengthen our village."),
                    new ChatLine(20, Mood.CALM, "Nature continues to provide.")
            )),

            Map.entry(GamePlayEvent.DISCOVERED_SEA, List.of(
                    new ChatLine(20, Mood.HAPPY, "The sea stretches farther than the eye can see."),
                    new ChatLine(20, Mood.CALM, "The waters bring new opportunities."),
                    new ChatLine(20, Mood.CONFIDENT, "The coast welcomes our people.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_TREES, List.of(
                    new ChatLine(20, Mood.WORRIED, "The forest has given all it could."),
                    new ChatLine(20, Mood.CALM, "We must let the trees return."),
                    new ChatLine(20, Mood.WORRIED, "Tomorrow's shade must be planted today.")
            )),

            Map.entry(GamePlayEvent.NO_MORE_STONE, List.of(
                    new ChatLine(20, Mood.WORRIED, "The mountain now rests."),
                    new ChatLine(20, Mood.CALM, "We shall find fresh stone."),
                    new ChatLine(20, Mood.WORRIED, "Every resource has its season.")
            )),

            Map.entry(GamePlayEvent.ECONOMY_STALLED, List.of(
                    new ChatLine(20, Mood.WORRIED, "Our work has slowed."),
                    new ChatLine(20, Mood.CALM, "Together we will restore our prosperity."),
                    new ChatLine(20, Mood.CONFIDENT, "Patience and effort will see us through.")
            )),

            Map.entry(GamePlayEvent.EXPANDED_TERRITORY, List.of(
                    new ChatLine(20, Mood.HAPPY, "Our village continues to grow."),
                    new ChatLine(20, Mood.HAPPY, "There is room for more families."),
                    new ChatLine(20, Mood.CONFIDENT, "The land welcomes us.")
            )),

            Map.entry(GamePlayEvent.LOST_TERRITORY, List.of(
                    new ChatLine(20, Mood.WORRIED, "We have lost part of our home."),
                    new ChatLine(20, Mood.CONFIDENT, "Together we shall recover."),
                    new ChatLine(20, Mood.CALM, "Hope remains.")
            )),

            Map.entry(GamePlayEvent.BUILT_FORTRESS, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Our people are well protected."),
                    new ChatLine(20, Mood.CALM, "Strong walls guard peaceful lives."),
                    new ChatLine(20, Mood.CONFIDENT, "Let this fortress stand for generations.")
            )),

            Map.entry(GamePlayEvent.ATTACKING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Advance with courage."),
                    new ChatLine(20, Mood.CONFIDENT, "May our warriors return safely."),
                    new ChatLine(20, Mood.CALM, "Stand together.")
            )),

            Map.entry(GamePlayEvent.BEING_ATTACKED, List.of(
                    new ChatLine(20, Mood.ANGRY, "Protect our people!"),
                    new ChatLine(20, Mood.CONFIDENT, "Stand firm together!"),
                    new ChatLine(20, Mood.ANGRY, "Our home shall not fall!")
            )),

            Map.entry(GamePlayEvent.WON_BATTLE, List.of(
                    new ChatLine(20, Mood.HAPPY, "Our courage has carried the day."),
                    new ChatLine(20, Mood.HAPPY, "Today we celebrate together."),
                    new ChatLine(20, Mood.CONFIDENT, "Our warriors have made us proud."),
                    new ChatLine(1, Mood.CALM, "The strongest shield is unity.")
            )),

            Map.entry(GamePlayEvent.LOST_BATTLE, List.of(
                    new ChatLine(20, Mood.WORRIED, "We mourn today's loss."),
                    new ChatLine(20, Mood.CONFIDENT, "We shall rise again."),
                    new ChatLine(20, Mood.CALM, "Every hardship teaches resilience.")
            )),

            Map.entry(GamePlayEvent.CAPTURED_BUILDING, List.of(
                    new ChatLine(20, Mood.HAPPY, "Our people have a new home."),
                    new ChatLine(20, Mood.CONFIDENT, "Our village grows stronger."),
                    new ChatLine(20, Mood.HAPPY, "Another community joins us.")
            )),

            Map.entry(GamePlayEvent.PROMOTED_FIRST_GENERAL, List.of(
                    new ChatLine(20, Mood.HAPPY, "A wise leader has emerged."),
                    new ChatLine(20, Mood.CONFIDENT, "Our warriors follow a worthy commander."),
                    new ChatLine(20, Mood.HAPPY, "Leadership brings responsibility.")
            )),

            Map.entry(GamePlayEvent.DOMINATING, List.of(
                    new ChatLine(20, Mood.CONFIDENT, "Our people prosper."),
                    new ChatLine(20, Mood.HAPPY, "Harmony has made us strong."),
                    new ChatLine(20, Mood.CALM, "May we use our strength wisely."),
                    new ChatLine(1, Mood.CALM, "The tallest tree still gives shade to others.")
            )),

            Map.entry(GamePlayEvent.FALLING_BEHIND, List.of(
                    new ChatLine(20, Mood.WORRIED, "These are difficult days."),
                    new ChatLine(20, Mood.CONFIDENT, "Together we will endure."),
                    new ChatLine(20, Mood.CALM, "After every dry season comes the rain."),
                    new ChatLine(1, Mood.CALM, "One ember can light a thousand fires.")
            ))
    );
}