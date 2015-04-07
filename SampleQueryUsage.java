public class SampleQueryUsage {
    public static void main(String [] args) {
        
        Query p = Query.readQuery();
        if(p.isSelect()) {
            // sample queries:
            // select * from employees;
            // select firstName, lastName from employees where id=56;

            System.out.println("\nSELECT query found!\n----------------------");
            String [] fields = p.getSelectFields();
            System.out.println("Here is relation name: " + p.getRelationName());
            System.out.print("Here are the fields: ");
            for(int i = 0; i < fields.length; i++) {
                System.out.print(fields[i] + ",");
            }
            System.out.println();

            if(p.getWhereID() != -1) {
                System.out.println("Here is the query ID: " + p.getWhereID());
            }
        
        } else if(p.isUpdate()) {
            // sample queries:
            // update employees set last=jordan;
            // update employees set first=mike where id=5;

            System.out.println("\nUPDATE query found!\n----------------------");
            System.out.println("Here is relation name: " + p.getRelationName());
            System.out.println("Here is the update FIELD: " + p.getUpdateField());
            System.out.println("Here is the update VALUE: " + p.getUpdateValue());
            if(p.getWhereID() != -1) {
                System.out.println("Here is the query ID: " + p.getWhereID());
            }

        } else if(p.isDelete()) {
            // sample queries 
            // delete from employees;
            // delete from employees where id=7;

            System.out.println("\nDELETE query found!\n----------------------");
            System.out.println("Here is relation name: " + p.getRelationName());

            if(p.getWhereID() != -1) {
                System.out.println("Here is the query ID: " + p.getWhereID());
            }
            
        } else if(p.isInsert()) {
            // sample queries 
            // insert into employees values (hey,man,this,is,a,test);

            System.out.println("\nINSERT query found!\n----------------------");
            System.out.println("Here is relation name: " + p.getRelationName());
            
            String [] vals = p.getInsertValues();
            System.out.print("Here are the values: ");
            for(int i = 0; i < vals.length; i++) {
                System.out.print(vals[i] + ",");
            }
            System.out.println();

        }
    }
}

