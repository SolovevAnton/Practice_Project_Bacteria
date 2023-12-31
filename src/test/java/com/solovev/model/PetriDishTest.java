package com.solovev.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class PetriDishTest {
    @Test
    public void creationMaxSize() {
        int size = PetriDish.MAX_SIZE;
        assertTrue(new PetriDish(size).getBacterias().containsKey(petriDish.new Address(size - 1, size - 1)));
        assertFalse(new PetriDish(size).getBacterias().containsKey(petriDish.new Address(size, size)));
    }

    @ParameterizedTest()
    @ValueSource(ints = {1, 2, 3, 5})
    public void creationTestNormal(int size) {
        assertTrue(new PetriDish(size).getBacterias().containsKey(petriDish.new Address(size - 1, size - 1)));
        assertFalse(new PetriDish(size).getBacterias().containsKey(petriDish.new Address(size, size)));
    }

    @Test
    public void creationEmptyTest() {
        assertEquals(new HashMap<>(), new PetriDish().getBacterias());
    }

    @Test
    public void creationTestThrows() {
        assertThrows(IllegalArgumentException.class, () -> new PetriDish(-1));
        assertThrows(IllegalArgumentException.class, () -> new PetriDish(PetriDish.MAX_SIZE + 1));
    }

    @Nested
    class oneDayBacteria {
        private ConfigurationOfBacteriaBehavior oneBacteriaDay = new ConfigurationOfBacteriaBehavior(1, 1, 0, 0);

        @Test
        public void calculateDaysOneBacteriaPot3x3() {
            assertEquals(new PetriDish.Response(9, 0), new PetriDish(3).calculateDays(oneBacteriaDay));
            assertEquals(new PetriDish.Response(1, 0), new PetriDish(3).calculateDays(new ConfigurationOfBacteriaBehavior(9, 9, 0, 0)));
            assertEquals(new PetriDish.Response(1, 0), new PetriDish(3).calculateDays(new ConfigurationOfBacteriaBehavior(50, 50, 0, 0)));

            assertEquals(new PetriDish.Response(3, 0), new PetriDish(3).calculateDays(new ConfigurationOfBacteriaBehavior(3, 3, 0, 0)));

            assertEquals(new PetriDish.Response(-1, 0), new PetriDish(3).calculateDays(new ConfigurationOfBacteriaBehavior(0, 0, 0, 0)));
        }

        @Test
        public void calcDaysEqualPut() {
            int size = 5;
            ConfigurationOfBacteriaBehavior confSizeIndayNoOther = new ConfigurationOfBacteriaBehavior(size, size, 0, 0);
            PetriDish dish = new PetriDish(size);
            assertEquals(size, dish.calculateDays(confSizeIndayNoOther).days());
        }

        @Test
        public void calculateDaysOneBacteriaPotMax() {

            assertEquals(new PetriDish.Response(PetriDish.MAX_SIZE, 0), new PetriDish(PetriDish.MAX_SIZE).calculateDays(new ConfigurationOfBacteriaBehavior(PetriDish.MAX_SIZE, PetriDish.MAX_SIZE, 0, 0)));
            assertEquals(new PetriDish.Response(PetriDish.MAX_SIZE * PetriDish.MAX_SIZE, 0), new PetriDish(PetriDish.MAX_SIZE).calculateDays(oneBacteriaDay));
            assertAll(() -> new PetriDish(PetriDish.MAX_SIZE).calculateDays(new ConfigurationOfBacteriaBehavior(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0)));
        }

        @Test
        public void calculateDaysOneBacteriaPotEmpty() {
            assertEquals(new PetriDish.Response(0, 0), petriDish.calculateDays(oneBacteriaDay));
            assertEquals(new PetriDish.Response(-1, 0), petriDish.calculateDays(new ConfigurationOfBacteriaBehavior(0, 0, 0, 0)));
        }
    }
    @Nested
    class NeighborsTests {
        private static Method fillNeighborsPot3x3;
        private static Supplier<Bacteria> bacteriaSupplier;
        private static Bacteria bacteriaToFill = new Bacteria();

        @BeforeAll
        private static void getMethod() throws NoSuchMethodException {
            fillNeighborsPot3x3 = dish3x3.getClass().getDeclaredMethod("fillNotEmptyNeighbors", PetriDish.Address.class, Supplier.class);
            fillNeighborsPot3x3.setAccessible(true);
            bacteriaSupplier = () -> bacteriaToFill;
        }

        @Test
        public void neighborsNoNeighbors() throws InvocationTargetException, IllegalAccessException {
            fillNeighborsPot3x3.invoke(petriDish, petriDish.new Address(0, 0), bacteriaSupplier); //00
            assertEquals(new HashMap<>(), petriDish.getBacterias());

            PetriDish onePot = new PetriDish(1);
            assertNull(onePot.getBacteria(0, 0));
        }

        @Test
        public void neighbors00AddressAllAreEmpty() throws InvocationTargetException, IllegalAccessException {
            // Address 0 0
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(0, 0), bacteriaSupplier);

            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 0));

            assertNull(dish3x3.getBacteria(0, 2));
            assertNull(dish3x3.getBacteria(1, 2));
            assertNull(dish3x3.getBacteria(2, 0));
            assertNull(dish3x3.getBacteria(2, 1));
            assertNull(dish3x3.getBacteria(2, 2));
        }

        @Test
        public void neighbors22AddressAllAreEmpty() throws InvocationTargetException, IllegalAccessException {
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(2, 2), bacteriaSupplier);

            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 2));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(2, 1));

            assertNull(dish3x3.getBacteria(0, 0));
            assertNull(dish3x3.getBacteria(0, 1));
            assertNull(dish3x3.getBacteria(0, 2));
            assertNull(dish3x3.getBacteria(1, 0));
            assertNull(dish3x3.getBacteria(2, 0));
        }

        @Test
        public void neighbors11AddressAllAreEmpty() throws InvocationTargetException, IllegalAccessException {
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(1, 1), bacteriaSupplier);

            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 0));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 2));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 0));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 2));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(2, 0));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(2, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(2, 2));

            assertNull(dish3x3.getBacteria(1, 1));
        }

        @Test
        public void neighbors11AddressAllFilled() throws InvocationTargetException, IllegalAccessException {
            Bacteria bacteriaToFill = new Bacteria();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    dish3x3.putBacteria(dish3x3.new Address(i, j), new Bacteria());
                }
            }
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(1, 1), bacteriaSupplier);

            assertNotSame(bacteriaToFill, dish3x3.getBacteria(0, 0));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(0, 1));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(0, 2));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(1, 0));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(1, 2));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(2, 0));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(2, 1));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(2, 2));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(1, 1));
        }

        @Test
        public void neighbors11AddressSomeFilled() throws InvocationTargetException, IllegalAccessException {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    dish3x3.putBacteria(dish3x3.new Address(i, j), new Bacteria());
                }
            }
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(1, 1), bacteriaSupplier);

            assertNotSame(bacteriaToFill, dish3x3.getBacteria(0, 0));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(0, 1));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(1, 0));
            assertNotSame(bacteriaToFill, dish3x3.getBacteria(1, 1));

            assertSame(bacteriaToFill, dish3x3.getBacteria(0, 2));
            assertSame(bacteriaToFill, dish3x3.getBacteria(1, 2));
            assertSame(bacteriaToFill, dish3x3.getBacteria(2, 0));
            assertSame(bacteriaToFill, dish3x3.getBacteria(2, 1));
            assertSame(bacteriaToFill, dish3x3.getBacteria(2, 2));


        }

        @Test
        public void neighbors10AddressAllAreEmpty() throws InvocationTargetException, IllegalAccessException {
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(1, 0), bacteriaSupplier);

            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 0));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(2, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(2, 0));

            assertNull(dish3x3.getBacteria(0, 2));
            assertNull(dish3x3.getBacteria(1, 2));
            assertNull(dish3x3.getBacteria(2, 2));
            assertNull(dish3x3.getBacteria(1, 0));
        }

        @Test
        public void neighbors01AddressAllAreEmpty() throws InvocationTargetException, IllegalAccessException {
            fillNeighborsPot3x3.invoke(dish3x3, dish3x3.new Address(0, 1), bacteriaSupplier);

            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 0));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 0));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 1));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(1, 2));
            assertEquals(bacteriaToFill, dish3x3.getBacteria(0, 2));

            assertNull(dish3x3.getBacteria(2, 0));
            assertNull(dish3x3.getBacteria(2, 1));
            assertNull(dish3x3.getBacteria(2, 2));
            assertNull(dish3x3.getBacteria(0, 1));
        }
    }
    @RepeatedTest(100)
    public void behaviorWithProbabilities(){
        PetriDish dish = new PetriDish(new Random().nextInt(PetriDish.MAX_SIZE));
        ConfigurationOfBacteriaBehavior randomConfig = randomConfiguratorSamePutNumber();
        ConfigurationOfBacteriaBehavior adjustedConfigAllDead = new ConfigurationOfBacteriaBehavior(randomConfig.fromNumber(), randomConfig.toNumber(), 1.0, 0);
        ConfigurationOfBacteriaBehavior adjustedConfigAllReproduce = new ConfigurationOfBacteriaBehavior(randomConfig.fromNumber(), randomConfig.toNumber(), 0, 1.0);
        PetriDish.Response original = dish.calculateDays(randomConfig);
        PetriDish.Response moreDays = dish.calculateDays(adjustedConfigAllDead);
        PetriDish.Response lessDays = dish.calculateDays(adjustedConfigAllReproduce);

        assertTrue(original.days() <= moreDays.days(), String.format("Size %d Original: %s Other: %s\nOriginal config: %s Other Config %s",dish.getSize(),original,moreDays,randomConfig,adjustedConfigAllDead));
        assertTrue(original.deadBacteria() <= moreDays.deadBacteria(),String.format("Size %d Original: %s Other: %s\nOriginal config: %s Other Config %s",dish.getSize(),original,moreDays,randomConfig,adjustedConfigAllDead));

        assertTrue(original.days() >= lessDays.days(),String.format("Size %d Original: %s Other: %s\nOriginal config: %s Other Config %s",dish.getSize(),original,lessDays,randomConfig,adjustedConfigAllReproduce));
    }

    private static PetriDish petriDish;
    private static PetriDish dish3x3 = new PetriDish(3);

    /**
     * Reloads all pots to the initial state
     */
    @BeforeEach
    private void reloadDishes() {
        petriDish = new PetriDish();
        dish3x3 = new PetriDish(3);
    }

    private ConfigurationOfBacteriaBehavior randomConfiguratorSamePutNumber() {
        Random rand = new Random();
        int fromNumber = rand.nextInt(PetriDish.MAX_SIZE+ 1);
        return new ConfigurationOfBacteriaBehavior(fromNumber,fromNumber, rand.nextDouble(), rand.nextDouble());
    }

}