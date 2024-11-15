package BackEnd.Assembly;

/**
 * 全局变量，是 int char const [] 的组合
 * llvm ir 中的私有全局变量均翻译为 .asciiz， char[] 也
 * */
public class GlobalVarAsm extends Asm {
    public static class Asciiz extends GlobalVarAsm {
        private String name;
        private String content;

        public Asciiz(String name, String content) {
            this.name = name;
            this.content = content.replace("\\0A", "\\n");
        }

        @Override
        public String toString() {
            return name + ": .asciiz \"" + content + "\"\n";
        }
    }

    public static class Byte extends GlobalVarAsm {
        private String name;
        private int ascii;

        public Byte(String name, int ascii) {
            this.name = name;
            this.ascii = ascii;
        }

        @Override
        public String toString() {
            return name + ": .byte " + ascii + "\n";
        }
    }

    public static class Word extends GlobalVarAsm {
        private String name;
        private int value;

        public Word(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + ": .word " + value + "\n";
        }
    }

    public static class Space extends GlobalVarAsm {
        private String name;
        private int size;

        public Space(String name, int size) {
            this.name = name;
            this.size = size;
        }

        @Override
        public String toString() {
            return name + ": .space " + size + "\n";
        }
    }
}
