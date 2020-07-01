package se.ltu.kitting.test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.Stream;
import org.optaplanner.core.config.solver.SolverConfig;
import static se.ltu.kitting.test.LayoutExamples.*;
import static java.util.stream.Collectors.toList;

public class BenchmarkTest {

  private static final String cfgName = "First Fit Decreasing";
  private static final String xml = "solverConf.xml";
  private static final SolverConfig cfg = SolverConfig.createFromXmlResource(xml);
  private static final String layout1Name = "very tricky layout";
  private static final String benchmarkName = "cool benchmark";
  private static final String defaultBenchmarkName = Benchmark.Builder.defaultBenchmarkName;

  @Test
  public void benchmarkShouldHaveExplicitlySetName() {
    Benchmark b = Benchmark.builder()
      .name(benchmarkName)
      .test(xml, layout1())
      .build();
    assertEquals(benchmarkName, b.name());
  }

  @Test
  public void benchmarkNameShouldBeFirstConfigNameIfSetFirst() {
    Benchmark b = Benchmark.builder()
      .config(cfg, cfgName)
      .config(cfg, "should be ignored")
      .test(layout1(), layout1Name)
      .build();
    assertEquals(cfgName, b.name());
  }

  @Test
  public void benchmarkNameShouldBeDerivedFromXmlFileIfSetFirst() {
    Benchmark b = Benchmark.builder()
      .config(xml)
      .config(cfg, "should be ignored")
      .test(layout1(), layout1Name)
      .build();
    assertEquals(xml, b.name());
  }

  @Test
  public void benchmarkShouldUseDefaultNameIfNothingSet_using_test() {
    Benchmark b = Benchmark.builder()
      .test(cfg, layout1(), "test name should't matter")
      .build();
    assertEquals(defaultBenchmarkName, b.name());
  }

  @Test
  public void benchmarkShouldUseDefaultNameIfNothingSet_using_test_config() {
    Benchmark b = Benchmark.builder()
      .layout(layout1())
      .test(cfg, cfgName)
      .build();
    assertEquals(defaultBenchmarkName, b.name());
  }

  @Test
  public void benchmarkShouldUseDefaultNameIfNothingSet_using_test_layout() {
    Benchmark b = Benchmark.builder()
      .config(cfg)
      .test(layout1(), layout1Name)
      .build();
    assertEquals(defaultBenchmarkName, b.name());
  }

  @Test
  public void benchmarkShouldUseDefaultNameIfNothingSet_using_layout_and_config() {
    Benchmark b = Benchmark.builder()
      .config(cfg)
      .layout(layout1())
      .build();
    assertEquals(defaultBenchmarkName, b.name());
  }

  @Test
  public void testNamesShouldMatchThoseExplicitlyGiven() {
    Benchmark b = Benchmark.builder()
      .test(cfg, layout1(), "test1")
      .test(cfg, layout1(), "test2")
      .build();
    List<String> expected = List.of("test1", "test2");
    assertEquals(expected, getTestNames(b));
  }

  @Test
  public void missingTestNamesShouldBeGenerated() {
    Benchmark b = Benchmark.builder()
      .test(cfg, layout1())
      .test(cfg, layout1())
      .config(xml)
      .test(layout1())
      .test(layout1(), "test4")
      .build();
    String expectedName1 = defaultBenchmarkName + " [0]";
    String expectedName2 = defaultBenchmarkName + " [1]";
    String expectedName3 = xml + " [2]";
    String expectedName4 = "test4";
    List<String> expected = List.of(expectedName1, expectedName2, expectedName3, expectedName4);
    assertEquals(expected, getTestNames(b));
  }

  @Test
  public void benchmarksGeneratedByConfigShouldUseConfigNameWhenAvailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "cfg1")
      .config(cfg, "cfg2")
      .layout(layout1(), "layout1")
      .layout(layout2(), "layout2")
      .buildMultipleByConfig();
    List<String> expected = List.of("cfg1", "cfg2");
    assertEquals(expected, getBenchmarkNames(bs));
  }

  @Test
  public void benchmarksGeneratedByConfigShouldUseDefaultWhenNoConfigNamesAvailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .configs(cfg, cfg)
      .layout(layout1(), "layout1")
      .layout(layout2(), "layout2")
      .buildMultipleByConfig();
    List<String> expected = List.of(defaultBenchmarkName, defaultBenchmarkName);
    assertEquals(expected, getBenchmarkNames(bs));
  }

  @Test
  public void benchmarksGeneratedByLayoutShouldUseLayoutNameWhenAvailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "doesn't matter")
      .config(cfg, "also doesn't matter")
      .layout(layout1(), "layout1")
      .layout(layout2(), "layout2")
      .buildMultipleByLayout();
    List<String> expected = List.of("layout1", "layout2");
    assertEquals(expected, getBenchmarkNames(bs));
  }

  @Test
  public void benchmarksGeneratedByLayoutShouldUseDefaultNameWhenNoLayoutNamesAvailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "doesn't matter")
      .config(cfg, "also doesn't matter")
      .layouts(layout1(), layout2())
      .buildMultipleByLayout();
    List<String> expected = List.of(defaultBenchmarkName, defaultBenchmarkName);
    assertEquals(expected, getBenchmarkNames(bs));
  }

  @Test
  public void benchmarksBuiltByConfigShouldDeriveTestNamesFromLayouts() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "cfg1")
      .config(cfg, "cfg2")
      .layout(layout1(), "layout1")
      .layout(layout2(), "layout2")
      .buildMultipleByConfig();
    List<String> testNames = List.of("layout1", "layout2");
    List<List<String>> expected = List.of(testNames, testNames);
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksBuiltByConfigShouldDeriveTestNamesFromBenchmarkNameIfLayoutNamesUnavailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "cfg1")
      .config(cfg, "cfg2")
      .layouts(layout1(), layout2())
      .buildMultipleByConfig();
    List<List<String>> expected = List.of(List.of("cfg1 [0]", "cfg1 [1]"), List.of("cfg2 [0]", "cfg2 [1]"));
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksBuiltByConfigShouldDeriveTestNamesFromDefaultBenchmarkNameIfNamesUnavailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .configs(cfg, cfg)
      .layouts(layout1(), layout2())
      .buildMultipleByConfig();
    List<String> testNames = List.of(defaultBenchmarkName + " [0]", defaultBenchmarkName + " [1]");
    List<List<String>> expected = List.of(testNames, testNames);
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void builtByConfigMixedBenchmarkNames() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg)
      .config(cfg, "cfg2")
      .layout(layout1())
      .layout(layout2(), "doesn't matter")
      .buildMultipleByConfig();
    List<String> expected = List.of(defaultBenchmarkName, "cfg2");
    assertEquals(expected, getBenchmarkNames(bs));
  }

  @Test
  public void builtByConfigMixedTestNames() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg)
      .config(cfg, "cfg2")
      .layout(layout1())
      .layout(layout2(), "layout2")
      .buildMultipleByConfig();
    List<String> testNames1 = List.of(defaultBenchmarkName + " [0]", "layout2");
    List<String> testNames2 = List.of("cfg2 [0]", "layout2");
    List<List<String>> expected = List.of(testNames1, testNames2);
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksBuiltByLayoutShouldDeriveTestNamesFromConfigs() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "cfg1")
      .config(cfg, "cfg2")
      .layout(layout1(), "doesn't matter")
      .layout(layout2(), "also doesn't matter")
      .buildMultipleByLayout();
    List<String> testNames = List.of("cfg1", "cfg2");
    List<List<String>> expected = List.of(testNames, testNames);
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksBuiltByLayoutShouldDeriveTestNamesFromBenchmarkNameIfConfigNamesUnavailable_from_layout() {
    Stream<Benchmark> bs = Benchmark.builder()
      .layout(layout1(), "layout1")
      .layout(layout2(), "layout2")
      .configs(cfg, cfg)
      .buildMultipleByLayout();
    List<List<String>> expected = List.of(List.of("layout1 [0]", "layout1 [1]"), List.of("layout2 [0]", "layout2 [1]"));
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksBuiltByLayoutShouldDeriveTestNamesFromBenchmarkNameIfConfigNamesUnavailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .name(benchmarkName)
      .layout(layout1(), "doesn't matter")
      .layout(layout2(), "also doesn't matter")
      .configs(cfg, cfg)
      .buildMultipleByLayout();
    List<String> testNames = List.of(benchmarkName + " [0]", benchmarkName + " [1]");
    List<List<String>> expected = List.of(testNames, testNames);
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksBuiltByLayoutShouldDeriveTestNamesFromDefaultBenchmarkNameIfNamesUnavailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .configs(cfg, cfg)
      .layouts(layout1(), layout2())
      .buildMultipleByLayout();
    List<String> testNames = List.of(defaultBenchmarkName + " [0]", defaultBenchmarkName + " [1]");
    List<List<String>> expected = List.of(testNames, testNames);
    List<List<String>> names = bs.map(this::getTestNames).collect(toList());
    assertEquals(expected, names);
  }

  @Test
  public void benchmarksGeneratedByLayoutShouldUseDefaultWhenNoLayoutNamesAvailable() {
    Stream<Benchmark> bs = Benchmark.builder()
      .config(cfg, "doesn't matter")
      .config(cfg, "also doesn't matter")
      .layouts(layout1(), layout2())
      .buildMultipleByLayout();
    List<String> expected = List.of(defaultBenchmarkName, defaultBenchmarkName);
    assertEquals(expected, getBenchmarkNames(bs));
  }

  @Test
  public void plainBuildShouldUseExplicitlyGivenName() {
    Benchmark b = Benchmark.builder()
      .name(benchmarkName)
      .config(cfg, "ignored1")
      .config(cfg, "ignored2")
      .layout(layout1(), "ignored3")
      .layout(layout2(), "ignored4")
      .build();
    assertEquals(benchmarkName, b.name());
  }

  @Test
  public void plainBuildShouldNotDefiveBenchmarkNameWhenMissing() {
    Benchmark b = Benchmark.builder()
      .config(cfg, "ignored1")
      .config(cfg, "ignored2")
      .layout(layout1(), "ignored3")
      .layout(layout2(), "ignored4")
      .build();
    assertEquals(defaultBenchmarkName, b.name());
  }

  @Test
  public void plainBuildShouldUseLayoutsAsTestNames() {
    Benchmark b = Benchmark.builder()
      .config(cfg, "cfg1")
      .config(cfg, "cfg2")
      .layout(layout1(), "layout1")
      .layout(layout2(), "layout2")
      .build();
    List<String> expected = List.of("(cfg1; layout1)", "(cfg1; layout2)", "(cfg2; layout1)", "(cfg2; layout2)");
    assertEquals(expected, getTestNames(b));
  }

  private List<String> getTestNames(Benchmark b) {
    return b.tests().stream().map(Benchmark.Test::name).collect(toList());
  }

  private List<String> getBenchmarkNames(Stream<Benchmark> bs) {
    return bs.map(Benchmark::name).collect(toList());
  }

}
