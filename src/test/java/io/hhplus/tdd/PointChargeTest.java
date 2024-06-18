package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/*
* 포인트 충전에 대한 테스트 케이스입니다.
*/
public class PointChargeTest extends PointServiceTestBase {


    @Mock
    public PointService pointServiceMock;

    @BeforeEach
    public void setMock() {
        MockitoAnnotations.openMocks(this);
    }
    /*
    *  충전을 성공하는 테스트 케이스
    */
    @Test
    @DisplayName("충전 성공 테스트 케이스")
    public void 충전_성공() {

        //given
        long id = 1L;
        long amount = 123L;
        UserPoint userPoint = pointService.selectById(id);
        long chargeResult = userPoint.point() + amount;

        //when
        UserPoint response = pointService.charge(id , amount);
        PointHistory pointHistory = pointService.insertPointHistory(response.id() , response.point() , TransactionType.CHARGE , response.updateMillis());

        //then
        Assertions.assertEquals(response.id() , id);
        Assertions.assertEquals(response.point() , chargeResult);

        Assertions.assertEquals(pointHistory.amount() , response.point());
        Assertions.assertEquals(pointHistory.userId() , response.id());
        Assertions.assertEquals(pointHistory.updateMillis() , response.updateMillis());

    }

    @Test
    @DisplayName("충전 실패 - 충전할 금액이 1원 미만인 경우")
    public void 충전금액이_1원미만인_경우() {

        //given
        UserPoint userPoint = UserPoint.empty(1L); //0원

        //when
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(userPoint.id(), userPoint.point());
        });

        //then
        Assertions.assertEquals(thrown.getMessage() , "충전금액은 1원 이상입니다.");
    }

    //이 경우 UserTable 에서는 값이 없으면 값을 할당해 리턴하므로 null일 수 없다.
    //이런 경우에는 해당 테스트는 무의미한 것인지 , 아니면 mock을 써서라도 테스트를 해야하는 것인가?
    @Test
    @DisplayName("충전 실패 - 충전할 대상이 없는 경우")
    public void 충전할_대상이_없을경우() {

        //given
        long id = 91263525L;

        //when
        //IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
        //    pointService.selectById(id);
        //});
        //then
        //Assertions.assertEquals(thrown.getMessage() , "사용자가 존재하지 않습니다.");
    }


    @Test
    @DisplayName("충전 실패 - 충전은 성공했지만 이력 적재에 실패한 경우")
    public void 충전은_성공했지만_이력_적재에_실패한_경우() {

        //given
        long id = 1L;
        long amount = 123L;
        UserPoint userPoint = pointService.selectById(id);
        long chargeResult = userPoint.point() + amount;

        //when
        UserPoint response = pointService.charge(id , amount);


        when(pointServiceMock.insertPointHistory(response.id() , response.point() , TransactionType.CHARGE , response.updateMillis()))
                .thenThrow(new IllegalArgumentException("이력 등록 실패"));


        //when
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointServiceMock.insertPointHistory(response.id() , response.point() , TransactionType.CHARGE , response.updateMillis());
        }); 

        //then
        Assertions.assertEquals(thrown.getMessage() , "이력 등록 실패");
    }

}
