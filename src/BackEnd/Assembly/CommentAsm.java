package BackEnd.Assembly;

public class CommentAsm extends Asm {
    private String comment;

    public CommentAsm(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "\n# " + comment;
    }
}
