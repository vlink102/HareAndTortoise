package me.vlink102.objects;

import java.util.Comparator;
import java.util.UUID;

public class ContestantModelComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Number n1 && o2 instanceof Number n2) {
            return Double.compare(n1.doubleValue(), n2.doubleValue());
        }
        if (o1 instanceof UUID u1 && o2 instanceof UUID u2) {
            return u1.compareTo(u2);
        }
        if (o1 instanceof String s1 && o2 instanceof String s2) {
            String regex = "^-?[0-9]\\d*(\\.\\d+)?$";
            if (s1.matches(regex) && s2.matches(regex)) {
                return Double.valueOf(s1).compareTo(Double.valueOf(s2));
            }
        }
        if (o1 instanceof Comparable c1 && o2 instanceof Comparable<?> c2) {
            return c1.compareTo(c2);
        }
        return o1.toString().compareTo(o2.toString());
    }
}
