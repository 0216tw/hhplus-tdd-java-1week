package service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class PointChargeTest extends PointServiceBase {

    /*
        -기능 요구사항 : 포인트를 충전한다.
        -기능 요구사항 : 포인트 충전 성공시 이력을 적재한다.

        -실패 케이스 [1] : 포인트를 0원 이하로 충전하려고 하는 경우 오류를 발생시킨다
        -실패 케이스 [2] : 조회된 사용자가 없을 경우 오류를 발생시킨다
        -실패 케이스 [3] : 이력 적재가 실패하면 오류를 발생시킨다.
     */


    @Test
    public void 포인트_충전_성공케이스() {

        //given
        long id = 1L;
        long amount = 200L ;
        UserPoint userPoint = new UserPoint(1L , 100L , System.currentTimeMillis());
        UserPoint chargeUserPoint = new UserPoint(1L , 300L , System.currentTimeMillis());

        PointHistory pointHistory = new PointHistory(1L
                , chargeUserPoint.id()
                , chargeUserPoint.point()
                , TransactionType.CHARGE
                , System.currentTimeMillis()
        );

        //when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        when(pointHistoryTable.insert(anyLong() , anyLong() , any() , anyLong())).thenReturn(pointHistory);

        UserPoint afterUserPoint = pointService.charge(id , amount);
        PointHistory getPointHistory = pointHistoryTable.insert(chargeUserPoint.id()
                , chargeUserPoint.point()
                ,TransactionType.CHARGE
                ,System.currentTimeMillis());

        //then
        Assertions.assertEquals(chargeUserPoint.point() , afterUserPoint.point());
        Assertions.assertEquals(getPointHistory.userId() , afterUserPoint.id());
        Assertions.assertEquals(getPointHistory.amount() , afterUserPoint.point());
    }


    @Test
    public void 충전할_포인트가_0원_이하일경우() {

        //given
        UserPoint userPoint = new UserPoint(1L , 100 , System.currentTimeMillis());
        long id = 1L;
        long amount = 0L ;

        //when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(id , amount);
        });

        Assertions.assertEquals("충전금액은 0원보다 커야합니다." , exception.getMessage());
    }


    @Test
    public void 조회된_사용자가_없을_경우() {
        //given
        long id = 987654321L;
        long amount = 123L ;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(id , amount);
        });

        //then
        Assertions.assertEquals("존재하지 않는 사용자입니다." , exception.getMessage());
    }

    @Test
    public void 이력_적재에_실패한_경우() {
        //TODO
    }



}
