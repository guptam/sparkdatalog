package socialite.Absyn; // Java Package generated by the BNF Converter.

public abstract class Head implements java.io.Serializable {
  public abstract <R,A> R accept(Head.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(socialite.Absyn.HeadSingle p, A arg);

  }

}