import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
//import java.util.List;

class SymbolTable {
    public LinkedHashMap<LinkedList<String>, String> varDecl;
    public LinkedHashMap<LinkedList<String>, MethodSymTable> methodDecl;
    public LinkedHashMap<String,String>  classOrder;
    public String MainName;

    public SymbolTable()
        {
            this.varDecl = new LinkedHashMap<>();
            this.methodDecl = new LinkedHashMap<>();
            this.classOrder = new LinkedHashMap<>();
        }

    public void insertVarSymbol(LinkedList<String> key,  String value)
        {   this.varDecl.put(key, value);   }

    public void insertMethodSymbol(LinkedList<String> key,  MethodSymTable value)
        {   this.methodDecl.put(key,value); }

    public boolean checkCorrectness(){
        for(Map.Entry<LinkedList<String>,String>var : varDecl.entrySet()) {
            String check = var.getValue();
            if(check.equals(this.MainName))
                return false;
            LinkedList<String> info = var.getKey();
            String motherClass = info.getLast();
            if(!(check.equals("int") || check.equals("boolean") || check.equals("int[]") || check.equals("boolean[]"))){
                if(classOrder.containsKey(check)) {
                    if (motherClass.equals(classOrder.get(check))) {
                        System.out.println("In symbol table {checkCorrectness} -> motherClass.equals(classOrder.get(check)");
                        return false;
                    }
                }
                else
                    return false;
            }
        }
        for(Map.Entry<LinkedList<String>,MethodSymTable>meth : methodDecl.entrySet()) {
            MethodSymTable check;// = new MethodSymTable();
            LinkedList<String> list = meth.getKey();
            String name = list.getFirst();
            check = meth.getValue();
            String type = check.Type;
            if (type.equals(this.MainName))
                return false;
            if(!(type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]") || classOrder.containsKey(type)))
                return false;
            LinkedHashMap<LinkedList<String>, String> argList;// = new LinkedHashMap<>();
            argList = check.argList;
            for(Map.Entry<LinkedList<String>,String>arg_list : argList.entrySet()) {
                String test = arg_list.getValue();
                if (test.equals(this.MainName))
                    return false;
                if(!(test.equals("int") || test.equals("boolean") || test.equals("int[]") || test.equals("boolean[]") || classOrder.containsKey(test))) {
                    //System.out.println("~~~WE GOT A PROBLEM~~~" + test);
                    return false;
                }
            }
            LinkedHashMap<LinkedList<String>, String> varList;// = new LinkedHashMap<>();
            varList = check.varList;
            for(Map.Entry<LinkedList<String>,String>var_list : varList.entrySet()) {
                String test = var_list.getValue();
                if (test.equals(this.MainName))
                    return false;
                if(!(test.equals("int") || test.equals("boolean") || test.equals("int[]") || test.equals("boolean[]") || classOrder.containsKey(test))) {
                    //System.out.println("~~~WE GOT A PROBLEM~~~");
                    return false;
                }
            }
        }

        return true;
    }

    public void printST()
    {
        System.out.println("\n\n~~~~~PRINTING SYMBOL TABLE~~~~~\n\n");
        System.out.println("Variable Declaration:\n" + this.varDecl);
        System.out.println("\nMethod Declaration:\n");
        for(Map.Entry<LinkedList<String>,MethodSymTable>iteration : methodDecl.entrySet()){
            MethodSymTable prnt;// = new MethodSymTable();
            System.out.println("Method info -> " + iteration.getKey());
            prnt = iteration.getValue();
            prnt.printMethod();
            System.out.println();
        }
        System.out.println("\nClass Order:\n");
        System.out.println("Variable Declaration:\n" + this.classOrder);
        System.out.println("\n\n~~~~~END OF SYMBOL TABLE~~~~~\n\n");
    }

    public String retType(String id, String curr_class, String mother_class, String curr_method) throws Exception{
        LinkedList<String> key = new LinkedList<>();
        LinkedList<String> key1 = new LinkedList<>();
        LinkedList<String> key2 = new LinkedList<>();
        LinkedList<String> key3 = new LinkedList<>();
        String type=null;
        if (curr_class.equals(this.MainName)) {
            key.add(id);
            key.add(curr_class);
            type = this.varDecl.get(key);
            if (type == null)
                throw new Exception("\n\n~~~~~Semantic error~~~~~\n");
        } else {

            MethodSymTable methInfo = new MethodSymTable();
            key.add(curr_method);
            key.add(curr_class);
            methInfo = this.methodDecl.get(key);
            if(methInfo == null)
                throw new Exception("\n\n~~~~~Semantic error~~~~~\n");

            key1.add(id);
            key1.add(curr_method);
            type = methInfo.varList.get(key1);
            if(type == null){
                key2.add(id);
                key2.add(curr_method);
                type = methInfo.argList.get(key2);
            }
            else
                return type;

            if(type == null) {
                key3.add(id);
                key3.add(curr_class);
                type = this.varDecl.get(key3);
            }
            else
                return type;

            if(type==null && mother_class!=null){
                String[] mums = mother_class.split("\\-");
                for (int i=0 ; i < mums.length ; ++i) {
                    LinkedList<String> key4 = new LinkedList<>();
                    key4.add(id);
                    key4.add(mums[i]);
                    type = this.varDecl.get(key4);
                    if(type!=null)
                        return type;
                }
            }
            //System.out.println("SHOULDA FIND ALREADY SOMETHING");
            if(type == null)
                throw new Exception("\n\n~~~~~Semantic error~~~~~\nIn retType of SymbolTable: didn't find the type needed.");
        }
        return type;
    }

    public void Offsets() {
        for(Map.Entry<String,String> class_ord : classOrder.entrySet()) {
            String mum_class = class_ord.getValue();
            String curr_class = class_ord.getKey();
            if(curr_class.equals(MainName))
                continue;
            String child_class=null;
           if(mum_class!=null)
                continue;
            for(Map.Entry<String,String> class_ord1 : classOrder.entrySet()) {
                String s1 = class_ord1.getValue();
                if(s1==null)
                    continue;
//                System.out.println("curr_class: " + curr_class);
//                System.out.println("s1: " + s1);
                if(s1.equals(curr_class)){
                    child_class = class_ord1.getKey();
                    break;
                }
            }
            //System.out.println("curr_class: " + curr_class);
            int offset = this.printOffsets(0,curr_class,false);

            if(child_class==null)
                continue;

            //System.out.println("child_class: " + child_class);
            this.printOffsets(offset,child_class,true);
        }
    }

    public int printOffsets(int offset,String curr_class, Boolean child) {
        for (Map.Entry<LinkedList<String>, String> curr_var1 : varDecl.entrySet()){
            String cl = curr_var1.getKey().getLast();
            if(!cl.equals(curr_class))
                continue;
            String id = curr_var1.getKey().getFirst();
            String type = curr_var1.getValue();
            System.out.println(cl + "." + id + ":" + offset);
            if(type.equals("int"))
                offset += 4;
            else if (type.equals("boolean"))
                offset++;
            else
                offset += 8;
        }
        for (Map.Entry<LinkedList<String>, MethodSymTable> curr_meth1 : methodDecl.entrySet()){
            String cl = curr_meth1.getKey().getLast();
            if(!cl.equals(curr_class))
                continue;
            String id = curr_meth1.getKey().getFirst();
            if(child){
                String mum = this.classOrder.get(cl);
                LinkedList<String> key = new LinkedList<>();
                key.add(id);
                key.add(mum);
                if(methodDecl.containsKey(key))
                    continue;
            }
            MethodSymTable methInfo = new MethodSymTable();
            methInfo = curr_meth1.getValue();
            String type = methInfo.Type;
            System.out.println(cl + "." + id + ":" + offset);
            //System.out.println("type: " + type);
            if(type.equals("int"))
                offset += 4;
            else if (type.equals("boolean"))
                offset++;
            else
                offset += 8;
        }
        return offset;
    }
}
//<[methName,methClass] -> {Type,[argList],[varList]}>
class MethodSymTable {
    public String Type;
    public LinkedHashMap<LinkedList<String>, String>  argList;
    public LinkedHashMap<LinkedList<String>, String> varList;

    public MethodSymTable() {
        this.argList = new LinkedHashMap<>();
        this.varList = new LinkedHashMap<>();
    }

    public void printMethod(){
        System.out.println("Type: " + this.Type);
        System.out.println("Argument List:\n" + this.argList);
        System.out.println("Variable List:\n" + this.varList);
    }

    public boolean isSame(MethodSymTable methTest){
        LinkedHashMap<LinkedList<String>, String> test;// = new LinkedHashMap<>();
        test = methTest.argList;
        if(this.argList.size() != test.size()) {
//            System.out.println("Extended argList doesn't match original one.");
            return false;
        }

        if(!this.Type.equals(methTest.Type))
            return false;

        LinkedList<String> list1 = new LinkedList<>();
        LinkedList<String> list2 = new LinkedList<>();

        for (Map.Entry<LinkedList<String>, String> check1 : argList.entrySet())
            list1.add(check1.getValue());

        for (Map.Entry<LinkedList<String>, String> check2 : test.entrySet())
            list2.add(check2.getValue());

        for (int i=0 ; i<list1.size() ; ++i) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }
}
