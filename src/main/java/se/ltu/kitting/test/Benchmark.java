package se.ltu.kitting.test;

import java.util.LongSummaryStatistics;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToLongFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.Collector;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.SolverConfig;
import se.ltu.kitting.model.Layout;
import se.ltu.kitting.util.StreamUtil;

import ch.rfin.util.Pair;

import static java.util.stream.Collectors.toList;

/**
 * Tool for comparing different solver configs (mostly different algorithms
 * and their parameters) and different layouts/problems.
 * For the most part, using either a single solver with multiple layouts
 * or a single layout with multiple solvers makes the most sense.
 * The benchmarking tool simply uses a solver config to solve a problem,
 * thereby converting a {@link Test} into a {@link Result}.
 * So it is up to the user to input a meaningful combination of configs and
 * layouts. However, there are some convenience methods for generating
 * tests for common cases.
 * @author Christoffer Fink
 */
public class Benchmark {

  private final String name;
  private final List<Test> tests;

  private Benchmark(String name, List<Test> tests) {
    this.name = name;
    this.tests = Collections.unmodifiableList(tests);
  }

  public static Builder builder() {
    return new Builder();
  }

  public String name() {
    return name;
  }

  public List<Test> tests() {
    return tests;
  }

  /**
   * Builds a benchmark, which is a collection of Tests.
   * The builder can essentially be used in two different ways, either by
   * explicitly adding tests one at a time, or by supplying some number of
   * layouts and some number of solver configurations and letting the
   * builder generate Tests from the different combinations.
   *
   * The layout() and config() methods add another layout/config to be combined
   * into tests when the Benchmark is built.
   *
   * The test() method adds a new test using the given layout and/or config
   * (and whatever layout/config was most recently set with either of the
   * layout()/config() methods, depending which of the two parameters, if
   * any, is missing).
   *
   * In other words, test() immediately creates a Test instance;
   * layout()/config() accumulate layouts and configs that are later used to
   * create Test instances.
   *
   * If tests are added with test(), only those tests will be used (layouts
   * and configs will not be combined to create additional tests).
   *
   * Unless explicitly given a name, the Benchmark will try to derive a name
   * from the layout(s) or config(s) or fall back on a default value.
   */
  public static class Builder {
    public static final String defaultBenchmarkName = "unnamed benchmark";

    private String name;
    private String implicitName;
    private List<Test> tests = new ArrayList<>();
    private List<Pair<String,Layout>> layouts = new ArrayList<>();
    private List<Pair<String,SolverConfig>> configs = new ArrayList<>();

    public Benchmark build() {
      if (!tests.isEmpty()) {
        return buildTests();
      }
      if (layouts.isEmpty() && configs.isEmpty()) {
        throw new IllegalStateException("Illegal state");
      }
      List<Test> tests = new ArrayList<>();
      for (Pair<String,SolverConfig> cfg : configs) {
        for (Pair<String,Layout> layout : layouts) {
          String cfgName = cfg._1 != null ? cfg._1 : "unnamed config";
          String layoutName = layout._1 != null ? layout._1 : "unnamed layout";
          String testName = String.format("(%s; %s)", cfgName, layoutName);
          tests.add(new Test(testName, cfg._2, layout._2));
        }
      }
      /*
      List<Test> tests = configs.stream()
        .flatMap(c -> builder().config(c._2, c._1).testLayouts(layouts).tests.stream())
        .collect(toList());
        */
      // Cannot rely on any derived or implicit names in this case.
      String benchmarkName = name != null ? name : defaultBenchmarkName;
      return new Benchmark(benchmarkName, tests);
    }

    public Stream<Benchmark> buildMultipleByConfig() {
      if (layouts.isEmpty() && configs.isEmpty()) {
        throw new IllegalStateException("Illegal state");
      }
      return configs.stream()
        .map(c -> builder().name(name).config(c._2, c._1).testLayouts(layouts).build());
    }

    public Stream<Benchmark> buildMultipleByLayout() {
      if (layouts.isEmpty() && configs.isEmpty()) {
        throw new IllegalStateException("Illegal state");
      }
      return layouts.stream()
        .map(l -> builder().name(name).layout(l._2, l._1).testConfigs(configs).build());
    }

    private Benchmark buildTests() {
      // Use only the tests list, ignoring the lists of layouts and configs.
      return new Benchmark(getName(), new ArrayList<>(tests));
    }

    /** Test these (unnamed) layouts. */
    public Builder layouts(Layout ... layouts) {
      for (Layout l : layouts) {
        layout(l);
      }
      return this;
    }

    /** Test these (named) layouts. */
    @SafeVarargs
    public final Builder layouts(Pair<String,Layout> ... nameAndLayoutPairs) {
      for (var pair : nameAndLayoutPairs) {
        layout(pair);
      }
      return this;
    }

    /** Test this (unnamed) layout. */
    public Builder layout(Layout layout) {
      return layout(layout, null);
    }

    /** Test this (named) layout. */
    // TODO: null check
    public Builder layout(Pair<String,Layout> nameAndLayoutPair) {
      return layout(nameAndLayoutPair._2, nameAndLayoutPair._1);
    }

    /** Test this (named) layout. */
    public Builder layout(Layout layout, String layoutName) {
      this.layouts.add(Pair.of(layoutName, layout));
      return setNameImplicitly(layoutName);
    }

    /** Test these (unnamed) XML configs. */
    public Builder configs(String ... configs) {
      for (String xml : configs) {
        config(xml);
      }
      return this;
    }

    /** Test these (unnamed) solver configs. */
    public Builder configs(SolverConfig ... configs) {
      for (SolverConfig config : configs) {
        config(config);
      }
      return this;
    }

    /** Test these (named) solver configs. */
    @SafeVarargs
    public final Builder configs(Pair<String,SolverConfig> ... nameAndConfigPairs) {
      for (var pair : nameAndConfigPairs) {
        config(pair);
      }
      return this;
    }

    /** Test this (unnamed) solver config. */
    public Builder config(SolverConfig config) {
      return config(config, null);
    }

    /** Test this XML config, using the XML file name as the config name. */
    public Builder config(String xml) {
      return config(xml, xml);
    }

    /** Test this (named) XML config. */
    public Builder xmlConfig(Pair<String,String> nameAndXmlPair) {
      return config(nameAndXmlPair._2, nameAndXmlPair._1);
    }

    /** Test this (named) solver config. */
    public Builder config(Pair<String,SolverConfig> nameAndConfigPair) {
      return config(nameAndConfigPair._2, nameAndConfigPair._1);
    }

    /** Test this (explicitly named) XML config. */
    public Builder config(String xml, String name) {
      return config(SolverConfig.createFromXmlResource(xml), name);
    }

    /** Test this (named) solver config. */
    // TODO: null check
    public Builder config(SolverConfig config, String configName) {
      configs.add(Pair.of(configName, config));
      return setNameImplicitly(configName);
    }

    public Builder test(Layout ... layouts) {
      for (Layout layout : layouts) {
        test(layout);
      }
      return this;
    }

    public Builder testLayouts(Iterable<Pair<String,Layout>> nameLayoutPairs) {
      for (Pair<String,Layout> pair : nameLayoutPairs) {
        test(pair._2, pair._1 != null ? pair._1 : defaultTestName());
      }
      return this;
    }

    public Builder testConfigs(Iterable<Pair<String,SolverConfig>> nameConfigPairs) {
      for (Pair<String,SolverConfig> pair : nameConfigPairs) {
        test(pair._2, pair._1 != null ? pair._1 : defaultTestName());
      }
      return this;
    }

    /** Add a test using this layout and the current config. */
    public Builder test(Layout layout) {
      return test(layout, defaultTestName());
    }

    /** Add a (named) test using this layout and the current config. */
    public Builder test(Layout layout, String testName) {
      return test(config()._2, layout, testName);
    }

    /** Add a (named) test using this config and the current layout. */
    public Builder test(SolverConfig config, String testName) {
      return test(config, layout()._2, testName);
    }

    public Builder test(String xml) {
      return test(SolverConfig.createFromXmlResource(xml), layout()._2, xml);
    }

    /** Add a (named) test using this config and the current layout. */
    public Builder test(String xml, String testName) {
      return test(SolverConfig.createFromXmlResource(xml), layout()._2, testName);
    }

    public Builder test(SolverConfig config) {
      return test(config, layout()._2, defaultTestName());
    }

    public Builder test(String xml, Layout layout) {
      return test(SolverConfig.createFromXmlResource(xml), layout, defaultTestName());
    }

    public Builder test(SolverConfig config, Layout layout) {
      return test(config, layout, defaultTestName());
    }

    // TODO: assert that none of these parameters are null
    public Builder test(SolverConfig config, Layout layout, String testName) {
      return test(new Test(testName, config, layout));
    }

    public Builder test(Test test) {
      tests.add(test);
      return this;
    }

    private String defaultTestName() {
      return getName() + " [" + tests.size() + "]";
    }

    private Builder setNameImplicitly(String name) {
      if (implicitName == null) {
        implicitName = name;
      }
      return this;
    }

    private String getName() {
      if (name != null) {
        return name;
      }
      if (implicitName != null) {
        return implicitName;
      }
      return defaultBenchmarkName;
    }

    private Pair<String,Layout> layout() {
      if (layouts.isEmpty()) {
        throw new IllegalStateException("You forgot to specify a layout!");
      }
      return layouts.get(layouts.size()-1);
    }

    private Pair<String,SolverConfig> config() {
      if (configs.isEmpty()) {
        throw new IllegalStateException("You forgot to specify a solver config!");
      }
      return configs.get(configs.size()-1);
    }

    /** Set the name of this benchmark. */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

  }

  public Stream<Result> run() {
    return tests.stream().map(Benchmark::runTest);
  }

  public static Result runTest(Test test) {
    Solver<Layout> solver = SolverFactory.<Layout>create(test.config()).buildSolver();
    final long start = System.currentTimeMillis();
    Layout solved = solver.solve(test.problem());
    final long end = System.currentTimeMillis();
    final long time = end - start;
    return new Result(test, time, solved);
  }

  public static Stream<Result> runTests(SolverConfig config, Layout ... layouts) {
    return runTests(config, List.of(layouts));
  }

  public static Stream<Result> runTests(SolverConfig config, Iterable<Layout> layouts) {
    return getTests(config, layouts).map(Benchmark::runTest);
  }

  private static Stream<Test> getTests(SolverConfig config, Layout ... layouts) {
    return getTests(config, List.of(layouts));
  }

  private static Stream<Test> getTests(SolverConfig config, Iterable<Layout> layouts) {
    return null;
  }

  private static Stream<Test> getTests(Layout layout, String name, SolverConfig ... configs) {
    return getTests(layout, name, List.of(configs));
  }

  private static Stream<Test> getTests(Layout layout, String name, Iterable<SolverConfig> configs) {
    return StreamUtil.stream(configs).map(cfg -> new Test(name, cfg, layout));
  }

  public static class Test {

    public final String testName;
    public final SolverConfig config;
    public final Layout problem;

    public Test(SolverConfig config, Layout problem) {
      this("unnamed test", config, problem);
    }

    public Test(String name, SolverConfig config, Layout problem) {
      this.testName = name;
      this.config = config;
      this.problem = problem;
    }

    public Layout problem() {
      return problem;
    }

    public SolverConfig config() {
      return config;
    }

    public String name() {
      return testName;
    }

  }

  public static class Result {

    public final Test test;
    public final long time;
    public final Layout solution;
    public final HardSoftLongScore score;

    public Result(Test test, long time, Layout solution) {
      this.test = test;
      this.time = time;
      this.solution = solution;
      // XXX: Layout should be changed to return a HardSoftLongScore
      //this.score = solution.getScore();
      HardSoftScore intScore = solution.getScore();
      HardSoftLongScore longScore = HardSoftLongScore.of(intScore.getHardScore(), intScore.getSoftScore());
      this.score = longScore;
    }

    public Test test() {
      return test;
    }

    public long time() {
      return time;
    }

    public HardSoftLongScore score() {
      return score;
    }

    public Layout solution() {
      return solution;
    }

    public static Collector<Result,StatsAccumulator,ResultStats> collector() {
      return Collector.of(StatsAccumulator::new, StatsAccumulator::accept, StatsAccumulator::combine,
          StatsAccumulator::finish);
    }

  }

  public static class ResultStats {

    public final HardSoftLongScoreSummaryStatistics score;
    public final LongSummaryStatistics time;

    public ResultStats(LongSummaryStatistics time, HardSoftLongScoreSummaryStatistics score) {
      this.time = time;
      this.score = score;
    }

    // TODO: add?
    /*
    public LongSummaryStatistics hardStats();
    public LongSummaryStatistics softStats();
    */

    public HardSoftLongScore minScore() {
      return score.getMin();
    }

    public HardSoftLongScore maxScore() {
      return score.getMax();
    }

    public HardSoftLongScore avgScore() {
      return score.getAverage();
    }


    public static LongSummaryStatistics timeStats(Collection<Result> results) {
      return stats(results.stream(), Result::time);
    }

    public static LongSummaryStatistics hardScoreStats(Collection<Result> results) {
      return stats(results.stream().map(Result::score), HardSoftLongScore::getHardScore);
    }

    public static LongSummaryStatistics softScoreStats(Collection<Result> results) {
      return stats(results.stream().map(Result::score), HardSoftLongScore::getSoftScore);
    }

    private static <T> LongSummaryStatistics stats(Stream<T> results, ToLongFunction<T> accessor) {
      return results.mapToLong(accessor).summaryStatistics();
    }

  }

  public static class HardSoftLongScoreSummaryStatistics {

    public HardSoftLongScore sum = HardSoftLongScore.of(0,0);
    public HardSoftLongScore min = HardSoftLongScore.of(Long.MAX_VALUE,Long.MAX_VALUE);
    public HardSoftLongScore max = HardSoftLongScore.of(Long.MIN_VALUE,Long.MIN_VALUE);
    public long count = 0L;

    public void accept(HardSoftLongScore score) {
      count++;
      sum = sum.add(score);
      if (min.compareTo(score) < 0) {
        min = score;
      }
      if (max.compareTo(score) > 0) {
        max = score;
      }
    }

    public HardSoftLongScoreSummaryStatistics combine(HardSoftLongScoreSummaryStatistics stats) {
      count += stats.count;
      sum = sum.add(stats.sum);
      if (min.compareTo(stats.min) < 0) {
        min = stats.min;
      }
      if (max.compareTo(stats.max) > 0) {
        max = stats.max;
      }
      return this;
    }

    public long getCount() {
      return count;
    }

    public HardSoftLongScore getSum() {
      return sum;
    }

    public HardSoftLongScore getAverage() {
      return sum.divide(count);
    }

    public HardSoftLongScore getMin() {
      return min;
    }

    public HardSoftLongScore getMax() {
      return max;
    }

  }

  public static class StatsAccumulator {
    public LongSummaryStatistics time = new LongSummaryStatistics();
    public HardSoftLongScoreSummaryStatistics score = new HardSoftLongScoreSummaryStatistics();

    public void accept(Result result) {
      time.accept(result.time());
      score.accept(result.score());
    }

    public StatsAccumulator combine(StatsAccumulator stats) {
      time.combine(stats.time);
      score.combine(stats.score);
      return this;
    }

    public ResultStats finish() {
      return new ResultStats(time, score);
    }

  }

}
