/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library_hello;

import DButils.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class LibraryController implements Initializable {
    
    int fine=0;
    String MemberID;
    
    @FXML
    private StackPane stackpane;
    @FXML
    private TextField BookInputID;
    @FXML
    private Label bookname;
    @FXML
    private Label bookauthor;
    @FXML
    private TextField memberID;
    @FXML
    private Label membercontact;
    @FXML
    private Label membername;
    @FXML
    private TextField BOOKID;
    @FXML
    private ListView<String> listview;
    
    public ObservableList<String> issueData;
    
    
    private ConnectDB connectDB;
    //private Object response;
    
    //Boolean isReadyForSubmission = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectDB = new ConnectDB();
    }   
    
    @FXML
    private void addBook(ActionEvent event) throws IOException {
       loadWindow("/Addbook/AddBook.fxml","Add Book","/img/member-add-on-300x300.png");
    }
        
    @FXML
    private void addMember(ActionEvent event) throws IOException {
        
        loadWindow("/AddMember/AddMember.fxml","Add Member","/img/member-add-on-300x300.png");
    }
    
    @FXML
    private void listBook(ActionEvent event) throws IOException {
        loadWindow("/BookList/BookList.fxml","Book List","/img/member-add-on-300x300.png");
    }
    
    @FXML
    private void MemberList(ActionEvent event) throws IOException {
        loadWindow("/MemberList/MemberList.fxml","Member List","/img/member-add-on-300x300.png");
    }
    
    public void loadWindow(String location,String title, String iconPath) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource(location));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(title);
        Image icon = new Image(getClass().getResourceAsStream(iconPath));
        stage.getIcons().add(icon);
        stage.setScene(new Scene(root));
        stage.show();
    }

        @FXML
    private void searchWindow(ActionEvent event) throws IOException {
        loadWindow("/searchWindow/searchWindow.fxml","Member List","/img/member-add-on-300x300.png");
    }
    @FXML
    public void bookIDenter(ActionEvent event) throws SQLException{
        String id = BookInputID.getText();
        String sql = "SELECT * FROM tbl_addbook WHERE id = '" + id +"'";
        Connection conn = ConnectDB.getConnections();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rst = pst.executeQuery();
        
        boolean flag=false;
        while(rst.next()) {
            String bName = rst.getString("title");
            String bAuthor = rst.getString("author");
            
            bookname.setText(bName);
            bookauthor.setText(bAuthor);
            flag = true;
        }
        if(!flag){
            bookname.setText("No such book is available");
            bookauthor.setText("No such author is available");
        }
    }
    
    @FXML
    public void memberIDenter(ActionEvent event) throws SQLException{
        String id = memberID.getText();
        String sql = "SELECT * FROM tbl_addmember WHERE id = '" + id +"'";
        Connection conn = ConnectDB.getConnections();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rst = pst.executeQuery();
        
        boolean flag=false;
        while(rst.next()) {
            String bName = rst.getString("name");
            String bEmail = rst.getString("email");
            
            membername.setText(bName);
            membercontact.setText(bEmail);
            flag = true;
        }
        if(!flag){
            membername.setText("No such name is available");
            membercontact.setText("No such contact is available");
        }
    }
    
    @FXML
    public void issuebook(ActionEvent event) throws SQLException{
        String bookID = BookInputID.getText();
        String memberInput = memberID.getText();
        if(bookID.isEmpty() || memberInput.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Fields must not be empty");
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("confirm issue operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to issue the book?" + bookname.getText() + "\n To" + membername.getText());
        Optional<ButtonType> response = alert.showAndWait();
        
        if(response.get() == ButtonType.OK)
        {
            String sql = "INSERT INTO tbl_issue (bookID,memberInput) VALUES(?,?)";
            Connection conn = ConnectDB.getConnections();
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, bookID);
            pst.setString(2, memberInput);
            pst.execute();
            
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setHeaderText(null);
            alert1.setContentText("Book issued successfully to" + membername.getText());
            alert1.showAndWait();
        }
        else
        {
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setHeaderText(null);
            alert1.setContentText("FAILED");
            alert1.showAndWait();
        }
    }
    
    @FXML
    public void loadInfo(ActionEvent event) throws SQLException{
        issueData = FXCollections.observableArrayList();
        String id = BOOKID.getText();
        String sql = "SELECT * FROM tbl_issue WHERE bookID = '" + id + "'";
        Connection conn = ConnectDB.getConnections();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rst = pst.executeQuery();
        
        while (rst.next()) {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            String BookID = id;
            MemberID = rst.getString("memberInput");
            String issueTime = rst.getString("issueTime");
            int renew_count = rst.getInt("renew_count");
            String curr_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            
            try {
                Date date1 = myFormat.parse(issueTime);
                Date date2 = myFormat.parse(curr_time);
                long diff = (date2.getTime() - date1.getTime())/86400000;
                if(diff > 15)
                {
                    fine=fine+15;
                }
            } 
            catch (ParseException e) {
                e.printStackTrace();
            }
            
            issueData.add("Issued Information: ");
            issueData.add("Issued Time and Date: " + issueTime);
            issueData.add("Renew Count: " + renew_count);

            issueData.add("Book Information: ");
            String sql2 = "SELECT * FROM tbl_addbook WHERE id = '" + BookID + "'";
            Connection con2 = ConnectDB.getConnections();
            PreparedStatement pst2 = con2.prepareStatement(sql2);
            ResultSet rst2 = pst2.executeQuery();
            while (rst2.next()) {
                issueData.add("Book Name: " + rst2.getString("title"));
                issueData.add("Book ID: " + rst2.getString("id"));
                issueData.add("Book Name: " + rst2.getString("author"));
                issueData.add("Book Publisher: " + rst2.getString("publisher"));

            }
            issueData.add("FINE: Rs." +fine);
            String sql3 = "SELECT * FROM tbl_addmember WHERE id = '" + MemberID + "'";
            Connection con3 = ConnectDB.getConnections();
            PreparedStatement pst3 = con3.prepareStatement(sql3);
            ResultSet rst3 = pst3.executeQuery();
            while (rst3.next()) {
                issueData.add("Member Name: " + rst3.getString("name"));
                issueData.add("Member ID: " + rst3.getString("id"));
                issueData.add("Member Contact Number: " + rst3.getString("mobile"));
                issueData.add("Member Email: " + rst3.getString("email"));
                issueData.add("Due: " +rst3.getString("due"));
            }
            listview.getItems().setAll(issueData);
        }
    }
       
    @FXML
    void memberDetails(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/MemberDetails/MemberDetails.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("All members");
        stage.setScene(new Scene(root));
        stage.show();
    }
            
    @FXML
    public void submitBook(ActionEvent event) throws SQLException{
        
        String id = BOOKID.getText();
        if(id.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Fields must not be empty");
            alert.showAndWait();
            return;
        }
        
        Alert alert2 =  new Alert(Alert.AlertType.CONFIRMATION);
        alert2.setTitle("Confirm fine submission");
        alert2.setHeaderText(null);
        alert2.setContentText("Did the issuer submit the fine?");
        Optional<ButtonType> response = alert2.showAndWait();
        
        if(response.get() == ButtonType.OK)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("confirm submit operation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to submit the book?");
            Optional<ButtonType> response1 = alert.showAndWait();
        
            if(response.get() == ButtonType.OK)
            {
        
                String sql = "DELETE FROM tbl_issue WHERE bookID = ?";
                Connection conn = ConnectDB.getConnections();
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1,id);
                pst.executeUpdate();
                pst.close();
            
            
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setHeaderText(null);
            alert1.setContentText("Book submitted successfully");
            alert1.showAndWait();
            
            }
        
        }
        else if(response.get() == ButtonType.CANCEL)
        {
            fine=0;
            String sql = "UPDATE tbl_addmember SET due=due+15 WHERE id = '" + MemberID + "'";
            Connection conn = ConnectDB.getConnections();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.executeUpdate();
            pst.close();
            
            
        }
    }
        
    @FXML
    public void renewButton(ActionEvent event) throws SQLException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("confirm renew operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to renew the book?");
        Optional<ButtonType> response = alert.showAndWait();
        if(response.get() == ButtonType.OK)
        {
            String ac = "UPDATE tbl_issue SET issueTime = CURRENT_TIMESTAMP,renew_count=renew_count+1 WHERE bookID = '" + BOOKID.getText() + "'";
            Connection conn = ConnectDB.getConnections();
            PreparedStatement pst = conn.prepareStatement(ac);
            
            pst.executeUpdate();
            pst.close();
            
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setHeaderText(null);
            alert1.setContentText("Book renewed successfully");
            alert1.showAndWait();
        }
    }
}
 
    

    

