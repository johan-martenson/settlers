package org.appland.settlers.test.player;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerChangeNotifications {

    @Test
    public void testListenToQuotaChanges() throws InvalidUserActionException {
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        var listener = new Utils.PlayerMonitor();

        // Listen to changes in the player
        player0.addPlayerChangeListener(listener);

        // Verify that changing the quota causes a notification
        assertNotEquals(3, player0.getMetalworksPlankQuota());
        assertEquals(listener.getEventsForPlayer(player0).size(), 0);

        player0.setMetalworksPlankQuota(3);

        assertEquals(3, player0.getMetalworksPlankQuota());

        // Verify that the listener was called
        assertTrue(listener.getEventsForPlayer(player0).size() > 0);
    }
}
