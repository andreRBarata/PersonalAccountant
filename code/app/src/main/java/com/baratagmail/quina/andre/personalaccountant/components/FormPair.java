package com.baratagmail.quina.andre.personalaccountant.components;

import java.util.TreeMap;

/**
 * Created by andre on 15-11-2015.
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
