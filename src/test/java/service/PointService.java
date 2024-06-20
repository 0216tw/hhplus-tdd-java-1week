package service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {
    public UserPoint charge(long id, long amount);

    public UserPoint use(long id, long amount);

    public UserPoint search(long id);

    public List<PointHistory> historySearch(long id);
}
