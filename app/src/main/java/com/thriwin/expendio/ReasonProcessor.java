package com.thriwin.expendio;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReasonProcessor extends Processor {
    private SharedPreferences localStorageForPreferences;

    private HashMap<String, Boolean> prepositions = new HashMap<String, Boolean>() {{
        put("with", true);
        put("at", true);
        put("from", true);
        put("into", true);
        put("during", true);
        put("including", true);
        put("until", true);
        put("against", true);
        put("among", true);
        put("throughout", true);
        put("despite", true);
        put("towards", true);
        put("upon", true);
        put("concerning", true);
        put("for", true);
        put("on", true);
        put("by", true);
        put("about", true);
        put("like", true);
        put("through", true);
        put("over", true);
        put("before", true);
        put("between", true);
        put("after", true);
        put("since", true);
        put("without", true);
        put("under", true);
        put("within", true);
        put("along", true);
        put("following", true);
        put("across", true);
        put("behind", true);
        put("beyond", true);
        put("plus", true);
        put("except", true);
        put("but", true);
        put("up", true);
        put("out", true);
        put("around", true);
        put("down", true);
        put("off", true);
        put("above", true);
        put("near", true);
    }};
    private HashMap<String, Boolean> currencies = new HashMap<String, Boolean>() {{
        put("afghani", true);
        put("euro", true);
        put("lek", true);
        put("dinar", true);
        put("dollar", true);
        put("kwanza", true);
        put("peso", true);
        put("dram", true);
        put("florin", true);
        put("pound", true);
        put("manat", true);
        put("taka", true);
        put("ruble", true);
        put("franc", true);
        put("ngultrum", true);
        put("boliviano", true);
        put("mark", true);
        put("pula", true);
        put("real", true);
        put("lev", true);
        put("escudo", true);
        put("riel", true);
        put("Renminbi", true);
        put("colon", true);
        put("kuna", true);
        put("guilder", true);
        put("koruna", true);
        put("krone", true);
        put("nakfa", true);
        put("lilangeni", true);
        put("birr", true);
        put("krona", true);
        put("dalasi", true);
        put("lari", true);
        put("cedi", true);
        put("quetzal", true);
        put("Pound", true);
        put("gourde", true);
        put("lempira", true);
        put("forint", true);
        put("rupee", true);
        put("rupees", true);
        put("rupiah", true);
        put("Right)", true);
        put("rial", true);
        put("shekel", true);
        put("yen", true);
        put("tenge", true);
        put("shilling", true);
        put("som", true);
        put("kip", true);
        put("loti", true);
        put("pataca", true);
        put("denar", true);
        put("ariary", true);
        put("kwacha", true);
        put("ringgit", true);
        put("rufiyaa", true);
        put("ouguiya", true);
        put("leu", true);
        put("tugrik", true);
        put("dirham", true);
        put("metical", true);
        put("kyat", true);
        put("cordoba", true);
        put("naira", true);
        put("won", true);
        put("kina", true);
        put("guarani", true);
        put("sol", true);
        put("zloty", true);
        put("riyal", true);
        put("tala", true);
        put("dobra", true);
        put("leone", true);
        put("rand", true);
        put("sterling", true);
        put("somoni", true);
        put("baht", true);
        put("pa’anga", true);
        put("lira", true);
        put("hryvnia", true);
        put("vatu", true);
        put("bolivar", true);
        put("dong", true);
        put("cents", true);
        put("centimos", true);
        put("centavos", true);
        put("gopik", true);
        put("fils", true);
        put("paisa", true);
        put("chetrum", true);
        put("pfenings", true);
        put("thebe", true);
        put("stotinki", true);
        put("fen", true);
        put("lipa", true);
        put("haler", true);
        put("øre", true);
        put("piastres", true);
        put("senti", true);
        put("pence", true);
        put("bututs", true);
        put("tetri", true);
        put("pesewas", true);
        put("centimes", true);
        put("paise", true);
        put("agorot", true);
        put("qirsh", true);
        put("santims", true);
        put("dinars", true);
        put("centas", true);
        put("avos", true);
        put("deni", true);
        put("tambala", true);
        put("sen", true);
        put("laari", true);
        put("khoums", true);
        put("bani", true);
        put("baiza", true);
        put("toea", true);
        put("céntimos", true);
        put("groszy", true);
        put("dirhams", true);
        put("kopeks", true);
        put("sene", true);
        put("halala", true);
        put("piasters", true);
        put("öre", true);
        put("satang", true);
        put("seniti", true);
        put("millimes", true);
        put("Kurus", true);
        put("tenge", true);
        put("kopiyok", true);
        put("centécimos", true);
        put("tiyn", true);
        put("ngwee", true);
    }};

    public ReasonProcessor(SharedPreferences localStorageForPreferences) {
        this.localStorageForPreferences = localStorageForPreferences;
    }

    public List<String> extract(StringBuilder expModifiable) {
        String[] allWords = Utils.splitStatementBy(expModifiable.toString(), " ");
        String tags = localStorageForPreferences.getString(Utils.TAGS, "{}");
        ArrayList<String> reasons = new ArrayList<>();

        for (String word : allWords) {
            if (word.length() >= 3) {
                Boolean isPrepositionPresent = prepositions.get(word.toLowerCase());
                Boolean isCurrencyPresent = currencies.get(word.toLowerCase());
                if (Utils.isNull(isPrepositionPresent) && Utils.isNull(isCurrencyPresent)) {
                    reasons.add(word);
                }
            }
        }

        return reasons;
    }
}
