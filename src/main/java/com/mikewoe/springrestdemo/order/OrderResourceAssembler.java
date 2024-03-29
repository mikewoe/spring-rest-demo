package com.mikewoe.springrestdemo.order;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class OrderResourceAssembler implements ResourceAssembler<Order, Resource<Order>> {

    @Override
    public Resource<Order> toResource(Order order) {
        Resource<Order> resource = new Resource<>(
                order,
                linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel("orders")
        );

        if (order.getStatus() == Status.IN_PROGRESS) {
            resource.add(
                    linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"),
                    linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete")
            );
        }

        return resource;
    }
}
