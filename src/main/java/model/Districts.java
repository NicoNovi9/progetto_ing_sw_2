package model;

import java.util.HashSet;
import java.util.Set;

public class Districts {
    private final Set<String> districts;

    public Districts() {
        districts = new HashSet<>();
    }

    public void addDistrict(String district) {
        districts.add(district.toLowerCase());
    }

    public boolean match(String district) {
        return districts.contains(district.toLowerCase());
    }

    public Set<String> getDistricts() {
        return districts;
    }

    public boolean isEmpty() {
        return districts.isEmpty();
    }
}


