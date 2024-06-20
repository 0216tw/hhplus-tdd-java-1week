package service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PointServiceBase {

    /*
    * 1차 과제 테스트 및 구현해야 할 대상
      -- 포인트 충전하기    -> service.PointChargeTest
      -- 포인트 사용하기    -> service.PointUseTest
      -- 포인트 조회하기    -> service.PointSearchTest
      -- 포인트 내역 조회하기 -> PointHistorySearchTest
      -- 추가로 고려할 사항!! 포인트를 충전하거나 사용할 때마다 이력이 적재되어야 한다.
         (이 경우에는 기존 TC를 변경해야하나? 아니면 새로 TC를 만드는 걸까 ?)
          기존 TC를 변경하자니 그건 좀 아닌거 같고.. 새로운 TC를 만들자니 이전 TC도 다시 검증할것 같은데 .. 후자로 가자!

    * 2차 과제 테스트 및 구현해야 할 대상
      --동시성 이슈 제어하기
    */

    @Mock
    UserPointTable userPointTable;
    @Mock
    PointHistoryTable pointHistoryTable;
    @InjectMocks
    PointServiceImpl pointService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void close() {
    }
}
