package io.hhplus.tdd;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class PointUseTest extends PointServiceTestBase {
    @Test
    @DisplayName("포인트 사용 성공 테스트 케이스")
    public void 포인트_사용_성공() {

        //given
        long id = 1L;
        long amount = 123L;
        UserPoint userPoint = pointService.selectById(id);

        //when
        UserPoint response = pointService.charge(id , amount);
        long chargeResult = userPoint.point() + amount;

        //then
        Assertions.assertEquals(response.id() , id);
        Assertions.assertEquals(response.point() , chargeResult);
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
    //이런 경우에는 해당 테스트는 무의미한 것인지 , 아니면 mock을 써서라도 테스트를 해야하는 것인지
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

}
