public class Tuple<A, B> 
{
    private A first;
    private B second;

    public Tuple(A First, B Second) 
    {
        this.first = First;
        this.second = Second;
    }

    public A getFirst() 
    {
        return this.first;
    }

    public B getSecond() 
    {
        return this.second;
    }
}
