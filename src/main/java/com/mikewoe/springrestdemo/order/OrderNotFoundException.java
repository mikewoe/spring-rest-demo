package com.mikewoe.springrestdemo.order;

public class OrderNotFoundException extends RuntimeException {

    OrderNotFoundException(Long id) {
        super("Could not find order with " + id + " id");
    }
}
