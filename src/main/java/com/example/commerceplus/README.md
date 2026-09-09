
# 인덱스 

테스트 결과 인덱스는 4번 테스트에서 가장 좋은 평가를 받은 (category,created_at)로 선택을 하였다.

이유는 시나리오 A, B에 대하여 행이 5만 -> 5천으로 줄었으며
Using filesort 가 가장 적으면서
인덱스를 넣어야 할게 category와 created_at 밖에 없기 때문이다.

---

■ 1. 테스트 상태: 인덱스 없음

1. 시나리오 A (카테고리+가격+정렬)
   - type: all / rows: 49820 / Extra: Using where; Using filesort

2. 시나리오 B (카테고리+정렬)
   - type: all / rows: 49820 / Extra: Using where; Using filesort

3. 시나리오 C (가격만+정렬)
   - type: ALL  / rows: 49820 / Extra: Using where; Using filesort

---

■ 2. 테스트 상태: 인덱스 (category) 적용 후

1. 시나리오 A (카테고리+가격+정렬)
   - type: ref / rows: 5032 / Extra: Using index condition; Using where; Using filesort

2. 시나리오 B (카테고리+정렬)
   - type: ref / rows: 5032 / Extra: Using index condition; Using filesort

3. 시나리오 C (가격만+정렬)
   - type: ALL  / rows: 49820 / Extra: Using where; Using filesort

---

■ 3. 테스트 상태: 인덱스 (category,price) 적용 후

1. 시나리오 A (카테고리+가격+정렬)
    - type: range / rows: 192 / Extra: Using index condition; Using filesort

2. 시나리오 B (카테고리만+정렬)
    - type: ref / rows: 5032 / Extra: Using index condition; Using filesort

3. 시나리오 C (가격만+정렬)
    - type: ALL  / rows: 49820 / Extra: Using where; Using filesort

---

■ 4. 테스트 상태: 인덱스 (category,created_at) 적용 후 (선택)

1. 시나리오 A (카테고리+가격+정렬)
   - type: ref / rows: 5032 / Extra: Using index condition; Using where; Backward index scan

2. 시나리오 B (카테고리만+정렬)
   - type: ref / rows: 5032 / Extra: Using where; Backward index scan

3. 시나리오 C (가격만+정렬)
   - type: ALL  / rows: 49820 / Extra: Using where; Using filesort

---

■ 5. 테스트 상태: 인덱스 (category,created_at, price) 적용 후

1. 시나리오 A (카테고리+가격+정렬)
   - type: ref / rows: 5032 / Extra: Using where; Backward index scan

2. 시나리오 B (카테고리만+정렬)
   - type: ref / rows: 5032 / Extra: Using where; Backward index scan

3. 시나리오 C (가격만+정렬)
   - type: ALL  / rows: 49820 / Extra: Using where; Using filesort
