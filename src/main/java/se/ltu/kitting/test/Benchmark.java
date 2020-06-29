package se.ltu.kitting.test;

import java.util.LongSummaryStatistics;
import java.util.Collection;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.SolverConfig;
import se.ltu.kitting.model.Layout;

/**
 * Tool for comparing different solver configs (mostly different algorithms
 * and their parameters).
 * Takes a config and a bunch of layouts and returns a {@link Result}
 * that is the result of solving all the layouts.
 */
public class Benchmark {

  public Result runTest(Test test) {
    Solver<Layout> solver = SolverFactory.<Layout>create(test.config()).buildSolver();
    final long start = System.currentTimeMillis();
    Layout solved = solver.solve(test.problem());
    final long end = System.currentTimeMillis();
    final long time = end - start;
    return new Result(test, time, solved);
  }

  public Stream<Result> runAllTests(Stream<Test> tests) {
    return tests.map(this::runTest);
  }

  public static class Test {

    public final Layout problem;
    public final SolverConfig config;

    public Test(Layout problem, SolverConfig config) {
      this.problem = problem;
      this.config = config;
    }

    public Layout problem() {
      return problem;
    }

    public SolverConfig config() {
      return config;
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

  }

  interface ResultStats {

    public LongSummaryStatistics timeStats();
    public LongSummaryStatistics hardStats();
    public LongSummaryStatistics softStats();

    public HardSoftLongScore minScore();
    public HardSoftLongScore maxScore();
    public HardSoftLongScore avgScore();

    public static LongSummaryStatistics timeStats(Collection<Result> results) {
      return stats(results.stream(), Result::time);
    }

    public static LongSummaryStatistics hardScoreStats(Collection<Result> results) {
      return stats(results.stream().map(Result::score), HardSoftLongScore::getHardScore);
    }

    public static LongSummaryStatistics softScoreStats(Collection<Result> results) {
      return stats(results.stream().map(Result::score), HardSoftLongScore::getSoftScore);
    }

    public static <T> LongSummaryStatistics stats(Stream<T> results, ToLongFunction<T> accessor) {
      return results.mapToLong(accessor).summaryStatistics();
    }

  }

}
