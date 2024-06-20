package service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class PointHisotrySearchTest extends PointServiceBase {

     /*
        -기능 요구사항 :  포인트 내역을 조회한다.
        -실패 케이스 [1] : 조회된 사용자가 없을 경우 오류를 발생시킨다
        -실패 케이스 [2] : 음...이건 실패가 아닌데.. 포인트 내역이 없으면 내역이 없습니다를 반환한다. ??? 이건 실패일까 200일까 .. ?
     */

    @Test
    public void 포인트_내역_조회_성공() {

        //given
        UserPoint userPoint = new UserPoint(1L , 100 , System.currentTimeMillis());
        List<PointHistory> histories = new ArrayList<>();
        histories.add(new PointHistory(1L , 1L , 100 , TransactionType.CHARGE , System.currentTimeMillis()));
        histories.add(new PointHistory(2L , 1L , 200 , TransactionType.CHARGE , System.currentTimeMillis()));
        histories.add(new PointHistory(3L , 1L , 200 , TransactionType.USE , System.currentTimeMillis()));

        //when
        when(userPointTable.selectById(userPoint.id())).thenReturn(userPoint);
        when(pointHistoryTable.selectAllByUserId(userPoint.id())).thenReturn(histories);

        List<PointHistory> afterHistories = pointService.historySearch(userPoint.id());

        //then
        Assertions.assertEquals(histories.get(0).id() , afterHistories.stream().filter(item -> item.id() == 1L).findFirst().get().id());
        Assertions.assertEquals(histories.get(1).id() , afterHistories.stream().filter(item -> item.id() == 2L).findFirst().get().id());
        Assertions.assertEquals(histories.get(2).id() , afterHistories.stream().filter(item -> item.id() == 3L).findFirst().get().id());


    }

    @Test
    public void 조회된_사용자가_없을_경우() {

        //given
        long id = 987654321L;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            List<PointHistory> afterHistories = pointService.historySearch(id);
        });

        //then
        Assertions.assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());

    }
}
