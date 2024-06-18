package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PointService {
    public UserPoint charge(long id, long amount); //포인트를 충전한다.
    public UserPoint use(long id, long amount);     //포인트를 사용한다.
    public UserPoint selectById(long id);       //포인트를 조회한다.
    public List<PointHistory> findPointHistories(long id) ; //포인트 내역을 조회한다.
    public PointHistory insertPointHistory(long id , long amount , TransactionType type , long updateMillis); //포인트 충전 및 사용 이력을 적재한다.
}
