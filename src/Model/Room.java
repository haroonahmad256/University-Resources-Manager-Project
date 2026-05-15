package Model;

import Database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Room {
    private String ID;
    private String roomNumber;
    private int capacity;
    private boolean isLab;

    public Room(){
    }

    public Room(String ID, String roomNumber, int capacity, boolean isLab){
        this.ID= ID;
        this.roomNumber= roomNumber;
        this.capacity= capacity;
        this.isLab= isLab;
    }

    String getID(){
        return this.ID;
    }
    String getRoomNumber(){
        return this.roomNumber;
    }
    int getCapacity(){
        return this.capacity;
    }
    boolean getIsLab(){
        return this.isLab;
    }
    void setID(String ID){
        this.ID= ID;
    }
    void setRoomNumber(String roomNumber){
        this.roomNumber= roomNumber;
    }
    void setCapacity(int capacity){
        this.capacity= capacity;
    }
    void setLab(boolean isLab){
        this.isLab= isLab;
    }
    DBConnection connection;

    public void showRoomDataInGUITable(JTable table){
        connection= new DBConnection();
        String sqlQuery = "SELECT * FROM rooms";
        String[] columnsNames= {"Room ID", "Room Name", "Room Capacity", "Is Lab"};
        DefaultTableModel model= new DefaultTableModel(columnsNames, 0);

        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null,"No Room Exist in Database! Please add a room data");
                return;
            }
            do{
                this.ID= resultSet.getString("roomId");
                this.roomNumber= resultSet.getString("roomNumber");
                this.capacity= resultSet.getInt("capacity");
                this.isLab= resultSet.getBoolean("is_Lab");

                Object[] rows= {this.ID, this.roomNumber, this.capacity, this.isLab};
                model.addRow(rows);
            }while(resultSet.next());
            table.setModel(model);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void totalRooms(JLabel label){
        connection= new DBConnection();
        String sqlQuery= "Select count(roomId) FROM rooms";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            ResultSet resultSet= statement.executeQuery();
            resultSet.next();
            int count= resultSet.getInt(1);
            label.setText(String.valueOf(count));
            System.out.println("Success || Total Teachers: "+count);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addRoom(JTextField roomId, JTextField roomName, JSpinner capacity, JComboBox<Boolean> isLab){
        int rowsAffected;
        connection= new DBConnection();
        String sqlQuery="INSERT INTO rooms (roomId, roomNumber, capacity, is_Lab) VALUES (?, ?, ?, ?)";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, roomId.getText());
            statement.setString(2, roomName.getText());
            statement.setInt(3, (Integer) capacity.getValue());
            statement.setBoolean(4,Boolean.parseBoolean(String.valueOf(isLab.getSelectedItem())));
            rowsAffected= statement.executeUpdate();
            if(rowsAffected<1){
                JOptionPane.showMessageDialog(null, "Couldn't Add Room! Please Try Again");
                return;
            }
            JOptionPane.showMessageDialog(null, "Room Added Successfully");
            System.out.println("Successfull || rows Affected: "+rowsAffected);
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void FetchRoomInfo(JTextField roomId, JTextField roomName, JSpinner capacity, JComboBox<String> isLab){
        this.ID= roomId.getText();
        connection= new DBConnection();
        String sqlQuery= "Select * FROM rooms WHERE roomId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, this.ID);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "Invalid Room ID, no Room found! Please enter valid room ID");
                return;
            }

            roomName.setText(resultSet.getString("roomNumber"));
            capacity.setValue(resultSet.getInt("capacity"));
            isLab.setSelectedItem(resultSet.getInt("is_Lab"));
            System.out.println("All value Fetched");
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void updateRoomInfo(JTextField roomId, JTextField roomName, JSpinner capacity, JComboBox<String> isLab){
        connection= new DBConnection();
        this.ID=roomId.getText();
        int rowsAffected;
        String updateNameSqlQuery = "UPDATE rooms SET roomNumber=?, capacity=?, is_Lab=? WHERE roomId=?";
        try(PreparedStatement statementUpdate = connection.getDatabaseConnection().prepareStatement(updateNameSqlQuery))
        {
            statementUpdate.setString(1, roomName.getText());
            statementUpdate.setInt(2, (Integer) capacity.getValue());
            statementUpdate.setBoolean(3,Boolean.parseBoolean(String.valueOf(isLab.getSelectedItem())));
            statementUpdate.setString(4, this.ID);
            rowsAffected= statementUpdate.executeUpdate();
            if(rowsAffected<1){
                JOptionPane.showMessageDialog(null, "Couldn't Update Information! Try Again");
                System.out.println("Rows Affected: "+rowsAffected);
            }
            else{
                System.out.println("All Done || Rows Affected: "+rowsAffected);
                JOptionPane.showMessageDialog(null, "Room Updated Successfully");
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void fetchDeleteRoomInfo(JTextField roomId, JLabel RoomName, JLabel RoomCapacity, JLabel status, JLabel warning){
        connection= new DBConnection();
        String sqlQuery= "SELECT * FROM rooms WHERE roomId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, roomId.getText());
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "Invalid Room ID, no Room found! Please enter valid room ID");
                return;
            }

            status.setText("Room Found");
            status.setForeground(Color.green);
            RoomName.setText("Room Name: "+resultSet.getString("roomNumber"));
            RoomCapacity.setText("Capacity: "+resultSet.getString("capacity"));
            warning.setText("Are you sure you want to delete Room? This Action can not be undone...");
            System.out.println("All done");
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteRoom(JTextField RID){
        connection= new DBConnection();
        int rowsAffected;
        String sqlQuery= "DELETE FROM rooms WHERE roomId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, RID.getText());
            rowsAffected = statement.executeUpdate();
            if(rowsAffected<1){
                JOptionPane.showMessageDialog(null, "Room couldn't be deleted! Please Try Again");
            }
            else{
                JOptionPane.showMessageDialog(null, "Room Data Deleted");
                System.out.println("Success || rows Affected: "+rowsAffected);
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public boolean searchRoom(JTextField search, JLabel roomID, JLabel roomNumber, JLabel capacity, JLabel isLab){
        connection = new DBConnection();
        String sqlQuery= "SELECT * FROM rooms WHERE roomId= ? OR roomNumber=?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, search.getText());
            statement.setString(2, search.getText());
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "Room doesn't exist. Please enter a valid id");
                return false;
            }
            else{
                this.roomNumber= resultSet.getString("roomNumber");
                this.capacity= resultSet.getInt("capacity");
                this.isLab= resultSet.getBoolean("is_Lab");
                this.ID= resultSet.getString("roomId");
                roomID.setText(this.ID);
                roomNumber.setText(this.roomNumber);
                capacity.setText(String.valueOf(this.capacity));
                isLab.setText(String.valueOf(this.isLab));
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return true;
    }

    public boolean roomManage(String batchId, String room){
        int strength, capacity;
        boolean isExceeding = false;
        connection= new DBConnection();
        String sqlQueryBatch= "Select * from batches where bactch_id= ?";
        String sqlQueryRoom= "Select * from rooms where roomId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQueryBatch);
            PreparedStatement statementRoom = connection.getDatabaseConnection().prepareStatement(sqlQueryRoom)){

            statement.setString(1, batchId);
            statementRoom.setString(1, room);
            ResultSet resultSet1= statement.executeQuery();
            ResultSet resultSet2= statementRoom.executeQuery();
            resultSet1.next();
            resultSet2.next();
            strength= resultSet1.getInt("strength");
            capacity= resultSet2.getInt("capacity");

            if(strength>capacity){
                isExceeding= true;
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return isExceeding;
    }
}
