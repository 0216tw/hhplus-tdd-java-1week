package service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class PointUseTest extends PointServiceBase {
    /*
        -기능 요구사항 :  포인트를 사용한다.
        -기능 요구사항 : 포인트 사용 성공시 이력을 적재한다.

        -실패 케이스 [1] : 잔여 포인트가 0원 미만이 되면 실패한다.
        -실패 케이스 [2] : 조회된 사용자가 없을 경우 실패한다.
        -실패 케이스 [3] : 사용하려는 포인트는 0원보다 커야 한다.
        -실패 케이스 [4] : 이력 적재가 실패하면 오류를 발생시킨다.
     */


    @Test
    public void 포인트_사용_성공케이스() {

        //given
        UserPoint userPoint = new UserPoint(1L , 100 , System.currentTimeMillis());
        long id = 1L;
        long amount = 100L ;
        UserPoint afterUserPoint = new UserPoint(1L , 0L , System.currentTimeMillis());
        PointHistory pointHistory = new PointHistory(1L
                , afterUserPoint.id()
                , afterUserPoint.point()
                , TransactionType.USE
                , System.currentTimeMillis()
        );

        //when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        when(pointHistoryTable.insert(anyLong() , anyLong() , any() , anyLong())).thenReturn(pointHistory);

        UserPoint usedUserPoint = pointService.use(id , amount);
        PointHistory getPointHistory = pointHistoryTable.insert(afterUserPoint.id()
                , afterUserPoint.point()
                ,TransactionType.USE
                ,System.currentTimeMillis());

        //then
        Assertions.assertEquals(usedUserPoint.point() , afterUserPoint.point());
        Assertions.assertEquals(getPointHistory.userId() , afterUserPoint.id());
        Assertions.assertEquals(getPointHistory.amount() , afterUserPoint.point());
    }

    @Test
    public void 잔여_포인트가_0원_미만일경우() {

        //given
        UserPoint userPoint = new UserPoint(1L , 100 , System.currentTimeMillis());
        long id = 1L;
        long amount = 200L ;

        //when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.use(id , amount);
        });

        Assertions.assertEquals("잔액이 부족합니다." , exception.getMessage());
    }


    @Test
    public void 사용금액이_0원이하_일경우() {

        //given
        UserPoint userPoint = new UserPoint(1L , 100 , System.currentTimeMillis());
        long id = 1L;
        long amount = 0L ;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.use(id , amount);
        });

        //then
        Assertions.assertEquals("사용금액은 0원보다 커야합니다." , exception.getMessage());


    }

    @Test
    public void 조회된_사용자가_없을_경우() {
        //given
        long id = 987654321L;
        long amount = 123L ;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.use(id , amount);
        });

        //then
        Assertions.assertEquals("존재하지 않는 사용자입니다." , exception.getMessage());
    }

    @Test
    public void 이력_적재에_실패한_경우() {
        //TODO
    }

}
