package com.example.commerceplus.concurrency;

import com.example.commerceplus.domain.cart.entity.Cart;
import com.example.commerceplus.domain.cart.entity.CartItem;
import com.example.commerceplus.domain.cart.repository.CartItemRepository;
import com.example.commerceplus.domain.cart.repository.CartRepository;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.member.entity.MemberRole;
import com.example.commerceplus.domain.member.repository.MemberRepository;
import com.example.commerceplus.domain.order.dto.request.CreateOrderRequest;
import com.example.commerceplus.domain.order.repository.OrderRepository;
import com.example.commerceplus.domain.order.service.OrderFacade;
import com.example.commerceplus.domain.order.service.OrderService;
import com.example.commerceplus.domain.product.entity.Product;
import com.example.commerceplus.domain.product.entity.ProductCategory;
import com.example.commerceplus.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em; //

    @Test
    void 재고_불변식을_검증한다() throws Exception {
        log.info("시작");
        int threadCount = 100;
        int initialStock = 10;

        long beforeOrderCount = orderRepository.count();

        Product product = productRepository.saveAndFlush(
                Product.create("test1", 100, initialStock, "test1", ProductCategory.ETC)
        );

        List<Long> memberIds = new ArrayList<>();
        List<CreateOrderRequest> requests = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Member member = memberRepository.saveAndFlush(
                    Member.createNormalMember(
                            "test" + i,
                            "1234",
                            "test" + i,
                            "011-1112-111" + i
                    )
            );

            Cart cart = cartRepository.saveAndFlush(Cart.create(member));
            CartItem cartItem = cartItemRepository.saveAndFlush(
                    CartItem.createCartItem(cart, product, 1)
            );

            memberIds.add(member.getId());
            requests.add(new CreateOrderRequest(List.of(cartItem.getId())));
        }

        em.clear();

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            int index = i;

            executorService.submit(() -> {
                readyLatch.countDown();

                try {
                    // 모든 스레드가 이 지점까지 도착할 때까지 대기
                    startLatch.await();
                    orderFacade.createOrder(
                            memberIds.get(index),
                            requests.get(index)
                    );

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    log.info("주문 실패: {}", e.getMessage());

                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 작업 스레드가 준비될 때까지 대기
        readyLatch.await();

        // 동시에 출발
        startLatch.countDown();

        // 모든 작업 종료 대기
        doneLatch.await();

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);

        Product resultProduct =
                productRepository.findById(product.getId())
                        .orElseThrow();

        long successOrderCount =
                orderRepository.count() - beforeOrderCount;

        log.info("성공 카운트 = {}", successCount.get());
        log.info("저장된 주문 수 = {}", successOrderCount);
        log.info("남은 재고 = {}", resultProduct.getStock());

        assertThat(successCount.get()).isLessThanOrEqualTo(initialStock);
        assertThat(resultProduct.getStock())
                .isEqualTo(initialStock - successCount.get());

        assertThat(successOrderCount)
                .isEqualTo(successCount.get());
    }

}
