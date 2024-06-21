package model;

import java.util.ArrayList;

public class ConversionFactors {
    private final ArrayList<Factor> factors;
    boolean initialized;

    public ConversionFactors() {
        factors = new ArrayList<>();
        initialized = false;
    }

    public void addFirstFactor(String id) {
        factors.add(new Factor(id, -1, null));
    }

    public void addFactor(String id1, Double factor, String id2) {

        if (factors.size() == 1 && !initialized) {
            factors.set(0, new Factor(id2, roundTo2Decimal(roundTo2Decimal(1 / factor)), id1));
            initialized = true;
        } else {

            factors.add(new Factor(id1, roundTo2Decimal(factor), id2));
            ArrayList<Factor> newFactors = new ArrayList<>();

            for (Factor f1 : factors) {
                if (f1.third().equals(id2) && !f1.first().equals(id1)) {
                    double newFactor = roundTo2Decimal(1 / f1.second() * (factor));
                    newFactors.add(new Factor(id1, newFactor, f1.first()));

                } else if (f1.first().equals(id2)) {
                    double newFactor = roundTo2Decimal(f1.second() * factor);
                    newFactors.add(new Factor(id1, newFactor, f1.third()));
                }

            }
            factors.addAll(newFactors);
        }
    }

    public double findTripleValue(String firstValue, String thirdValue) {
        for (Factor t : factors) {
            if (t.first().equals(firstValue) && t.third().equals(thirdValue))
                return t.second();
            if (t.first().equals(thirdValue) && t.third().equals(firstValue))
                return roundTo2Decimal(1 / t.second());
        }
        return -1;
    }

    public ArrayList<Factor> findAllTripleValuePerLeaf(String value) {
        ArrayList<Factor> temp = new ArrayList<>();
        for (Factor t : factors) {
            if (t.first().equals(value))
                temp.add(t);
            else if (t.third().equals(value)) {
                Factor s = new Factor(value, roundTo2Decimal(1 / t.second()), t.first());
                temp.add(s);
            }
        }
        return temp;
    }

    private double roundTo2Decimal(double d) {
        return Math.round(d * 100.0) / 100.0;

    }

    public ArrayList<Factor> getFactors() {
        return factors;
    }

    public boolean isEmpty() {
        return factors.isEmpty();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void inverInitialized() {
        this.initialized = !this.initialized;
    }

}