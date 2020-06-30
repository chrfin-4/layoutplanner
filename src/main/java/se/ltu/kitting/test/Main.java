package se.ltu.kitting.test;

import java.util.*;
import java.util.stream.Stream;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.SolverConfig;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;

import static se.ltu.kitting.test.LayoutExamples.*;
import static java.util.stream.Collectors.joining;

// TODO: The different scenarios should be defined separately somewhere.
public class Main {

  public static void main(String[] args) throws Exception {
    runAndVisualize(layout2());
    runBenchmark("solverConf.xml", layout1(), layout2(), layout3(), layout4(), layout5(), layout6(), layout7());
  }

  public static void runBenchmark(String xml, Layout ... layouts) {
    SolverConfig cfg = SolverConfig.createFromXmlResource("solverConf.xml");
    Stream.of(layouts)
      .map(l -> new Benchmark.Test(cfg, l))
      .map(Benchmark::runTest)
      .map(r -> String.format("time (ms): %5d, score: (%dhard/%dsoft)", r.time(), r.score().getHardScore(), r.score().getSoftScore()))
      .forEach(System.out::println);
  }

  public static void runAndVisualize(Layout layout) {
    runAndVisualize("solverConf.xml", layout);
  }

  public static void runAndVisualize(String xmlConfig, Layout unsolvedLayout) {
    SolverFactory<Layout> solverFactory = SolverFactory.createFromXmlResource(xmlConfig);
    Solver<Layout> solver = solverFactory.buildSolver();
    Layout solvedLayout = solver.solve(unsolvedLayout);
    System.out.println("After " + solver.getTimeMillisSpent()/1000 + " s:");
    System.out.println("Score: " + solvedLayout.getScore());
    System.out.println("Positions:");
    System.out.println(solvedLayout.getParts().stream().map(p -> p.toString() + ": " + String.valueOf(p.getPosition())).collect(joining(", ")));
    Vis.draw(solvedLayout);
  }

}
