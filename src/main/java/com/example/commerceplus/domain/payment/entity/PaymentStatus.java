package com.example.commerceplus.domain.payment.entity;


// 결제 상태 전이 규칙
// PAYMENT_PENDING → COMPLETED / FAILED
// COMPLETED       → CANCELED
// FAILED          → (종료)
// CANCELED        → (종료)
public enum PaymentStatus {

    PAYMENT_PENDING {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return target == COMPLETED || target == FAILED;
        }
    },

    COMPLETED {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return target == CANCELED;
        }
    },

    FAILED {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return false;
        }
    },

    CANCELED {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(PaymentStatus target);
}