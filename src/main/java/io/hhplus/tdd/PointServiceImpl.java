package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

public class PointServiceImpl implements PointService {

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable , PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint selectById(long id) {
        UserPoint userPoint = userPointTable.selectById(id);

        if(userPoint.updateMillis() == 0L) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        return userPoint;
    }

    public UserPoint charge(long id, long amount) {

        Assert.isTrue(amount > 0, "충전금액은 1원 이상입니다.");

        //대상조회
        UserPoint foundUser = selectById(id);

        long chargedAmount = amount + foundUser.point();
        UserPoint newUserPoint = new UserPoint(id, chargedAmount, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(newUserPoint.id() , chargedAmount);
    }

    public UserPoint use(long id, long amount) {

        Assert.isTrue(amount > 0, "사용금액은 0원 이상입니다.");

        //대상조회
        UserPoint foundUser = selectById(id);
        long afterUseAmount = foundUser.point() - amount;
        Assert.isTrue(afterUseAmount < 0 , "잔고가 부족합니다.");

        return userPointTable.insertOrUpdate(id , afterUseAmount);
    }

    public List<PointHistory> findPointHistories(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }


    public PointHistory insertPointHistory(long id , long amount , TransactionType type , long updateMillis) {
        return pointHistoryTable.insert(id , amount , type , updateMillis);
    }

}
