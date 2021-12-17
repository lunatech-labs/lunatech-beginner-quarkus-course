package com.lunatech.training.quarkus;

import java.util.AbstractList;
import java.util.List;

public class ProductList extends AbstractList<Product> {

    private List<Product> wrapped;

    private ProductList(List<Product> wrapped) {
        this.wrapped = wrapped;
    }

    public static ProductList wrap(List<Product> list) {
        return new ProductList(list);
    }

    @Override
    public Product get(int index) {
        return wrapped.get(index);
    }

    @Override
    public int size() {
        return wrapped.size();
    }
}