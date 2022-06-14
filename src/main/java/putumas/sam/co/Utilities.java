package putumas.sam.co;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum Kind {
    CAR,
    VAN,
    MO
}

public class Utilities<T> {
    /**
     * Convert a map of things into
     * a count of each key
     */

    public Map<Kind, Integer> getCountByKey(Map<Kind, T[]> arrayByKind, Predicate<Object> filter) {
        return arrayByKind.entrySet().stream()
                //the magic is here
                //supplier: Hashmap
                //accumulator: is BiConsumer-> (aMap,aMapToBeProcessed) -> aMap.put(Kind,aMapToBeProcessed.getValue().length) //put the value into a map 1 by one
                //can have business logic for transformation
                //Combiner: is BiConsumer-> (a,b) -> a.putAll(b) combining a map content into other map
                //
                //.collect(HashMap::new, (a, b) -> a.put(b.getKey(), b.getValue().length), Map::putAll);
                .collect(HashMap::new, (a, b) ->
                                a.put(b.getKey(), (int) Arrays.stream(b.getValue()).filter(filter).count())
                        , Map::putAll);
    }

    public int getCountWithFilter(Map<Kind, String[]> slots, Predicate<Object> filter) {

        return slots.entrySet().stream().collect(
                         HashMap::new, //supplier
                        (initMap, aMap) -> initMap.put(aMap.getKey(),(int) Arrays.stream(aMap.getValue()).filter(filter).count()), //accumulator
                        HashMap<Kind, Integer>::putAll) //combiner
                //count total
                .values().stream().reduce(0, (a, b) -> a += b);
        //  return total;
    }
}
