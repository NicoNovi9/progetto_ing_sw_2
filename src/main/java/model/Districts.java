package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Districts {
    private Set<String> districts;

    public Districts() {
        districts = new HashSet<>();
    }

    public void addDistrict(String district) {
        districts.add(district.toLowerCase());
    }

    public boolean match(String district) {
        return districts.contains(district.toLowerCase());
    }

    //gestire con lancio eccezione?
    public String getDistrictByIndex(int index) {
        ArrayList<String> lista = new ArrayList<>(districts);
        if (index >= 0 && index < lista.size())
            return lista.get(index);
        else return null;

    }


    public Set<String> getDistricts() {
        return districts;
    }

    public boolean isEmpty() {
        return districts.isEmpty();
    }
}


