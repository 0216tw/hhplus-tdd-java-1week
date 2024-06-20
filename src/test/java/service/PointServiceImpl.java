package service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.util.Assert;

import java.util.List;

public class PointServiceImpl implements PointService {

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint charge(long id, long amount) {

        Assert.isTrue(amount > 0, "충전금액은 0원보다 커야합니다.");
        UserPoint userPoint = search(id);
        UserPoint newUserPoint = new UserPoint(id , userPoint.point() + amount , System.currentTimeMillis());
        userPointTable.insertOrUpdate(id , newUserPoint.point());
        pointHistoryTable.insert(id , newUserPoint.point() , TransactionType.CHARGE , System.currentTimeMillis());
        return newUserPoint;

    }

    @Override
    public UserPoint use(long id, long amount) {

        Assert.isTrue(amount > 0 , "사용금액은 0원보다 커야합니다.");

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
    public List<PointHistory> historySearch(long id) {

        UserPoint user = search(id);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(user.id());
        if(pointHistories.size() ==0) throw new RuntimeException("포인트 내역이 없습니다.");
        return pointHistories;
    }
}
