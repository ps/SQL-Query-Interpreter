import java.io.*;
import java.util.*;

public class Query {
    /*########## Query class instance vars ###########*/
    private static BufferedReader br =
        new BufferedReader(new InputStreamReader(System.in));

    private boolean isSelect, isUpdate, isDelete, isInsert;
    protected boolean error;
    protected String relationName;
    protected int whereID;
    /*---------- Query class instance vars -----------*/

    /*########### Query constructors ###########*/
    public Query() {
        initialize();
    }

    public Query(char type) {
        initializeType(type);
    }
    /*---------- Query constructors -----------*/

    /*########### Query public non-static methods ###########*/
    public String[] getSelectFields() {
        System.out.println("WARNING, THIS IS NOT A SELECT QUERY!!");
        return null;
    }
    public String [] getInsertValues() {
        System.out.println("WARNING, THIS IS NOT AN INSERT QUERY!!");
        return null;   
    }
    public String getUpdateField() {
        System.out.println("WARNING, THIS IS NOT AN UPDATE QUERY!!");
        return null;
    }
    public String getUpdateValue() {
        System.out.println("WARNING, THIS IS NOT AN UPDATE QUERY!!");
        return null;
    }

    public boolean error() { return error; }
    public int getWhereID() { return whereID; }
    public String getRelationName() { return relationName; }
    public boolean isSelect() { return isSelect; }
    public boolean isUpdate() { return isUpdate; }
    public boolean isDelete() { return isDelete; }
    public boolean isInsert() { return isInsert; }
    /*---------- Query public non-static methods -----------*/

    /*########### Query public STATIC methods ###########*/
    public static Query readQuery() {
        Query finalQuery = null;
        boolean error = false;
        while(true) {
            if(error) {
                System.out.println("Wrong query input. Try again.");
            }
            error = true;
            String q = getInput();
            finalQuery = readQuery(q);
            if(finalQuery == null) {
                continue;
            } else {
                break;
            }
            
        }
        return finalQuery;
    }

    public static Query readQuery(String q) {
        Query finalQuery = null;

        if(q.length() < 6) {
            // query should be longer than 6 characters
            return finalQuery;
        }

        StringTokenizer tok = new StringTokenizer(q.trim());
        if(tok.countTokens() < 3) {
            // min tokens for delete query (shortest query)
            // ex: delete from employees;
            return finalQuery;
        }

        String qType = tok.nextToken();
        if(qType.length() < 6) {
            // select, delete, update, insert are all of 6 characters
            return finalQuery;
        }

        qType = qType.toLowerCase();
        if(qType.equals("select")) {
            finalQuery = new Select(tok);
        } else if(qType.equals("delete")) {
            finalQuery = new Delete(tok);
        } else if(qType.equals("insert")) {
            finalQuery = new Insert(tok);
        } else if(qType.equals("update")) {
            finalQuery = new Update(tok);
        }

        if(finalQuery != null && finalQuery.error()) {
            // error has occured within child parsing
            return null;
        }
        return finalQuery;
    }
    /*---------- Query public STATIC methods -----------*/

    /*########### Query private/protected helper methods ###########*/
    private static String getInput() {
        while(true) {
            System.out.print("Enter sql> ");
            try {
                return br.readLine();
            } catch (IOException e) {
                // shouldn't happen
            }
        }
    }

    private void initialize() {
        whereID = -1;
        relationName = "NOT FOUND";
        error = false;
        isSelect = false;
        isUpdate = false;
        isDelete = false;
        isInsert = false;
    }

    private void initializeType(char type) {
        initialize(); 
        switch(type) {
            case 's':
                isSelect = true;
                break;
            case 'u':
                isUpdate = true;
                break;
            case 'd':
                isDelete = true;
                break;
            case 'i':
                isInsert = true;
                break;
        }
    }

    protected void parseWhere(StringTokenizer tok) {
        String potentialWhere = tok.nextToken();
        potentialWhere = potentialWhere.toLowerCase();
        if(potentialWhere.equals("where") && tok.hasMoreTokens()) {
            String potentialID = tok.nextToken();
            int equalSignPos = potentialID.indexOf("=");
            int semicolenPos = potentialID.indexOf(";");
            if(equalSignPos != -1 && semicolenPos != -1) {
                String id = potentialID.substring(equalSignPos + 1, semicolenPos);
                try {
                    this.whereID = Integer.parseInt(id);
                } catch (NumberFormatException e) {
                    this.error = true;
                } catch (Exception e) {
                    this.error = true;
                }
            } else {
                this.error = true;
            }
        } else {
            this.error = true;
        }
    }
    /*---------- Query private/protected helper methods -----------*/


    public static class Select extends Query {
        private ArrayList<String> fields;
        private StringTokenizer tok;
        public Select(StringTokenizer tok) {
            super('s');  
            fields = new ArrayList<String>();
            // at this point 'tok' does not have the first 'select' token
            this.tok = tok;
            parseFields();
            if(this.error) {
                // there was an error parsing fields
                return;
            }
            // at this point next token should be relation name
            String relationName = tok.nextToken();
            if(relationName.indexOf(";") != -1 && !tok.hasMoreTokens()) {
                // no where clause
                relationName = relationName.replace(";", "");
                this.relationName = relationName;
            } else if(tok.hasMoreTokens()) {
                // where clause present
                this.relationName = relationName;
                parseWhere(tok);
            } else {
                this.error = true;
            }
        }
        public String [] getSelectFields() {
            return fields.toArray(new String[fields.size()]);
        }

        private void parseFields() {
            String potentialFields = tok.nextToken();
            String potentialFrom = tok.nextToken();
            potentialFrom = potentialFrom.toLowerCase();
            if((potentialFields.indexOf(",") != -1 || potentialFields.equals("*")) && potentialFrom.equals("from")) {
                populateFieldsList(potentialFields);
            } else {
                this.error = true;
            }
        }
        private void populateFieldsList(String fls) {
            StringTokenizer fieldsTok = new StringTokenizer(fls, ",");
            while(fieldsTok.hasMoreTokens()) {
                fields.add(fieldsTok.nextToken());
            }
        }
    }

    public static class Update extends Query {
        private StringTokenizer tok;
        private FieldValue fv;

        public Update(StringTokenizer tok) {
            super('u');   
            // at this point query doesn't hold 'update' token
            this.tok = tok;
            this.fv = null;
            // min token requirement is 3 so this access is ok
            String potentialRelName = tok.nextToken();
            this.relationName = potentialRelName;
            String potentialSet = tok.nextToken();
            potentialSet = potentialSet.toLowerCase();
            if(potentialSet.equals("set") && tok.hasMoreTokens()) {
                String potentialSetClause = tok.nextToken();
                if(potentialSetClause.indexOf(";") != -1) {
                    // no where clause
                    potentialSetClause = potentialSetClause.replace(";", "");
                    parseFieldVal(potentialSetClause);
                } else if(tok.hasMoreTokens()) {
                    parseFieldVal(potentialSetClause);
                    // parse where clause
                    parseWhere(tok);
                } else {
                    this.error = true;
                }
            } else {
                this.error = true;
            }
        }

        public String getUpdateField() { return fv.getField(); }
        public String getUpdateValue() { return fv.getValue(); }

        private void parseFieldVal(String fieldVal) {
            StringTokenizer fvTok = new StringTokenizer(fieldVal, "=");
            if(fvTok.countTokens() != 2) {
                this.error = true;
            } else {
                this.fv = new FieldValue(fvTok.nextToken(), fvTok.nextToken());
            }
        }
    }

    public static class Delete extends Query {
        private StringTokenizer tok;
        public Delete(StringTokenizer tok) {
            super('d');    
            // at this point the query does not hold the 'delete' token
            this.tok = tok;
            // can access tokens as below because min number of tokens is 3 anyway
            String potentialFrom = tok.nextToken();
            potentialFrom = potentialFrom.toLowerCase();
            String potentialRelName = tok.nextToken();
            this.relationName = potentialRelName;
            if(potentialFrom.equals("from") && tok.hasMoreTokens()) {
                parseWhere(tok);
            } else if(potentialFrom.equals("from") && !tok.hasMoreTokens()){
                this.relationName = this.relationName.replace(";", "");
            } else {
                this.error = true;
            }
        }
    }

    public static class Insert extends Query {
        private StringTokenizer tok;
        private ArrayList<String> insertValues;

        public Insert(StringTokenizer tok) {
            super('i');
            insertValues = new ArrayList<String>();    
            // at this point no 'insert' token present
            this.tok = tok;

            String potentialInto = tok.nextToken();
            potentialInto = potentialInto.toLowerCase();
            String potentialRelName = tok.nextToken();
            this.relationName = potentialRelName;

            if(potentialInto.equals("into") && tok.countTokens() == 2) {
                String potentialValKeyword = tok.nextToken();
                potentialValKeyword = potentialValKeyword.toLowerCase();
                String potentialValues = tok.nextToken();
                if(potentialValKeyword.equals("values")) {
                    parseValues(potentialValues);
                } else {
                    this.error = true;
                }
            } else {
                this.error = true;
            }
        }

        public String [] getInsertValues() {
            return insertValues.toArray(new String[insertValues.size()]);
        }

        private void parseValues(String vals) {
            int openParPos = vals.indexOf("(");
            int closeParPos = vals.indexOf(")");
            if(openParPos == -1 || closeParPos == -1) {
                this.error = true;
                return;
            }
            vals = vals.substring(openParPos + 1, closeParPos);
            StringTokenizer valsTok = new StringTokenizer(vals, ",");
            if(valsTok.countTokens() <= 1) {
                this.error = true;
                return;
            }
            while(valsTok.hasMoreTokens()) {
                insertValues.add(valsTok.nextToken());
            }
        }
    }

    public static class FieldValue {
        private String field;
        private String value;
        public FieldValue (String f, String v) {
            field = f;
            value = v;
        }
        public String getField() { return field; }
        public String getValue() { return value; }
    }




}


