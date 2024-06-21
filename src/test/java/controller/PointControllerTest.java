package controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.hhplus.tdd.TddApplication;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

@WebMvcTest(PointController.class)
@ContextConfiguration(classes = {TddApplication.class})
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    /*
    * 조회 컨트롤러 테스트 성공 케이스
    *
    * */
    @Test
    public void 포인트_조회_컨트롤러_성공_테스트() throws Exception {
        UserPoint userPoint = new UserPoint(1L , 500 , System.currentTimeMillis());
        given(pointService.search(1L)).willReturn(userPoint);

        mockMvc.perform(get("/point/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(500));
    }


}
