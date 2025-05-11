
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Hotel_Management_system {
    private static final String url="jdbc:mysql://localhost:3306/hoteldb";
    private static final String username= "root";
    private static final String password= "rounak";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

        try {
            Connection con= DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL Supervison SYSTEM");
                Scanner scanner=new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice=scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(con, scanner);
                        break;
                    case 2:
                        viewReservation(con);
                        break;
                    case 3:
                        getRoom(con, scanner);
                        break;
                    case 4:
                        update(con, scanner);
                        break;
                    case 5:
                        delete(con, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again!!");;
                }

            }
        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);//explicitly throwing
        }
    }

    private static void reserveRoom(Connection con, Scanner scanner){
        try {
            System.out.println("Enter guest name: ");
            String guest_name=scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int room_number=scanner.nextInt();
            System.out.println("Enter contact number: ");
            String phone_number=scanner.next();

            String query = "INSERT INTO reservation (name, roomNo, phoneNo) " +
                    "VALUES ('" + guest_name + "', " + room_number + ", '" + phone_number + "')";
            
            try(Statement stat = con.createStatement()){
                int rowsAffected=stat.executeUpdate(query);

                if(rowsAffected > 0){
                    System.out.println("Reservation successful!");
                }else{
                    System.out.println("Reservation failed!");
                }
            }
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection con) throws SQLException{
        String query="select * from reservation;";
        
        try(Statement stat = con.createStatement();
            ResultSet result=stat.executeQuery(query)){

            System.out.println("Current Reservations: ");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while(result.next()){
                int id=result.getInt("id");
                String name=result.getString("name");
                int room=result.getInt("roomNo");
                String phone=result.getString("phoneNo");
                String date=result.getTimestamp("reservationDate").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s |\n", id,name,room,phone,date);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

        }  
    }

    private static void getRoom(Connection con, Scanner scanner){
        try{
            System.out.println("Enter reservation ID: ");
            int id=scanner.nextInt();
            System.out.println("Enter guest name: ");
            String name=scanner.next();

            String query = "SELECT roomNo FROM reservation " +
            "WHERE id = " + id +
            " AND name = '" + name + "'";
            try(Statement stat=con.createStatement();
               ResultSet result=stat.executeQuery(query)){

                if(result.next()){
                    int room=result.getInt("roomNo");
                    System.out.println("Room Number for Reservation ID "+id+ " and Guest "+name+" is: " + room);
                }
                else{
                    System.out.println("Reservation not found!!!");
                }
            }
            }catch (SQLException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        private static void update(Connection con,Scanner scanner){
            try{
                System.out.println("Enter reservation ID to update: ");
                int id=scanner.nextInt();
                scanner.nextLine();

                if(!reservationExits(con , id)){
                    System.out.println("Reservation not found!");
                    return;

                }
                System.out.println("Enter new guest name: ");
                String name=scanner.nextLine();
                System.out.println("Enter new Room number: ");
                int num=scanner.nextInt();
                System.out.println("Enter new phone number: ");
                String phone=scanner.next();

                String query = "UPDATE reservation SET name = '" + name + "', " +
                    "roomNo = " + num + ", " +
                    "phoneNo = '" + phone + "' " +
                    "WHERE id = " + id;

                try(Statement stat=con.createStatement()){
                    int rowsAffected=stat.executeUpdate(query);

                    if(rowsAffected > 0){
                        System.out.println("Reservation updated successfully!");
                    }
                    else{
                        System.out.println("Reservation update failed!");
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

        private static void delete(Connection con, Scanner scanner){
            try{
                System.out.println("Enter reservation ID: ");
                int id=scanner.nextInt();

                if(!reservationExits(con , id)){
                    System.out.println("Reservation not found!");
                    return;
                }
                String query ="delete from reservation where id =" +id;
                try(Statement stat=con.createStatement()){
                    int rowsAffected=stat.executeUpdate(query);

                    if(rowsAffected > 0){
                        System.out.println("Reservation deleted successfully!");
                    }
                    else{
                        System.out.println("Reservation deletion failed!");
                    }
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }

        private static  boolean reservationExits(Connection con, int id){
            try{
                 String query="select id from reservation where id = " +id;

                 try(Statement stat=con.createStatement();
                     ResultSet result=stat.executeQuery(query)){
                    
                        return result.next();
                     }
            }
            catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }

        public static void exit() throws InterruptedException{
            System.out.print("Exiting System");
            int i=5;
            while( i != 0){
                System.out.print(".");
                Thread.sleep(450); 
                i--;
            }
            System.out.println();
            System.out.println("Thank You for using Hotel Reservation System!!!!");
        }
}