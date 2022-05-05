class test {
    public static void main(String[] args) {
        A a;
        a = new B();
    }
}

class A{
    int i;
    boolean j;
    public int foo(int x, int y) {return 0;}
}

class C{
    boolean z;
    B b;
    int x;
    public int test() {return 0;}
}

class B extends A{
    int x;
    A a;
    int y;
    public int foo(int i, int j) {return 0;}
    public B test() {
        B b;
        b = new B();
        return b;
    }
}