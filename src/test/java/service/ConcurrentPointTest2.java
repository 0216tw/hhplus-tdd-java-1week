package service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentPointTest2 {
    /* 추가적으로 동시성 테스트를 해보기 위한 소스코드입니다 */


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

