package se.ltu.kitting.test;

import static se.ltu.kitting.test.FirstDemoLayouts.*;

public class FirstDemo {

  public static void main(String[] args) throws Exception {
    Benchmark benchmark = Benchmark.builder()
      .config("firstDemo.xml")
      .test(demoLayout3(), "slowest, few pieces that fit exactly")
      .test(demoLayout2(), "fast")
      .test(demoLayout1(), "easy, many pieces but lots of space")
      .build();
    System.out.println("Running benchmark " + benchmark.name() + " ...");
    Benchmark.ResultStats stats = benchmark.run()
      .peek(r -> System.out.println(formatResult(r)))
      .peek(r -> Vis.draw(r.solution(), r.test().name()))
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
