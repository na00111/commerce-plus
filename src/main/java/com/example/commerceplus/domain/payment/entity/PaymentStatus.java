package com.example.commerceplus.domain.payment.entity;


// 결제 상태 전이 규칙
// PAYMENT_PENDING → COMPLETED / FAILED
// COMPLETED       → CANCELED
// FAILED          → (종료)
// CANCELED        → (종료)

//서버가 검증 ,처리를 마친 뒤 저장한 결제 결과
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