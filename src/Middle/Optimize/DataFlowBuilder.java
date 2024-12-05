package Middle.Optimize;

import Middle.LLVMIR.IRModule;
import Middle.LLVMIR.Values.IRBasicBlock;
import Middle.LLVMIR.Values.IRFunction;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.Terminal.IRBr;

import java.util.ArrayList;
import java.util.HashMap;

public class DataFlowBuilder {
    private IRModule irModule;

    // 当前函数的流图
    // - 当前块的前驱
    private HashMap<IRBasicBlock, ArrayList<IRBasicBlock>> preGraph;
    // - 当前块的后继
    private HashMap<IRBasicBlock, ArrayList<IRBasicBlock>> sucGraph;

    // 支配树、支配关系
    int time; // DFS遍历的计时器，用于分配DFS编号
    ArrayList<IRBasicBlock> dfsList;  // DFS遍历后的节点列表，按DFS顺序排列

    public DataFlowBuilder(IRModule irModule) {
        this.irModule = irModule;
        preGraph = null;
        sucGraph = null;
        time = 0;
        dfsList = null;
    }

    public void run() {
        for (IRFunction function : irModule.getFunctions()) {
            initialize(function);
            getCFG(function);
            buildDominatorTree(function.getEntryBlock());
            printDominatorTree(function);
        }
    }

    private void initialize(IRFunction function) {
        preGraph = new HashMap<>();
        sucGraph = new HashMap<>();
        for (IRBasicBlock block : function.getBlocks()) {
            preGraph.put(block, new ArrayList<>());
            sucGraph.put(block, new ArrayList<>());
        }
        time = 0;
        dfsList = new ArrayList<>();
    }

    private void getCFG(IRFunction function) {
        ArrayList<IRBasicBlock> blocks = function.getBlocks();
        for (IRBasicBlock block : blocks) {
            // br 出现在最后一条指令
            IRInstruction instr = block.getLastInstruction();

            // 只有跳转指令会改变数据流方向
            if (instr instanceof IRBr) {
                IRBr br = (IRBr) instr;
                if (br.noCondition()) { // 无条件跳转
                    IRBasicBlock target = br.getTargetBlock("");
                    preGraph.get(target).add(block);
                    sucGraph.get(block).add(target);
                } else { // 有条件跳转，两个后继块
                    IRBasicBlock trueBlock = br.getTargetBlock("true");
                    IRBasicBlock falseBlock = br.getTargetBlock("false");
                    preGraph.get(trueBlock).add(block);
                    preGraph.get(falseBlock).add(block);
                    sucGraph.get(block).add(trueBlock);
                    sucGraph.get(block).add(falseBlock);
                }
            }
        }
        // 将前驱和后继信息写入基本块
        for (IRBasicBlock block : blocks) {
            block.setPreBlocks(preGraph.get(block));
            block.setSucBlocks(sucGraph.get(block));
        }
        // 将 CFG 写到当前函数中
        function.setPreGraph(preGraph);
        function.setSucGraph(sucGraph);
    }

    /**
     * 进行深度优先搜索（DFS）遍历，分配DFS编号
     * @param curBlock 当前访问的基本块
     * @param parentBlock 当前基本块的父节点
     */
    private void dfs(IRBasicBlock curBlock, IRBasicBlock parentBlock) {
        time++;
        curBlock.dfsOrder = time; // 分配DFS编号
        curBlock.semi = time; // 初始化半支配者为自身
        curBlock.label = curBlock; // 初始化标签为自身
        curBlock.ancestor = null; // 初始化祖先为空
        if (parentBlock != null) {
            curBlock.parent = parentBlock.dfsOrder; // 记录DFS树中的父节点编号
        }
        dfsList.add(curBlock); // 将节点添加到DFS列表中
        for (IRBasicBlock v : curBlock.getSucBlocks()) { // 遍历所有后继节点
            if (v.dfsOrder == 0) { // 如果后继节点尚未访问
                dfs(v, curBlock); // 递归访问后继节点
            }
        }
    }

    /**
     * 查找函数，带路径压缩，用于并查集操作
     * @param u 需要查找的基本块
     * @return u 的代表基本块
     */
    private IRBasicBlock find(IRBasicBlock u) {
        if (u.ancestor == null) {
            return u; // 如果没有祖先，返回自身
        } else {
            compress(u); // 进行路径压缩
            // 返回标签中半支配者编号最小的节点
            return (u.label.semi < u.ancestor.label.semi) ? u.label : u.ancestor.label;
        }
    }

    /**
     * 路径压缩函数，递归压缩路径
     * @param u 需要压缩路径的基本块
     */
    private void compress(IRBasicBlock u) {
        if (u.ancestor.ancestor != null) { // 如果祖先的祖先不为空
            compress(u.ancestor); // 递归压缩祖先节点的路径
            // 更新标签，如果祖先的标签的半支配者编号更小
            if (u.ancestor.label.semi < u.label.semi) {
                u.label = u.ancestor.label;
            }
            u.ancestor = u.ancestor.ancestor; // 更新祖先为祖先的祖先
        }
    }

    /**
     * 并查集的链接操作，将节点v的祖先设置为节点u
     * @param u 要链接的节点u
     * @param v 要链接的节点v
     */
    private void link(IRBasicBlock u, IRBasicBlock v) {
        v.ancestor = u; // 将v的祖先设置为u
    }

    /**
     * 计算所有基本块的半支配者
     */
    private void computeSemiDominator() {
        int n = dfsList.size();
        // 按照逆序DFS顺序处理所有节点
        for (int i = n - 1; i >= 1; i--) {
            IRBasicBlock u = dfsList.get(i);
            // 遍历所有前驱节点，找出最小的半支配者编号
            for (IRBasicBlock v : u.getPreBlocks()) {
                IRBasicBlock vAncestor = find(v); // 查找前驱节点v的代表节点
                if (vAncestor.semi < u.semi) {
                    u.semi = vAncestor.semi; // 更新u的半支配者编号
                }
            }
            IRBasicBlock semiBlock = dfsList.get(u.semi - 1); // 获取半支配者节点
            semiBlock.bucket.add(u); // 将u加入半支配者节点的桶中
            if (u.parent != 0) { // 如果u有父节点
                link(dfsList.get(u.parent - 1), u); // 链接父节点和u
            }
            // 遍历父节点的桶，更新立即支配者
            for (IRBasicBlock v : dfsList.get(u.parent - 1).bucket) {
                IRBasicBlock y = find(v); // 查找v的代表节点
                if (y.semi < v.semi) {
                    v.idom = y; // 如果y的半支配者更小，则设置y为v的立即支配者
                } else {
                    v.idom = dfsList.get(u.semi - 1); // 否则，设置半支配者节点为v的立即支配者
                }
            }
            dfsList.get(u.parent - 1).bucket.clear(); // 清空父节点的桶
        }
    }

    /**
     * 计算每个节点的立即支配者
     */
    public void computeImmediateDominator() {
        computeSemiDominator(); // 先计算半支配者
        // 遍历所有节点，赋值立即支配者
        for (int i = 1; i < dfsList.size(); i++) { // 从第二个节点开始（第一个是入口）
            IRBasicBlock u = dfsList.get(i);
            if (u.idom != null && u.idom != dfsList.get(u.semi - 1)) {
                u.idom = u.idom.idom; // 如果当前立即支配者不等于半支配者节点，递归设置
            } else {
                u.idom = dfsList.get(u.semi - 1); // 否则，设置半支配者节点为立即支配者
            }
        }
    }

    /**
     * 构建支配树，主入口方法
     * @param entryBlock 入口节点
     */
    public void buildDominatorTree(IRBasicBlock entryBlock) {
        dfs(entryBlock, null); // 进行DFS遍历，分配DFS编号
        computeImmediateDominator(); // 计算立即支配者
    }

    /**
     * 打印支配树的结果
     */
    public void printDominatorTree(IRFunction function) {
        for (IRBasicBlock u : function.getBlocks()) { // 遍历所有节点
            if (u.idom != null) { // 如果有立即支配者
                System.out.println(u.getName() + " is dominated by " + u.idom.getName());
            } else {
                System.out.println(u.getName() + " is the entry block"); // 否则，是入口节点
            }
        }
    }
}
