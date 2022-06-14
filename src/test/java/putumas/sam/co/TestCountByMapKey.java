package putumas.sam.co;

import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;


public class TestCountByMapKey {
    /**
     * Given a Map with a definition
     * Map<key,string[]>
     * Then get the number of element in each key
     */
    @Test
    public void testGetCountByKey() {
        String[] moSlots = new String[10];
        Arrays.fill(moSlots, 0, moSlots.length, "o");
        String[] vanSlots = new String[10];
        Arrays.fill(vanSlots, 0, vanSlots.length, "o");
        String[] carSlots = new String[10];
        Arrays.fill(carSlots, 0, carSlots.length, "o");

        Map<Kind, String[]> slotByKinds = new HashMap<>();
        slotByKinds.put(Kind.CAR, carSlots);
        slotByKinds.put(Kind.MO, moSlots);
        slotByKinds.put(Kind.VAN, vanSlots);
        //Map<Kind, Integer> p = new HashMap<>();
        Utilities<String> util = new Utilities<>();
        Map<Kind, Integer> countByKInd = util.getCountByKey(slotByKinds, "o"::equals);

        //assert p

        Assertions.assertFalse(countByKInd.isEmpty());
        Assertions.assertTrue(countByKInd.containsKey(Kind.CAR));
        Assertions.assertTrue(countByKInd.containsKey(Kind.VAN));
        Assertions.assertTrue(countByKInd.containsKey(Kind.MO));

        //further check

        Assertions.assertEquals(10, countByKInd.get(Kind.CAR));
        Assertions.assertEquals(10, countByKInd.get(Kind.VAN));
        Assertions.assertEquals(10, countByKInd.get(Kind.MO));

        int noOfAvailableSlot = util.getCountWithFilter(slotByKinds, "o"::equals);
        Assertions.assertEquals(30, noOfAvailableSlot);

        int noOfTaken = util.getCountWithFilter(slotByKinds, "x"::equals);
        Assertions.assertEquals(0, noOfTaken);
    }
}
