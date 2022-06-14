package putumas.sam.co;

/*
Design a parking lot using object-oriented principles

Goals:
- Your solution should be in Java - if you would like to use another language, please let the interviewer know.
- Boilerplate is provided. Feel free to change the code as you see fit

Assumptions:
- The parking lot can hold motorcycles, cars and vans
- The parking lot has motorcycle spots, car spots and large spots
- A motorcycle can park in any spot
- A car can park in a single compact spot, or a regular spot
- A van can park, but it will take up 3 regular spots
- These are just a few assumptions. Feel free to ask your interviewer about more assumptions as needed

Here are a few methods that you should be able to run:
- Tell us how many spots are remaining
- Tell us how many total spots are in the parking lot
- Tell us when the parking lot is full
- Tell us when the parking lot is empty
- Tell us when certain spots are full e.g. when all motorcycle spots are taken
- Tell us how many spots vans are taking up

Hey candidate! Welcome to your interview. I'll start off by giving you a Solution class. To run the code at any time, please hit the run button located in the top left corner.
*/

import java.util.*;
import java.util.function.*;

class LotUtility {
    //private static

    /**
     * slots is the map Kind to String[]
     * filter is the expression for filtering the array element
     * return is the map kind to Integer
     */
    public static Map<Kind, Integer> countByKindWithFilter(Map<Kind, String[]> slots, Predicate<Object> filter) {
        return slots.entrySet().stream().collect(
                HashMap::new,//supplier
                (initMap, aMap) -> initMap.put(aMap.getKey(), (int) Arrays.stream(aMap.getValue()).filter(filter).count()), //accumulator
                //(finalMap,anotherMap)->finalMap.putAll(anotherMap))//combiner
                //same as below
                HashMap::putAll)//combiner
                ;
    }

    public static int countWithFilter(Map<Kind, String[]> slots, Predicate<Object> filter) {
        return countByKindWithFilter(slots, filter).values().stream().reduce(0, (initTotal, currentVal) -> initTotal += (int) currentVal);
    }

    public static Map<Kind, Integer> countByKind(Map<Kind, String[]> slots) {
        return slots.entrySet().stream().collect(HashMap::new, (initMap, aMap) -> initMap.put(aMap.getKey(), (int) Arrays.stream(aMap.getValue()).count()), (finalMap, anotherMap) -> finalMap.putAll(anotherMap));
    }

    //return spot starting index
    public static Map<Kind, Integer> getSpots(Kind forKind, Map<Kind, String[]> slots) {
        //lookup the String

        Map<Kind, Integer> kindLotIndexStart = slots.entrySet().stream().filter(p -> forKind != p.getKey()).collect(HashMap::new, (a, b) -> a.put(b.getKey(), Arrays.stream(b.getValue()).reduce("", (p, x) -> p += x).indexOf(forKind.spotLayout)), HashMap::putAll);

        return kindLotIndexStart;

    }

}

class LotFullException extends Exception {
    public LotFullException(String aMessage) {
        super(aMessage);
    }
}

//provide the behavior for a vehicle
//in term of what spot it will require
enum Kind {

    MO(1, "o"),
    CAR(2, "oo"),
    VAN(3, "ooo");
    int spotSize = 0;
    String spotLayout;

    Kind(int size, String spot) {
        spotSize = size;
        spotLayout = spot;
    }

}

//initialized with the corresponding
//kind.
//It is a super class for all supported
//vehicle
class Vehicle {
    private Kind kind;

    public Vehicle(Kind aKind) {
        this.kind = aKind;
    }

    public Kind getKind() {
        return this.kind;
    }

    public String toString() {
        return "Vehicle " + this.kind;
    }
}

class Car extends Vehicle {
    public Car() {
        super(Kind.CAR);
    }

}

class MotorCycle extends Vehicle {
    public MotorCycle() {
        super(Kind.MO);
    }
}

class Van extends Vehicle {
    public Van() {
        super(Kind.VAN);
    }

}

class LotManager {
    int maxMoSize = 0;
    int maxCarSize = 0;
    int maxVanSize = 0;

    public LotManager(int carSpotSize,
                      int moSpotSize,
                      int vanSpotSize) {
        this.maxMoSize = moSpotSize;
        this.maxCarSize = carSpotSize;
        this.maxVanSize = vanSpotSize;
        slots.put(Kind.MO, new String[moSpotSize]);
        slots.put(Kind.CAR, new String[carSpotSize]);
        slots.put(Kind.VAN, new String[vanSpotSize]);

        //initialize with o
        Arrays.fill(slots.get(Kind.MO), 0, slots.get(Kind.MO).length, "o");
        Arrays.fill(slots.get(Kind.CAR), 0, slots.get(Kind.CAR).length, "o");
        Arrays.fill(slots.get(Kind.VAN), 0, slots.get(Kind.VAN).length, "o");

    }
    //to request for parking slot.
    //it assumes that the vehicle can
    //be car, motorcycle or a van
    //otherwise it will reject with
    //vehicle is not supported exception

    private Map<Kind, String[]> slots = new HashMap<>();

    public Map<Kind, Integer> getAllSpots() {
        return LotUtility.countByKind(slots);
    }

    public boolean isSpotsTakenUpByKind(Kind aKind) {
        //reuse the existing method
        return LotUtility.countByKindWithFilter(slots, "x"::equals).get(aKind) == this.getAllSpots().get(aKind);
    }

    public int getTakenUpSpotsByKind(Kind aKind) {
        //reuse the existing method
        return LotUtility.countByKindWithFilter(slots, "x"::equals).get(aKind);
    }

    public boolean isLotFull() {
        return LotUtility.countWithFilter(slots, "x"::equals) == this.maxCarSize + this.maxMoSize + this.maxVanSize;
    }

    public boolean isLotEmpty() {
        return LotUtility.countWithFilter(slots, "o"::equals) == this.maxCarSize + this.maxMoSize + this.maxVanSize;
    }

    public void parkAtXAvailableSpotsByKind(Kind aKind) throws LotFullException {
        Map<Kind, Integer> spotsIndex = LotUtility.getSpots(aKind, slots);
        if (spotsIndex.values().stream().allMatch(p -> p == -1)) {
            throw new LotFullException("not available spots for " + aKind);
        }
        Map.Entry<Kind, Integer> entry = spotsIndex.entrySet().stream().filter(p -> p.getValue() > -1).findFirst().get();
        //lets park
        Kind xKind = entry.getKey();
        Integer startingIdx = entry.getValue();
        Arrays.fill(slots.get(xKind), startingIdx, startingIdx + xKind.spotSize + 1, "x");
    }

    public Map<Kind, Integer> getAvailableSpots() {
        //declare the variable
        //Map<Kind,Integer> availableSlots=new HashMap<>();
        //use the stream api to filter the "o" then count
        //supplier: initialise the HashMap
        //accumulator: populate the hashmap key -> count key by key
        //combiner: combine the map
//  Map<Kind,Integer> availableSlots= slots.entrySet().stream().
//  collect(HashMap::new,
//          (kindToInt,kindBySlots)->kindToInt.put(kindBySlots.getKey(),(int)Arrays.stream(kindBySlots.getValue()).filter("o"::equals).count()),
//          (a,b)->a.putAll(b));
        return LotUtility.countByKindWithFilter(slots, "o"::equals);
    }

    public int park(Vehicle vehicle) throws LotFullException {
        boolean parked = false;
        System.out.println("Lets park " + vehicle);
        //check the availibility of a slot from the correct slot pool
        Kind vehicleKind = vehicle.getKind();
        //for the moment keep the pool as the string array. the o means available, x means taken.
        //if all are taken mean, the all spots fo
        if (!this.isSpotsTakenUpByKind(vehicleKind)) {
            String[] kindPool = this.slots.get(vehicleKind);

            for (int k = 0; k < kindPool.length; k++) {
                if (kindPool[k].equals("o")) {
                    kindPool[k] = "x";
                    parked = true;
                    break;
                }
            }
        } else {
            //treat differently
            this.parkAtXAvailableSpotsByKind(vehicleKind);

        }

        if (!parked) {
            throw new LotFullException("The lot for " + vehicleKind + " is full");
        }

        return 0;
    }
}

class Solution {

    public static void main(String[] args) {


        try {
//initialise the slots pool with car, motor and van size respectively
            LotManager lotManager = new LotManager(20, 20, 20);


            lotManager.park(new MotorCycle());
            System.out.println("available spots");
            System.out.println(lotManager.getAvailableSpots());
            lotManager.park(new Car());
            lotManager.park(new Van());
            lotManager.park(new MotorCycle());
            System.out.println("available spots");
            System.out.println(lotManager.getAvailableSpots());
            System.out.println("all spots");
            System.out.println(lotManager.getAllSpots());
            System.out.println("Is the parking lot full? " + lotManager.isLotFull());
            System.out.println("Is the parking lot empty? " + lotManager.isLotEmpty());
            //for a specific spots
            System.out.println("is van spots is taken up? " + lotManager.isSpotsTakenUpByKind(Kind.VAN));
            System.out.println("is motorcycle spots is taken up? " + lotManager.isSpotsTakenUpByKind(Kind.MO));

            lotManager.parkAtXAvailableSpotsByKind(Kind.VAN);
            System.out.println("available spots");
            System.out.println(lotManager.getAvailableSpots());


        } catch (LotFullException lfe) {
            System.out.println(lfe.getMessage());
        }
    }
}

