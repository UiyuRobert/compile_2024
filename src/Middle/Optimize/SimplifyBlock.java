package Middle.Optimize;

import Middle.LLVMIR.IRModule;
import Middle.LLVMIR.Values.IRBasicBlock;
import Middle.LLVMIR.Values.IRFunction;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.IRLabel;
import Middle.LLVMIR.Values.Instructions.Terminal.IRBr;
import Middle.LLVMIR.Values.Instructions.Terminal.IRReturn;

import java.util.*;

public class SimplifyBlock {
    private IRModule module;

    public SimplifyBlock(IRModule module) {
        this.module = module;
    }

    public void run() {
        for (IRFunction function : module.getFunctions()) {
            removeExtraInstr(function);
            deleteUnreachableBlocks(function);
            jumpThreading(function);
        }
    }

    /**
     * 删除不可到达的基本块
     * */
    private void deleteUnreachableBlocks(IRFunction function) {
        IRBasicBlock entryBlock = function.getEntryBlock();
        HashSet<IRBasicBlock> reachableBlocks = new HashSet<>();
        bfs(entryBlock, reachableBlocks);
        Iterator<IRBasicBlock> iterator = function.getBlocks().iterator();
        while (iterator.hasNext()) {
            IRBasicBlock block = iterator.next();
            if (!reachableBlocks.contains(block)) {
                iterator.remove();
            }
        }
    }

    private void bfs(IRBasicBlock entry, HashSet<IRBasicBlock> reachable) {
        Stack<IRBasicBlock> stack = new Stack<>();
        stack.push(entry);
        reachable.add(entry);

        while (!stack.isEmpty()) {
            IRBasicBlock block = stack.pop();
            IRInstruction instr = block.getLastInstruction();
            if (instr instanceof IRBr) { // 是跳转语句
                IRBr br = (IRBr) instr;
                if (br.noCondition()) {
                    IRBasicBlock next = br.getTargetBlock("");
                    if (!reachable.contains(next)) {
                        reachable.add(next);
                        stack.push(next);
                    }

                } else {
                    IRBasicBlock trueBlock = br.getTargetBlock("true");
                    IRBasicBlock falseBlock = br.getTargetBlock("false");
                    if (!reachable.contains(trueBlock)) {
                        reachable.add(trueBlock);
                        stack.push(trueBlock);
                    }
                    if (!reachable.contains(falseBlock)) {
                        reachable.add(falseBlock);
                        stack.push(falseBlock);
                    }
                }
            }
        }
    }

    /**
     * 跳转串联
     * */
    private void jumpThreading(IRFunction function) {
        Iterator<IRBasicBlock> iterator = function.getBlocks().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            IRBasicBlock block = iterator.next();
            IRInstruction instr = block.getLastInstruction();
            // 如果是无条件跳转
            if (instr instanceof IRBr && ((IRBr) instr).noCondition()) {
                // 如果有下一个块
                if (index + 1 < function.getBlocks().size()) {
                    IRBasicBlock nextBlock = function.getBlocks().get(index + 1);
                    // 下一个块的 label
                    IRInstruction label = nextBlock.getInstructions().get(0);
                    // 下一个块与无条件跳转到的块是同一个块,并且只有当前块可以到达下一个块
                    if (nextBlock == ((IRBr) instr).getTargetBlock("")
                            && ((IRLabel)label).isOnlyOneJump()) {
                        // 获取将当前块中的指令
                        ArrayList<IRInstruction> instructions = block.getInstructions();
                        // 删除跳转指令
                        instructions.remove(instr);
                        // 将下一个块中中的指令加入到当前块
                        ArrayList<IRInstruction> newInstructions = new ArrayList<>(instructions);
                        // 获取下一个块中的指令
                        ArrayList<IRInstruction> nextInstructions = nextBlock.getInstructions();
                        // 删除 label
                        nextInstructions.remove(0);
                        // 合并指令
                        newInstructions.addAll(nextInstructions);
                        block.setInstructions(newInstructions);
                        // 删除下一个块
                        if (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                    }
                }
            }
            ++index;
        }
    }

    /**
     * 删除基本块中跳转指令后的指令
     * */
    private void removeExtraInstr(IRFunction function) {
        for (IRBasicBlock block : function.getBlocks()) {
            boolean canRemove = false;
            Iterator<IRInstruction> iterator = block.getInstructions().iterator();
            while (iterator.hasNext()) {
                IRInstruction instr = iterator.next();
                if (canRemove)
                    iterator.remove();
                else if (instr instanceof IRBr || instr instanceof IRReturn)
                    canRemove = true;
            }
        }
    }
}
