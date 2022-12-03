package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    public static final String SOURCE_TARGET_NOT_SAME_EXCEPTION_MESSAGE = "출발역과 도착역이 같을 수 없습니다.";

    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }

    public Path findPath(Station source, Station target) {
        validSearchPath(source, target);
        return new Path(createSections(findPath(source, target, registerStationInfo())));
    }

    private void validSearchPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(SOURCE_TARGET_NOT_SAME_EXCEPTION_MESSAGE);
        }
    }

    private Sections createSections(GraphPath<Station, SectionEdge> result) {
        Sections sections = new Sections();
        result.getEdgeList().stream().map(SectionEdge::getSection).forEach(sections::add);
        return sections;
    }

    private GraphPath<Station, SectionEdge> findPath(Station source, Station target, SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {

        GraphPath<Station, SectionEdge> result = new DijkstraShortestPath<>(graph).getPath(source, target);

        if (result == null) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    private SimpleDirectedWeightedGraph<Station, SectionEdge> registerStationInfo() {
        SimpleDirectedWeightedGraph<Station, SectionEdge> graph = new SimpleDirectedWeightedGraph<>(SectionEdge.class);
        addStations(graph);
        registerStations(graph);
        registerSections(graph);
        return graph;
    }

    private void addStations(SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(it -> {
                    return it.getStations().stream();
                })
                .distinct()
                .collect(Collectors.toList()).forEach(graph::addVertex);
    }

    private void registerStations(SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(it -> it.getSections().stream())
                .forEach(it -> {
                    SectionEdge sectionEdge = SectionEdge.of(it);
                    graph.addEdge(it.getUpStation(), it.getDownStation(), sectionEdge);
                    graph.setEdgeWeight(sectionEdge, it.getDistance().getDistance());
                });
    }

    private void registerSections(SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(it -> it.getSections().stream())
                .map(it -> new Section(it.getLine(), it.getDownStation(), it.getUpStation(), it.getDistance()))
                .forEach(it -> {
                    SectionEdge sectionEdge = SectionEdge.of(it);
                    graph.addEdge(it.getUpStation(), it.getDownStation(), sectionEdge);
                    graph.setEdgeWeight(sectionEdge, it.getDistance().getDistance());
                });
    }
}