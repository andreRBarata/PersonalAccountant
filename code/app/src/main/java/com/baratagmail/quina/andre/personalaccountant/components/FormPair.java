package com.baratagmail.quina.andre.personalaccountant.components;

import java.util.TreeMap;

/**
 * Created by andre on 15-11-2015.
 *
 * Custom TreeMap make to be easier to initialize and
 * be usable in spinners (the toString returns a specific string to display)
 */
public class FormPair extends TreeMap<String,String> {

    public FormPair(String... pairs) {
        for (int i = 0; i < pairs.length; i += 2) {
            if (pairs.length > i + 1) {
                this.put(pairs[i], pairs[i+1]);
            }
        }
    }

    public String toString() {
        return this.get("name");
    }
}
