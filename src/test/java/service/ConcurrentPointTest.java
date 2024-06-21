package service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentPointTest {

  /*
        -기능 요구사항 : 동시에 여러 요청이 발생했을 때 이를 적절히 동기화해야 한다.
                      동시성 테스트는 통합테스트로 진행한다.

        -실패 케이스 [1] : 여러 요청 발생시 결과값이 다르면 실패한다. (동기화 되지 않은 경우)

        궁금증 : 성공케이스는 PointService에 synchronized 키워드를 이용해 동기화 처리를 하였습니다.
                그런데 실패케이스를 위해서 PointService를 임의로 synchronized 가 없는 메서드를 재정의하여
                테스트를 해야하는지 궁금합니다.

                여기서는 해당 실패 케이스 테스트를 위해 PointService 인터페이스를 재정의하였고
                charge 와 use에 대해서 유효성 검증부분을 제외하고 진행하였습니다.

   */

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;

    PointService pointService;

    @BeforeEach
    public void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointServiceImpl(userPointTable , pointHistoryTable);
    }


    @Test
    public void 사용자_1명이_포인트를_동시에_충전하고_사용하는_성공케이스() throws InterruptedException {

        int threadCount = 10 ; //동시에 10번의 요청이 들어온다면?
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        long id = 1L;
        long initialPoints = 100L;
        UserPoint userPoint = pointService.charge(id , initialPoints);  //초기세팅

        for (int i = 0; i < threadCount; i++) {

            executorService.execute(() -> {
                try {
                    pointService.charge(id, 10);
                    pointService.use(id, 5);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS); //10초 대기하도록 설정
        executorService.shutdown();

        long expectedFinalPoints = initialPoints + 10L * threadCount - 5L * threadCount;
        UserPoint finalUserPoint = pointService.search(id);
        Assertions.assertEquals(expectedFinalPoints, finalUserPoint.point());
    }

    @Test
    public void 동기화되지_않은_실패케이스() throws InterruptedException { //no synchronized
        pointService = new PointServiceNoSyncImpl();

        int threadCount = 10 ; //동시에 10번의 요청이 들어온다면?
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        long id = 1L;
        long initialPoints = 100L;
        UserPoint userPoint = pointService.charge(id , initialPoints);  //초기세팅

        for (int i = 0; i < threadCount; i++) {

            executorService.execute(() -> {
                try {
                    pointService.charge(id, 10);
                    pointService.use(id, 5);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS); //10초 대기하도록 설정
        executorService.shutdown();

        long expectedFinalPoints = initialPoints + 10L * threadCount - 5L * threadCount;
        UserPoint finalUserPoint = pointService.search(id);
        Assertions.assertNotEquals(expectedFinalPoints, finalUserPoint.point());


    }

    public class PointServiceNoSyncImpl implements PointService {
        @Override
        public UserPoint charge(long id, long amount) {
            UserPoint userPoint = search(id);
            UserPoint newUserPoint = new UserPoint(id , userPoint.point() + amount , System.currentTimeMillis());
            userPointTable.insertOrUpdate(id , newUserPoint.point());
            pointHistoryTable.insert(id , newUserPoint.point() , TransactionType.CHARGE , System.currentTimeMillis());
            return newUserPoint;
        }

        @Override
        public UserPoint use(long id, long amount) {
            UserPoint userPoint = search(id);
            long remainAmount = userPoint.point() - amount ;
            Assert.isTrue(remainAmount >= 0, "잔액이 부족합니다.");

            UserPoint newUserPoint = new UserPoint(id , remainAmount , System.currentTimeMillis());
            userPointTable.insertOrUpdate(id , newUserPoint.point());
            pointHistoryTable.insert(id , newUserPoint.point() , TransactionType.USE , System.currentTimeMillis());
            return newUserPoint;
        }

        @Override
        public UserPoint search(long id) {
            UserPoint userPoint = userPointTable.selectById(id);
            if(userPoint == null) throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            return userPoint;
        }

        @Override
        public List<PointHistory> historySearch(long id) {return null; }
    };

}

