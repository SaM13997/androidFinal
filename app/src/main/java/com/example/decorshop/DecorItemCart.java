package com.example.decorshop;

import java.util.ArrayList;
import java.util.List;

public class DecorItemCart {
    private static DecorItemCart instance;
    private List<DecorItem> decorItems;

    private DecorItemCart() {
        decorItems = new ArrayList<>();
    }

    public static DecorItemCart getInstance() {
        if (instance == null) {
            instance = new DecorItemCart();
        }
        return instance;
    }

    public void addDecorItem(DecorItem decorItem) {
        decorItems.add(decorItem);
    }


    public List<DecorItem> getDecorItems() {
        return decorItems;
    }
}
