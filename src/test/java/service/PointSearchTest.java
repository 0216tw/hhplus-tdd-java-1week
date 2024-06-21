package service;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class PointSearchTest extends PointServiceBase {

     /*
        -기능 요구사항 :  포인트를 조회한다.
        -실패 케이스 [1] : 조회된 사용자가 없을 경우 오류를 발생시킨다
     */

    @Test
    public void 포인트_조회_성공() {

        //given
        long id = 1L;
        UserPoint userPoint = new UserPoint(1L , 100 , System.currentTimeMillis());

        //when
        when(userPointTable.selectById(id)).thenReturn(userPoint);  //이 경우 mock한 userPointTable를 처리한 것이니 단위테스트에 부합한지 확인받고 싶습니다.
        UserPoint foundUser = pointService.search(id);

        //then
        Assertions.assertEquals(foundUser.point() , userPoint.point());
    }

    @Test
    public void 조회된_사용자가_없을_경우() {

        //given
        long id = 987654321L;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserPoint userPoint = pointService.search(id);
        });

        //then
        Assertions.assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());

    }
}
