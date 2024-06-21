package whitebox;
import model.ConversionFactors;
import model.Factor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class AddFactorTest {

        private ConversionFactors testClass;

        @BeforeEach
        void setUp() {
            testClass = new ConversionFactors();
        }

        @Test
        void testAddFactor_SingleFactor() { //path coverage primo if

            testClass.addFirstFactor("id1");
            testClass.addFactor("id1", 2.0, "id2");

            assertTrue(testClass.isInitialized());
            assertEquals(1, testClass.getFactors().size());
            assertEquals(new Factor("id2", roundTo2Decimal(0.5), "id1"), testClass.getFactors().get(0));
        }
         @Test
        void testAddFactor_2Factors() { // primo path coverage else

            testClass.getFactors().add(new Factor("id2", roundTo2Decimal(roundTo2Decimal(1 / 2.0)), "id1"));
            testClass.inverInitialized();

            testClass.addFactor("id3", 0.5, "id1");

            assertEquals(3, testClass.getFactors().size());
            assertTrue(testClass.getFactors().contains(new Factor("id2", roundTo2Decimal(0.5), "id1")));
            assertTrue(testClass.getFactors().contains(new Factor("id3", roundTo2Decimal(0.5), "id1")));
            assertTrue(testClass.getFactors().contains(new Factor("id3", roundTo2Decimal(1.0), "id2")));
        }
        @Test
        void testAddFactor_2Factors2() { //secondo path coverage else

            testClass.getFactors().add(new Factor("id2", roundTo2Decimal(roundTo2Decimal(1 / 2.0)), "id1"));
            testClass.inverInitialized();

            testClass.addFactor("id3", 0.5, "id2");

            assertEquals(3, testClass.getFactors().size());
            assertTrue(testClass.getFactors().contains(new Factor("id2", roundTo2Decimal(0.5), "id1")));
            assertTrue(testClass.getFactors().contains(new Factor("id3", roundTo2Decimal(0.5), "id2")));
            assertTrue(testClass.getFactors().contains(new Factor("id3", roundTo2Decimal(0.25), "id1")));
        }


       @Test
        void testAddFactor_3Factors() {

            testClass.getFactors().add(new Factor("id2", roundTo2Decimal(roundTo2Decimal(1 / 2.0)), "id1"));
            testClass.getFactors().add(new Factor("id3", roundTo2Decimal(0.5), "id1"));
            testClass.getFactors().add(new Factor("id3", roundTo2Decimal(1.0), "id2"));
            testClass.inverInitialized();

            testClass.addFactor("id4", 1.0, "id2");


            assertEquals(6, testClass.getFactors().size());

            assertTrue(testClass.getFactors().contains(new Factor("id2", roundTo2Decimal(0.5), "id1")));
            assertTrue(testClass.getFactors().contains(new Factor("id3", roundTo2Decimal(0.5), "id1")));
            assertTrue(testClass.getFactors().contains(new Factor("id3", roundTo2Decimal(1.0), "id2")));
            assertTrue(testClass.getFactors().contains(new Factor("id4", roundTo2Decimal(1.0), "id2")));
            assertTrue(testClass.getFactors().contains(new Factor("id4", roundTo2Decimal(0.5), "id1")));
            assertTrue(testClass.getFactors().contains(new Factor("id4", roundTo2Decimal(1.0), "id3")));
        }



        private double roundTo2Decimal(double value) {
            return Math.round(value * 100.0) / 100.0;
        }
}
