package se.ltu.kitting.test;

import java.util.*;
import java.util.stream.Stream;
import java.util.function.UnaryOperator;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.SolverConfig;
import se.ltu.kitting.model.Part;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.model.Surface;
import ch.rfin.util.Pair;

import se.ltu.kitting.ConstructionHeuristics;

import static se.ltu.kitting.ConstructionHeuristics.*;
import static se.ltu.kitting.test.LayoutExamples.*;
import static se.ltu.kitting.test.FirstDemoLayouts.*;
import static java.util.stream.Collectors.joining;
import static se.ltu.kitting.util.StreamUtil.stream;

public class Main {

  public static void main(String[] args) throws Exception {
    //runAndVisualize(layout1());
    runBenchmark("solverConf.xml", layout0(), layout1(), layout2(), layout3(), layout4(), layout5(), layout6(), layout7(), layout8(), layout9(), layout10(), layout11());
    //runExampleBenchmarkMulti1();
	List<Pair<String,Layout>> layouts = getAll();
	//runCustomHeuristic("noConstructionHeuristic.xml", ConstructionHeuristics::FFDH, layouts);
	//runCustomHeuristic("noConstructionHeuristic.xml", ConstructionHeuristics::zero, layouts);
	runCustomHeuristic("noConstructionHeuristic.xml", ConstructionHeuristics::areaAndHeight, layouts);
	//runCustomHeuristic("noConstructionHeuristic.xml", ConstructionHeuristics::minArea, layouts);
  }

  @Deprecated
  public static void runBenchmark(String xml, Layout ... layouts) {
    SolverConfig cfg = SolverConfig.createFromXmlResource("solverConf.xml");
    Benchmark.ResultStats stats = Stream.of(layouts)
      .map(l -> new Benchmark.Test(cfg, l))
      .map(Benchmark::runTest)
      .peek(r -> System.out.println(String.format("time (ms): %5d, score: (%dhard/%dsoft)", r.time(), r.score().getHardScore(), r.score().getSoftScore())))
      .peek(r -> Vis.draw(r.solution()))
      .collect(Benchmark.Result.collector());
    String s = String.format("avg time (ms): %.2f", stats.time.getAverage());
    System.out.println(s);
      //.map(r -> String.format("time (ms): %5d, score: (%dhard/%dsoft)", r.time(), r.score().getHardScore(), r.score().getSoftScore()))
      //.peek(System.out::println)
  }

  public static void runAndVisualize(Layout layout) {
    runAndVisualize("noConstructionHeuristic.xml", layout);
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
  
  // Benchmark for the first demonstration, shows that layouts that need specific order and rotation can be solved.
  public static void runFirstDemoBenchmark() {
    Benchmark benchmark = Benchmark.builder()
    .name("First Demo Benchmark")
    .config("firstDemo.xml")
    .test(demoLayout1(), "3 parts, specific order")
    .test(demoLayout2(), "4 parts, rotation, full packed")
    .test(demoLayout3(), "15 parts")
    .build();
    runExampleBenchmark(benchmark);
  }

  // Some examples of how to create and run benchmarks.

  /* Example output:
   * Running benchmark Example benchmark ...
   * time (ms):    36, score: (0hard/0soft) [easy]
   * time (ms):   706, score: (0hard/0soft) [easy, requires rotation]
   * time (ms):     3, score: (0hard/0soft) [difficult, tightly packed]
   * avg time (ms): 248.33
   */
  public static void runExampleBenchmark1() {
    // Let's see how this solver config handles this set of layouts.
    Benchmark benchmark = Benchmark.builder()
      .name("Example benchmark")
      .config("solverConf.xml")
      .test(layout1(), "easy")
      .test(layout2(), "easy, requires rotation")
      .test(layout9(), "difficult, tightly packed")
      .build();
    runExampleBenchmark(benchmark);
  }
  
  public static void runExampleBenchmarkEx() {
    // Let's see how this solver config handles this set of layouts.
    Benchmark benchmark = Benchmark.builder()
      .name("Example benchmark")
      .config("solverConf.xml")
      .test(layout1(), "Layout1")
      .test(layout2(), "Layout2")
      .test(layout3(), "Layout3")
	  .test(layout4(), "Layout4")
	  .test(layout5(), "Layout5")
	  .test(layout6(), "Layout6")
	  .test(layout7(), "Layout7")
	  .test(layout8(), "Layout8")
	  .test(layout9(), "Layout9")
	  .test(layout10(), "Layout10")
	  .test(layout11(), "Layout11")
      .build();
    runExampleBenchmark(benchmark);
  }

  /* Example output:
   * Running benchmark difficult, tightly packed ...
   * time (ms):    37, score: (0hard/0soft) [firstFit.xml]
   * time (ms):     9, score: (0hard/0soft) [firstFitDecreasing.xml]
   * avg time (ms): 23.00
   */
  public static void runExampleBenchmark2_1() {
    // Let's solve a difficult problem using different configs and
    // see which does best.
    Benchmark benchmark = Benchmark.builder()
      .layout(layout9(), "difficult, tightly packed")
      .test("firstFit.xml")
      .test("firstFitDecreasing.xml")
      .build();
    runExampleBenchmark(benchmark);
  }

  /* Example output:
   * Running benchmark FIRST_FIT vs FIRST_FIT_DECREASING ...
   * time (ms):    34, score: (0hard/0soft) [(firstFit.xml; difficult, tightly packed)]
   * time (ms):     8, score: (0hard/0soft) [(firstFitDecreasing.xml; difficult, tightly packed)]
   * avg time (ms): 21.00
   */
  public static void runExampleBenchmark2_2() {
    // Let's solve a difficult problem using different configs and
    // see which does best.
    Benchmark benchmark = Benchmark.builder()
      .name("FIRST_FIT vs FIRST_FIT_DECREASING")
      .layout(layout9(), "difficult, tightly packed")
      .configs("firstFit.xml", "firstFitDecreasing.xml")
      .build();
    runExampleBenchmark(benchmark);
  }

  /* Example output:
   * Running benchmark difficult, tightly packed ...
   * time (ms):    35, score: (0hard/0soft) [firstFit.xml]
   * time (ms):    10, score: (0hard/0soft) [firstFitDecreasing.xml]
   * avg time (ms): 22.50
   */
  public static void runExampleBenchmark2_3() {
    // Let's solve a difficult problem using different configs and
    // see which does best.
    //Benchmark benchmark = Benchmark.builder()
    Benchmark.builder()
      .layout(layout9(), "difficult, tightly packed")
      .configs("firstFit.xml", "firstFitDecreasing.xml")
      //.buildMultipleByLayout().findFirst().get();
      .buildMultipleByLayout().forEach(Main::runExampleBenchmark);
    //runExampleBenchmark(benchmark);
  }

  /* Example output:
   * Running benchmark firstFit.xml ...
   * time (ms):    34, score: (0hard/0soft) [firstFit.xml [0]]
   * time (ms):    44, score: (-1hard/0soft) [firstFit.xml [1]]
   * time (ms):    25, score: (0hard/0soft) [firstFit.xml [2]]
   * avg time (ms): 34.33
   * Running benchmark firstFitDecreasing.xml ...
   * time (ms):     7, score: (0hard/0soft) [firstFitDecreasing.xml [0]]
   * time (ms):    23, score: (-1hard/0soft) [firstFitDecreasing.xml [1]]
   * time (ms):    17, score: (0hard/0soft) [firstFitDecreasing.xml [2]]
   * avg time (ms): 15.67
   */
  public static void runExampleBenchmarkMulti1() {
    // Let's solve a set of layouts using only a construction heuristic
    // and see the difference between first fit and first fit decreasing.
    // The build() method would return
    Benchmark.builder()
      .configs("firstFit.xml", "firstFitDecreasing.xml")
      .layouts(layout1(), layout2(), layout3())
      .buildMultipleByConfig().forEach(Main::runExampleBenchmark);
  }

  // Feeds layouts through a custom construction heuristic before passing them to the benchmark.
  public static void runCustomHeuristic(String xml, UnaryOperator<Layout> heuristic, Iterable<Pair<String,Layout>> layouts) {
    Benchmark benchmark = Benchmark.builder()
      .name("Benchmark with custom heuristic")
      .config(xml)
      .testLayouts(() -> stream(layouts).map(p -> p.map_2(heuristic)).iterator())
      .build();
    runExampleBenchmark(benchmark);
  }

  // XXX: A lot of this complexity will be hidden in the future.
  public static void runExampleBenchmark(Benchmark benchmark) {
    System.out.println("Running benchmark " + benchmark.name() + " ...");
    Benchmark.ResultStats stats = benchmark.run()
      .peek(r -> System.out.println(formatResult(r)))
      .peek(r -> Vis.draw(r.solution()))
      .collect(Benchmark.Result.collector());
    String s = String.format("avg time (ms): %.2f", stats.time.getAverage());
    System.out.println(s);
  }

  private static String formatResult(Benchmark.Result result) {
    long time = result.time();
    String score = result.score().toString();
    String name = result.test().name();
    return String.format("time (ms): %5d, score: (%s) [%s]", time, score, name);
  }

}
