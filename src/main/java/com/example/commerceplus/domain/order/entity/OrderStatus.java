package com.example.commerceplus.domain.order.entity;

public enum OrderStatus {
    PAYMENT_PENDING {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return target == COMPLETED || target == CANCELLED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return target == CANCELED;
        }
    },
    CANCELLED {
        public boolean canTransitTo(OrderStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(OrderStatus target);
}
