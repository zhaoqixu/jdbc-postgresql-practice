import java.util.Scanner;
import java.sql.*;

 
public class Project3 {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Connection conn = null;
        try {
            //load driver
            Class.forName("org.postgresql.Driver").newInstance();
 
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", "cs421g36", "[0mp421group36");
            //Do something with the Connection
            menu:
            while(true){
                System.out.println("\n");
                System.out.println("Menu:\n"
                                 + "1.Insert a store\n"
                                 + "2.Increment salary\n"
                                 + "3.Return member basic information by email\n"
                                 + "4.Delete all information related to one member by email\n"
                                 + "5.Update membership type by reward points\n"
                                 + "0.Exit\n");
                System.out.println("Please select an option:");
                switch (scanInt()){
                    case 1: String insertsqlString = "INSERT INTO PLACE" 
                                    +"(PLID, PLNAME, PHONE, LOCATION, POSTAL_CODE, OPENING_HOURS) VALUES"
                                    +"(?,?,?,?,?,?)";
                            PreparedStatement pstmt = conn.prepareStatement(insertsqlString);
                            String fd = "";
                            while(true){
                                System.out.println("10 digits place id (store 0+):");
                                
                                fd = scanString();
                                //check empty input
                                if (fd.isEmpty()) 
                                {
                                    System.out.println("Invalid input!");
                                }
                                else break;
                            }
                            char first_digit_arg1 = fd.charAt(0);
                            //check store
                            if (first_digit_arg1 == '0'){
                                pstmt.setString(1,fd);
                                System.out.println("Place name:");
                                pstmt.setString(2,scanString());
                                System.out.println("Phone number:");
                                pstmt.setString(3,scanString());
                                System.out.println("Location:");
                                pstmt.setString(4,scanString());
                                System.out.println("Postal code:");
                                pstmt.setString(5,scanString());
                                System.out.println("Opening hours:");
                                pstmt.setString(6,scanString());
                                //insert into place first
                                pstmt.executeUpdate();

                                String insertsql = "";
                                PreparedStatement pstmt1 = null;
                                
                                insertsql = "INSERT INTO STORE"
                                    + "(PLID, SALES_REVENUE, SERVICES) VALUES"
                                    +"(?,?,?)";
                                pstmt1 = conn.prepareStatement(insertsql);
                                pstmt1.setString(1,fd);
                                System.out.println("Sales revenue:");
                                pstmt1.setDouble(2,scanDouble());
                                System.out.println("Services:");
                                pstmt1.setString(3,scanString());
                                //insert into store
                                pstmt1.executeUpdate();
                                System.out.println("Inserted into STORE successfully!");
                            }
                            else System.out.println("Invalid store ID.");
                            break;
                    
                    case 2: String insertsql1 = "UPDATE staff"
                                    + " SET  SALARY = SALARY + ? "
                                    + " WHERE SALARY < ? AND WORKING_HOURS > ?";
                            PreparedStatement pstmt2 = conn.prepareStatement(insertsql1);
                            System.out.println("Salary you want to increase:");
                            pstmt2.setInt(1,scanInt());
                            System.out.println("Upper bound of current salary:");
                            pstmt2.setInt(2,scanInt());
                            System.out.println("Lower bound of working hours:");
                            pstmt2.setInt(3,scanInt());
                            int n = pstmt2.executeUpdate();
                            System.out.println(n + " tuples are updated!");
                            break;
                    
                    case 3: String insertsql3 = "SELECT mid,phone,mfname,mlname,gender,birthday,membership "
                                             +"FROM MEMBER WHERE EMAIL =? ";
                            PreparedStatement pstmt3 = conn.prepareStatement(insertsql3);
                            System.out.println("Please enter member email address: ");
                            pstmt3.setString(1,scanString());
                            ResultSet rs = pstmt3.executeQuery();
                            int flag = 0;
                            while(rs.next()){
                                flag = 1;
                                String mid = rs.getString("mid");
                                String phone = rs.getString("phone");
                                String mfname = rs.getString("mfname");
                                String mlname = rs.getString("mlname");
                                String gender = rs.getString("gender");
                                Timestamp birthday = rs.getTimestamp("birthday");
                                String membership = rs.getString("membership");
                                System.out.println("Mid: " + mid + "\n"
                                                   + "Name: " + mfname + " " + mlname + "\n"
                                                   + "Gender: " + gender + "\n"
                                                   + "Birthday: " + birthday + "\n"
                                                   + "Phone: " + phone + "\n"
                                                   + "Membership type: " +membership+ "\n");
                                }
                            //check invalid email
                            if (flag == 1){
                            }
                            else System.out.println("Nothing found in database.");
                            break;

                    case 4: String selectsql1 = "SELECT MID FROM MEMBER WHERE EMAIL = ?";
                            PreparedStatement pstmt4 = conn.prepareStatement(selectsql1);
                            System.out.println("Please enter member email address: ");
                            pstmt4.setString(1,scanString());
                            ResultSet rsmid = pstmt4.executeQuery();
                            while(rsmid.next()){
                                String mid_delete = rsmid.getString("mid");
                                //delete from wishlist
                                String selectsql2 = "DELETE FROM WISHLIST WHERE MID = ?";
                                PreparedStatement pstmt41 = conn.prepareStatement(selectsql2);
                                pstmt41.setString(1,mid_delete);
                                pstmt41.executeUpdate();
                                //delete from contain due to foreign key
                                String deletesql = "DELETE FROM CONTAIN WHERE ORDER_NUMBER IN" 
                                        + "(SELECT ORDER_NUMBER FROM PAYMENT_HISTORY WHERE MID = ?)";
                                PreparedStatement pstmt42 = conn.prepareStatement(deletesql);
                                pstmt42.setString(1,mid_delete);
                                pstmt42.executeUpdate();
                                //delete from payment history
                                String selectsql4 = "DELETE FROM PAYMENT_HISTORY WHERE MID = ?";
                                PreparedStatement pstmt43 = conn.prepareStatement(selectsql4);
                                pstmt43.setString(1,mid_delete);
                                pstmt43.executeUpdate();
                                //delete from review
                                String deletesql2 = "DELETE FROM REVIEW WHERE MID = ?";
                                PreparedStatement pstmt44 = conn.prepareStatement(deletesql2);
                                pstmt44.setString(1,mid_delete);
                                pstmt44.executeUpdate();
                                //last, delete from member due to foreign key constraint
                                String selectsql3 = "DELETE FROM MEMBER WHERE MID = ?";
                                PreparedStatement pstmt45 = conn.prepareStatement(selectsql3);
                                pstmt45.setString(1,mid_delete);
                                int i = pstmt45.executeUpdate();
                                
                                if(i==1){
                                    System.out.println("Member deleted successfully!");
                                }
                            }
                            break;

                    case 5: String sql5 = " SELECT MID, MEMBERSHIP "
                                    + " FROM MEMBER WHERE REWARD_POINTS < ?";
                            PreparedStatement pstmt5 = conn.prepareStatement(sql5);
                            System.out.println("Please enter the minimal REWARD_POINTS: ");
                            pstmt5.setInt(1,scanInt());
                            ResultSet rs5 = pstmt5.executeQuery();
                            int p = 0;//counter

                            while(rs5.next()){
                                String mid5 = rs5.getString("MID");
                                String membership5 = rs5.getString("MEMBERSHIP");
                                //System.out.println(membership5);

                                if (membership5 == null){}
                                else if (membership5.equals("BEAUTY INSIDER")){
                                    // String sql6 = " UPDATE MEMBER SET MEMBERSHIP = NULL WHERE MID = ?";
                                    // PreparedStatement pstmt8 = conn.prepareStatement(sql6);
                                    // pstmt8.setString(1,mid5);
                                    // int i = pstmt8.executeUpdate();
                                    // p = p + i;
                                    }
                                else if (membership5.equals("VIB")){
                                    String sql6 = " UPDATE MEMBER SET MEMBERSHIP = 'BEAUTY INSIDER' WHERE MID = ?";
                                    PreparedStatement pstmt6 = conn.prepareStatement(sql6);
                                    pstmt6.setString(1,mid5);
                                    int i = pstmt6.executeUpdate();
                                    p = p + i;
                                    }
                                else if (membership5.equals("VIB ROUGE")){
                                    String sql6 = " UPDATE MEMBER SET MEMBERSHIP = 'VIB' WHERE MID = ?";
                                    PreparedStatement pstmt7 = conn.prepareStatement(sql6);
                                    pstmt7.setString(1,mid5);
                                    int i = pstmt7.executeUpdate();
                                    p = p + i;
                                    }
                                }
                            System.out.println(p + " members are updated");
                            break;  
                    case 0: System.out.println("Exiting...");
                            break menu;
                }
            }

        } catch (ClassNotFoundException e) {
            //Cannot register postgresql MySQL driver
            System.out.println("This is something you have not add in postgresql library to classpath!");
            e.printStackTrace();
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally{
            //After using connection, release the postgresql resource.
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    public static int scanInt(){
        Scanner keyboard = new Scanner(System.in);
        return  keyboard.nextInt();
    }

    public static double scanDouble(){
        Scanner keyboard = new Scanner(System.in);
        return keyboard.nextDouble();
    }

    public static String scanString(){
        Scanner keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }
}