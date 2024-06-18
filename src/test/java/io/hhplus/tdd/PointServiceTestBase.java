package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class PointServiceTestBase {


    PointService pointService;

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;

    @BeforeEach
    public void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();

        userPointTable.insertOrUpdate(1L ,100);
        pointHistoryTable.insert(1L, 100 , TransactionType.CHARGE , System.currentTimeMillis());
        userPointTable.insertOrUpdate(2L ,200);
        pointHistoryTable.insert(2L, 200 , TransactionType.CHARGE , System.currentTimeMillis());
        userPointTable.insertOrUpdate(3L ,300);
        pointHistoryTable.insert(3L, 300 , TransactionType.CHARGE , System.currentTimeMillis());


        pointService = new PointServiceImpl(userPointTable , pointHistoryTable);



    }

    @AfterEach
    public void close() {
    }

}
