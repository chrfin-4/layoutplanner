package se.ltu.kitting.model;

import java.util.*;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;

import static java.util.stream.Collectors.joining;

// TODO: The different scenarios should be defined separately somewhere.
public class Main {

  public static void main(String[] args) throws Exception {
    SolverFactory<Layout> solverFactory = SolverFactory.createFromXmlResource("solverConf.xml");
    Solver<Layout> solver = solverFactory.buildSolver();

    List<Part> parts = new ArrayList<>();
    Surface surface = new Surface();
    Layout unsolvedLayout = new Layout(surface, parts);
    runSolver(solver, unsolvedLayout);
  }

  public static void runSolver(Solver<Layout> solver, Layout unsolvedLayout) {
    Layout solvedLayout = solver.solve(unsolvedLayout);
    System.out.println("After " + solver.getTimeMillisSpent()/1000 + " s:");
    System.out.println("Score: " + solvedLayout.getScore());
    System.out.println("Positions:");
    System.out.println(solvedLayout.getParts().stream().map(p -> p.toString() + ": " + String.valueOf(p.getPosition())).collect(joining(", ")));
  }

}
