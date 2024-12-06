package Middle.Optimize;

import Middle.LLVMIR.IRModule;

public class Optimizer {
    private static final Optimizer optimizer = new Optimizer();

    public static Optimizer getOptimizer() { return optimizer; }

    private Optimizer() {}

    public void run(IRModule module) {
        new SimplifyBlock(module).run();

        new DataFlowBuilder(module).run();
    }
}
