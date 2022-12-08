package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.Fare;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class PathService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {

        List<Line> lines = lineRepository.findAll();

        Station source = stationRepository.findById(sourceId).orElseThrow(EntityNotFoundException::new);
        Station target = stationRepository.findById(targetId).orElseThrow(EntityNotFoundException::new);

        PathFinder pathFinder = new PathFinder(lines);
        int distance = pathFinder.findDistance(source, target);
        return new PathResponse(pathFinder.findStations(source, target), distance, new Fare(distance).calculate());
    }
}
