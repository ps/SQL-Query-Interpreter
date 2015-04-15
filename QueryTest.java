public class QueryTest {

    public static void main(String [] args) throws Exception{
        generalTests();
        selectTests();
        deleteTests();
        updateTests();
        insertTests();
    }

    public static void insertTests() throws Exception {
        // insert no actual values present
        Query q = Query.readQuery("insert into employees values;");
        assertTrue(q == null, 0, "INSERT");
        
        // no values specified bad insert
        q = Query.readQuery("insert into employees values ();");
        assertTrue(q == null, 1, "INSERT");

        // no values specified bad insert
        q = Query.readQuery("insert into employees values (test,);");
        assertTrue(q == null, 2, "INSERT");

        q = Query.readQuery("insert into employees values (test,test2);");
        assertTrue(q != null, 3, "INSERT");
        assertTrue(q.getRelationName().equals("employees"), 4, "INSERT"); 
        String [] values = q.getInsertValues();
        assertTrue(values.length == 2, 4, "INSERT");
        assertTrue(values[0].equals("test"), 5, "INSERT");
        assertTrue(values[1].equals("test2"), 6, "INSERT");

	// no space between values keyword and actual values
	q = Query.readQuery("INSERT INTO employees VALUES(ha,to,bum,50,$500);");
        assertTrue(q == null, 7, "INSERT");
        
        // check that one value with a trailing comma fails
	q = Query.readQuery("INSERT INTO employees VALUES(ha,);");
        assertTrue(q == null, 8, "INSERT");

        // check that one value with a trailing comma fails
	q = Query.readQuery("INSERT INTO employees VALUES(ha,);");
        assertTrue(q == null, 8, "INSERT");
    }

    public static void generalTests() throws Exception {
        // catch too short query error
        Query q = Query.readQuery("i");
        assertTrue(q == null, 0,  "GENERAL");
        
        // catch too few tokens in query error
        q = Query.readQuery("insert into");
        assertTrue(q == null, 1, "GENERAL");
        
        // catch query type not recognized
        q = Query.readQuery("insert-haha into stuff here");
        assertTrue(q == null, 2, "GENERAL");
    }

    public static void updateTests() throws Exception {
        // update semicolen missing
        Query q = Query.readQuery("update employees set last=jordan");
        assertTrue(q == null, 0, "UPDATE");
        
        // check good query no where clause
        q = Query.readQuery("update employees set last=jordan;");
        assertTrue(q.getRelationName().equals("employees"), 2, "UPDATE");
        
        // check good query with where clause id
        q = Query.readQuery("update employees set last=jordan where id=678;");
        assertTrue(q.getRelationName().equals("employees"), 3, "UPDATE");
        assertTrue(q.getWhereID() == 678, 4, "UPDATE");
        String f = q.getUpdateField();
        String v = q.getUpdateValue();
        assertTrue(f.equals("last"), 5, "UPDATE");
        assertTrue(v.equals("jordan"), 6, "UPDATE");
    }


    public static void deleteTests() throws Exception {
        // delete all data test
        Query q = Query.readQuery("delete from employees;");
        assertTrue(q != null, 0, "DELETE");
        assertTrue(q.getRelationName().equals("employees"), 1, "DELETE");
        
        // check good query with where id
        q = Query.readQuery("delete from employees where id=45;");
        assertTrue(q != null, 2, "DELETE");
        assertTrue(q.getWhereID() == 45, 3, "DELETE");
    }

    public static void selectTests() throws Exception {
        // correct select query with '*' argument
        Query q = Query.readQuery("select * from employees;");
        assertTrue(q != null, 0, "SELECT");
        String [] sFields = q.getSelectFields();
        assertTrue(sFields.length == 1, 2, "SELECT");
        assertTrue(sFields[0].equals("*"), 3, "SELECT");
        
        // correct select query with fields, test that fields are returned
        q = Query.readQuery("select field1,field2,man from employees;");
        assertTrue(q != null, 4, "SELECT");
        sFields = q.getSelectFields();
        assertTrue(sFields.length == 3, 5, "SELECT");
        assertTrue(sFields[0].equals("field1"), 6, "SELECT");
        assertTrue(sFields[1].equals("field2"), 7, "SELECT");
        assertTrue(sFields[2].equals("man"), 8, "SELECT");
        assertTrue(q.getRelationName().equals("employees"), 9, "SELECT");
        
        // test proper select with no specing in fields and no spacing in where
        q = Query.readQuery("select field1,field2,man from employees where id=2;");
        assertTrue(q != null, 10, "SELECT");
        assertTrue(q.getWhereID() == 2, 11, "SELECT");
        String relName = q.getRelationName();
        assertTrue(q.getRelationName().equals("employees"), 12, "SELECT");
        
        // catch spacing error in fields
        q = Query.readQuery("select field1, field2,man from employees;");
        assertTrue(q == null, 13, "SELECT");

        // catch spacing error with semicolen
        q = Query.readQuery("select field1,field2,man from employees ;");
        assertTrue(q == null, 14, "SELECT");

        // catch no semicolen 
        q = Query.readQuery("select field1,field2,man from employees ");
        assertTrue(q == null, 15, "SELECT");


        // catch spacing error in where clause
        q = Query.readQuery("select field1,field2,man from employees where id =2;");
        assertTrue(q == null, 16, "SELECT");

        // catch no semicolen after where
        q = Query.readQuery("select field1,field2,man from employees where id=2");
        assertTrue(q == null, 17, "SELECT");

        // check that one field works properly now
        q = Query.readQuery("select field1 from employees where id=2;");
        assertTrue(q != null, 18, "SELECT");

        // make sure trailing comma not outputted
        q = Query.readQuery("select field1,field2,man,hey, from employees where id=2;");
        assertTrue(q != null, 19, "SELECT");
        String [] q19 = q.getSelectFields();
        assertTrue(q19[0].equals("field1"), 20, "SELECT");
        assertTrue(q19[1].equals("field2"), 21, "SELECT");
        assertTrue(q19[2].equals("man"), 22, "SELECT");
        assertTrue(q19[3].equals("hey"), 23, "SELECT");
        assertTrue(q19.length == 4, 24, "SELECT");
    }

    public static void assertTrue(boolean val, int id, String name) throws Exception {
        if(!val)
            throw new Exception("Assertion is false " + id + "-" + name);
        else 
            System.out.println(name + " test #" + id + " PASSED!");
    }
}

